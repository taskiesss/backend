package taskaya.backend.services.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.contracts.responses.ContractConversationResponseDTO;
import taskaya.backend.DTO.mappers.ContractConvoResponseMapper;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.NotificationDest;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.ContractConversation;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ContractConversationRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.services.NotificationService;

import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContractConversationService {

    @Autowired
    private ContractConversationRepository contractConversationRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    ContractService contractService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @PreAuthorize("@jwtService.contractAuthAndActive(#contractId)")
    public Page<ContractConversationResponseDTO> getConversationsByContract(String contractId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContractConversation> myPage = contractConversationRepository.findByContractIdOrderByCreatedAtDesc(UUID.fromString(contractId), pageable);

        return myPage.map(convo -> {
            User user = convo.getUser();

            // Simple role check and fetch
            if (user.getRole().equals(User.Role.FREELANCER)) {
                Freelancer freelancer = freelancerRepository.findByUser(user)
                        .orElseThrow(()-> new RuntimeException("Freelancer not found!"));
                return ContractConvoResponseMapper.toDTO(convo, freelancer);

            } else if (user.getRole().equals(User.Role.CLIENT)) {
                Client client = clientRepository.findByUser(user)
                        .orElseThrow(()-> new RuntimeException("Client not found!"));
                return ContractConvoResponseMapper.toDTO(convo, client);

            } else {
                throw new IllegalStateException("Unknown user role: " + user.getRole());
            }
        });
    }

    @PreAuthorize("@jwtService.contractAuthAndActive(#contractId)")
    public void createConversation(String contractId, String content) {
        User user = jwtService.getUserFromToken();
        Contract contract = contractRepository.findById(UUID.fromString(contractId))
                .orElseThrow(() -> new RuntimeException("Contract not found!"));

        ContractConversation conversation = ContractConversation.builder()
                .user(user)
                .contract(contract)
                .content(content)
                .createdAt(new Date())
                .build();
        contractConversationRepository.save(conversation);

        LinkedList<User> contractUsers = contractService.
                getFreelancersFromContract(contract).stream().map(Freelancer::getUser)
                .collect(Collectors.toCollection(LinkedList::new)
                );
        NotificationDest dest = contract.getWorkerEntity().getType()== WorkerEntity.WorkerType.COMMUNITY? NotificationDest.CONTRACT_COMMUNITY: NotificationDest.CONTRACT;
        for (User contractUser : contractUsers) {
            if (!contractUser.getId().equals(user.getId())) {
                notificationService.newContractPostNotification(contract.getJob().getTitle(),contractUser,dest,contractId);
            }
        }

        if (!contract.getClient().getUser().getId().equals(user.getId())){
            notificationService.newContractPostNotification(contract.getJob().getTitle(),contract.getClient().getUser(),NotificationDest.CONTRACT,contractId);

        }
    }


}
