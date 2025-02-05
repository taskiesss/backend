package taskaya.backend.specifications;

import org.springframework.data.jpa.domain.Specification;
import taskaya.backend.DTO.search.jobs.JobSearchRequestDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.work.Job;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    public static Specification<Job> filterJobs(JobSearchRequestDTO request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 🔹 Search by title or description
            if (request.getSearch() != null && !request.getSearch().isEmpty()) {
                String search = "%" + request.getSearch().toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), search);
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), search);
                predicates.add(criteriaBuilder.or(titlePredicate, descriptionPredicate));
            }

            // 🔹 Filter by experience level
            if (request.getExperienceLevel() != null) {
                predicates.add(criteriaBuilder.equal(root.get("experienceLevel"), request.getExperienceLevel()));
            }

            // 🔹 Filter by project length
            if (request.getProjectLength() != null) {
                predicates.add(criteriaBuilder.equal(root.get("projectLength"), request.getProjectLength()));
            }

            // 🔹 Filter by hourly rate range
            if (request.getHourlyRateMin() > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expectedCostPerHour"), request.getHourlyRateMin()));
            }
            if (request.getHourlyRateMax() > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expectedCostPerHour"), request.getHourlyRateMax()));
            }

            // 🔹 Filter by required skills
            if (request.getSkills() != null && !request.getSkills().isEmpty()) {
                Join<Job, Skill> skillJoin = root.join("skills");
                predicates.add(skillJoin.in(request.getSkills()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

