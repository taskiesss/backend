package taskaya.backend.services.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.contracts.requests.MyContractsPageRequestDTO;
import taskaya.backend.DTO.contracts.responses.MyContractsPageResponseDTO;
import taskaya.backend.DTO.freelancers.requests.FreenlancerSearchRequestDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
import taskaya.backend.DTO.mappers.MyContractsPageResponseMapper;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.enums.SortedByForContracts;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.specifications.ContractSpecification;
import taskaya.backend.specifications.FreelancerSpecification;

import java.util.Comparator;
import java.util.UUID;

@Service
public class ContractService {

    @Autowired
    ContractRepository contractRepository;



    public Page<MyContractsPageResponseDTO> searchContracts(MyContractsPageRequestDTO requestDTO ,
                                                              UUID workerEntityId ,UUID clientId) {

        // Create Specification based on request
        Specification<Contract> specification = ContractSpecification.searchContract(
                requestDTO.getSearch(),
                requestDTO.getContractStatus(),
                workerEntityId,
                clientId
        );

        Pageable pageable;

        if (requestDTO.getSortedBy() != null) {

            Sort sort;
            if (SortDirection.DESC.equals(requestDTO.getSortDirection())) {
                sort = Sort.by(Sort.Order.desc(requestDTO.getSortedBy().getValue().equals(SortedByForContracts.TITLE.getValue())?"job.title":requestDTO.getSortedBy().getValue()));
            } else {
                sort = Sort.by(Sort.Order.asc(requestDTO.getSortedBy().getValue().equals(SortedByForContracts.TITLE.getValue())?"job.title":requestDTO.getSortedBy().getValue()));
            }
            pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getSize(), sort);
        }else {
            pageable=PageRequest.of(requestDTO.getPage(), requestDTO.getSize());
        }
        Page<Contract> contractPage = contractRepository.findAll(specification, pageable);

        return MyContractsPageResponseMapper.toDTOPage(contractPage);
    }




    public static Double getContractBudget(Contract contract){
        Double totalEstimatedHours = 0D;
        for (Milestone milestone : contract.getMilestones()){
            totalEstimatedHours+=milestone.getEstimatedHours();
        }
        return totalEstimatedHours * contract.getCostPerHour();
    }

    public static Milestone getActiveMilestone (Contract contract){
        contract.getMilestones().sort(Comparator.comparing(Milestone::getNumber));
        for (Milestone milestone : contract.getMilestones()){
            if (milestone.getStatus().equals(Milestone.MilestoneStatus.IN_PROGRESS)){
                return milestone;
            }
        }
        return null;
    }
}
