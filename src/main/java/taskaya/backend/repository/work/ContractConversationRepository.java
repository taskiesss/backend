package taskaya.backend.repository.work;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import taskaya.backend.entity.work.ContractConversation;

import java.util.UUID;

public interface ContractConversationRepository extends JpaRepository<ContractConversation, UUID> {
    Page<ContractConversation> findByContractIdOrderByCreatedAtDesc(UUID contractId, Pageable pageable);
}
