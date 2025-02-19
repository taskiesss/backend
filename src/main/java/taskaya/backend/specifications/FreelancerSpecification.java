package taskaya.backend.specifications;


import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.enums.ExperienceLevel;

import java.util.List;

public class FreelancerSpecification {

    public static Specification<Freelancer> searchFreelancers(String search, List<Skill> skills,
                                                              List<ExperienceLevel> experienceLevel,
                                                              float hourlyRateMin, float hourlyRateMax,
                                                              float rate) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Search by name
            if (search != null && !search.isEmpty())  {
                String searchRegExp ="%" + search.toLowerCase() + "%";
                Predicate usernamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("username")), searchRegExp);
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchRegExp);
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchRegExp);
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchRegExp);

                // Combine the predicates for name, title, or description
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(usernamePredicate,namePredicate, titlePredicate, descriptionPredicate));
            }

            // Search by skills
            if (skills != null && !skills.isEmpty()) {
                Join<Freelancer, Skill> skillJoin = root.join("skills");
                predicate = criteriaBuilder.and(predicate,
                        skillJoin.in(skills));
            }

            // Search by experience level
            if (experienceLevel != null && !experienceLevel.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, root.get("experienceLevel").in(experienceLevel));
            }

            // Search by hourly rate range
            if (hourlyRateMin > 0) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("pricePerHour"), hourlyRateMin));
            }
            if (hourlyRateMax > 0) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("pricePerHour"), hourlyRateMax));
            }

            // Search by rate
            if (rate > 0) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("rate"), rate));
            }

            return predicate;
        };
    }
}
