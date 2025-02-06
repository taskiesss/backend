package taskaya.backend.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.enums.ExperienceLevel;

import java.util.List;

public class CommunitySpecification {

    public static Specification<Community> searchCommunities(String search, List<Skill> skills,
                                                             ExperienceLevel experienceLevel,
                                                             float hourlyRateMin, float hourlyRateMax,
                                                             float rate , Boolean isFull) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Search by name or description
            if (search != null && !search.isEmpty()) {
                String searchRegExp = "%" + search.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("communityName")), searchRegExp);
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchRegExp);

                // Combine the predicates for name, title, or description
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            // Search by skills
            if (skills != null && !skills.isEmpty()) {
                Join<Community, Skill> skillJoin = root.join("skills");
                predicate = criteriaBuilder.and(predicate, skillJoin.in(skills));
            }

            // Search by experience level
            if (experienceLevel != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("experienceLevel"), experienceLevel));
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

            //search by is full
            if (isFull != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("isFull"), isFull));
            }

            return predicate;
        };
    }
}
