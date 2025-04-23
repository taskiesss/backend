package taskaya.backend.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.payments.responses.PaymentSearchResponseMapper;
import taskaya.backend.DTO.payments.responses.PaymentSearchResponseDTO;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.Payment;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.client.ClientBalance;
import taskaya.backend.entity.client.ClientBusiness;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerBalance;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.ContractContributor;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.repository.PaymentRepository;
import taskaya.backend.repository.client.ClientBalanceRepository;
import taskaya.backend.repository.client.ClientBusinessRepository;
import taskaya.backend.repository.freelancer.FreelancerBalanceRepository;
import taskaya.backend.services.community.CommunityService;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.specifications.PaymentSpecification;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CommunityService communityService;
    @Autowired
    private FreelancerService freelancerService;

    @Autowired
    private ClientBusinessRepository clientBusinessRepository;
    @Autowired
    ClientBalanceRepository clientBalanceRepository;
    @Autowired
    private FreelancerBalanceRepository freelancerBalanceRepository;



    public Page<PaymentSearchResponseDTO> findPayments(User user ,  Date startDate, Date endDate, Payment.Type type, int page, int size) {
        var specification = PaymentSpecification.searchPayment(user, user, startDate, endDate, type);
        Sort sort= Sort.by(Sort.Order.desc("date"));
        Pageable pageable = PageRequest.of(page, size,sort);
        Page <Payment> paymentPage = paymentRepository.findAll(specification, pageable);
        return PaymentSearchResponseMapper.toDTOPage(paymentPage,user);
    }


    @Transactional
    public double payForCommunityContract(Contract contract , List<Milestone> milestones) {

        Community community = communityService.getCommunityByWorkerEntity(contract.getWorkerEntity());
        double totalValue = getPaymentValue(contract, milestones);

        // Create a new payment for the client
        Client client = contract.getClient();
        Payment clientPayment = Payment.builder()
                .sender(client.getUser())
                .community(community)
                .contract(contract)
                .amount(totalValue)
                .type(Payment.Type.TRANSACTION)
                .build();

        ClientBalance clientBalance = client.getBalance();
        clientBalance.setRestricted(clientBalance.getRestricted() - totalValue);

        ClientBusiness clientBusiness = client.getClientBusiness();
        clientBusiness.setTotalSpent(totalValue+clientBusiness.getTotalSpent());

        paymentRepository.save(clientPayment);
        clientBalanceRepository.save(clientBalance);
        clientBusinessRepository.save(clientBusiness);


        //transfer to freelancers
        //create payments for each community member for this job
        List<Payment> paymentsForCommunityMembers = new LinkedList<>();

        for (
                ContractContributor contractContributor :
                contract.getContractContributors()
        ) {
            double freelancerAmountBeforeCommission= (contractContributor.getPercentage()) * totalValue;
            double freelancerAmountAfterCommission = freelancerAmountBeforeCommission - freelancerAmountBeforeCommission * Constants.COMMISSION_PERCENTAGE;
            // Create a new payment for each freelancer
            paymentsForCommunityMembers.add(
                    Payment.builder()
                            .amount(freelancerAmountBeforeCommission)
                            .community(community)
                            .contract(contract)
                            .receiver(contractContributor.getFreelancer().getUser())
                            .type(Payment.Type.TRANSACTION)
                            .build()
            );

            //update the balance of the freelancer
            Freelancer freelancer = contractContributor.getFreelancer();
            FreelancerBalance freelancerBalance = freelancer.getBalance();

            freelancerBalance.setAvailable(
                    freelancer.getBalance().getAvailable() + freelancerAmountAfterCommission
            );
            freelancerBalance.setWorkInProgress(
                    freelancer.getBalance().getWorkInProgress() - freelancerAmountBeforeCommission
            );
            freelancerBalanceRepository.save(freelancerBalance);

        }
        paymentRepository.saveAll(paymentsForCommunityMembers);
        return totalValue;
    }


    public double payForFreelancerContract (Contract contract, List<Milestone>milestones){
        Freelancer freelancer = freelancerService
                .getFreelancerByWorkerEntity(contract.getWorkerEntity());
        double totalValue = getPaymentValue(contract, milestones);

        // Create a new payment for the client
        Client client = contract.getClient();
        Payment payment = Payment.builder()
                .sender(client.getUser())
                .receiver(freelancer.getUser())
                .contract(contract)
                .amount(totalValue)
                .type(Payment.Type.TRANSACTION)
                .build();

        ClientBalance clientBalance = client.getBalance();
        clientBalance.setRestricted(clientBalance.getRestricted() - totalValue);
        ClientBusiness clientBusiness = client.getClientBusiness();
        clientBusiness.setTotalSpent(totalValue+clientBusiness.getTotalSpent());


        paymentRepository.save(payment);
        clientBalanceRepository.save(clientBalance);
        clientBusinessRepository.save(clientBusiness);

        FreelancerBalance freelancerBalance = freelancer.getBalance();
        freelancerBalance.setAvailable(
                freelancerBalance.getAvailable() + (totalValue - totalValue*Constants.COMMISSION_PERCENTAGE)
        );
        freelancerBalance.setWorkInProgress(
                freelancerBalance.getWorkInProgress() - totalValue
        );

        freelancerBalanceRepository.save(freelancerBalance);

        return totalValue;
    }


    public double getPaymentValue(Contract contract , List<Milestone> milestones){
        double totalValue = 0.0;
        for (Milestone milestone : milestones) {
            totalValue+= milestone.getEstimatedHours() * contract.getCostPerHour();
        }
        return totalValue;
    }


//        public double getTaskCommision(double totalValue){
//            return totalValue * Constants.COMMISSION_PERCENTAGE;
//        }
}