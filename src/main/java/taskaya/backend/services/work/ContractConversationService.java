package taskaya.backend.services.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.ContractConversation;
import taskaya.backend.repository.work.ContractConversationRepository;
import taskaya.backend.repository.work.ContractRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class ContractConversationService {

    @Autowired
    private ContractConversationRepository contractConversationRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private JwtService jwtService;

    @PreAuthorize("@jwtService.contractDetailsAuth(#id)")
    public Page<ContractConversation> getConversationsByContract(String contractId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return contractConversationRepository.findByContractIdOrderByCreatedAtDesc(UUID.fromString(contractId), pageable);
    }

    @PreAuthorize("@jwtService.contractDetailsAuth(#id)")
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
    }


}
