package taskaya.backend.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import taskaya.backend.entity.Payment;
import taskaya.backend.entity.User;


import java.util.Date;

public class PaymentSpecification {

    public static Specification<Payment> searchPayment(User receiver, User sender, Date startDate, Date endDate, Payment.Type type) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (receiver != null) {
                Predicate receiverPredicate = criteriaBuilder.equal(root.get("receiver"), receiver);
                predicate = criteriaBuilder.or(predicate, receiverPredicate);
            }
            if (sender != null) {
                Predicate senderPredicate = criteriaBuilder.equal(root.get("sender"), sender);
                predicate = criteriaBuilder.or(predicate, senderPredicate);
            }

            if (startDate != null) {
                Predicate startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate);
                predicate = criteriaBuilder.and(predicate, startDatePredicate);
            }

            if (endDate != null) {
                Predicate endDatePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate);
                predicate = criteriaBuilder.and(predicate, endDatePredicate);
            }

            if (type != null) {
                Predicate typePredicate = criteriaBuilder.equal(root.get("type"), type);
                predicate = criteriaBuilder.and(predicate, typePredicate);
            }


            return predicate;
        };

    }
}