package taskaya.backend.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import taskaya.backend.entity.work.Proposal;

import java.util.List;
import java.util.UUID;

public class ProposalSpecification {
    public static Specification<Proposal> searchProposal(String search,
                                                         List<Proposal.ProposalStatus>proposalStatus,
                                                         UUID workerEntityId,
                                                         UUID clientId,
                                                         UUID jobId) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (search != null && !search.isEmpty()) {
                String searchRegExp = "%" + search.toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("job").get("title")), searchRegExp);

                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or( titlePredicate));

            }
            if (proposalStatus != null && !proposalStatus.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, root.get("status").in(proposalStatus));
            }
            if (workerEntityId != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("workerEntity").get("id"), workerEntityId));
            }
            if (clientId != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("client").get("id"), clientId));
            }

            if (jobId != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("job").get("uuid"), jobId));
            }

            return predicate;
        };
    }
}
