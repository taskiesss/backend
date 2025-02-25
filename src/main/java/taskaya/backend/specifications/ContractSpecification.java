package taskaya.backend.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.List;
import java.util.UUID;

//for all contract communities ,freelancers and clients
//just adjust the call from the services for every one
//example for freelancer get its worker entity and the set the client to null
public class ContractSpecification {
    public static Specification<Contract> searchContract(String search ,
                                                         List<Contract.ContractStatus>contractStatus ,
                                                         UUID workerEntityId,
                                                         UUID clientId){
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (search != null && !search.isEmpty())  {
                String searchRegExp ="%" + search.toLowerCase() + "%";
                Predicate usernamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("client").get("user").get("username")), searchRegExp);
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchRegExp);
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("job").get("title")), searchRegExp);

                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(usernamePredicate,namePredicate, titlePredicate));

            }

            if (contractStatus != null && !contractStatus.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, root.get("status").in(contractStatus));
            }

            if (workerEntityId != null){
                predicate =  criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("workerEntity").get("id"), workerEntityId));
            }

            if (clientId != null){
                predicate =  criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("client").get("id"), clientId));
            }
            

            return predicate;
        };
        }
}
