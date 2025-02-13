package taskaya.backend.specifications;

import org.springframework.data.jpa.domain.Specification;
import taskaya.backend.DTO.jobs.requests.JobSearchRequestDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Job;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class JobSpecification {

    public static Specification<Job> filterJobs(JobSearchRequestDTO request) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            // 🔹 Search by title or description
            if (request.getSearch() != null && !request.getSearch().isEmpty()) {
                String search = "%" + request.getSearch().toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), search);
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), search);
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(titlePredicate, descriptionPredicate));            }

            // 🔹 Filter by experience level
            if (request.getExperienceLevel() != null && !request.getExperienceLevel().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, root.get("experienceLevel").in(request.getExperienceLevel()));
            }

            // 🔹 Filter by project length
            if (request.getProjectLength() != null && !request.getProjectLength().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, root.get("projectLength").in(request.getProjectLength()));
            }

            // 🔹 Filter by hourly rate range
            if (request.getHourlyRateMin() > 0) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.greaterThanOrEqualTo(root.get("expectedCostPerHour"), request.getHourlyRateMin()));
            }
            if (request.getHourlyRateMax() > 0) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.lessThanOrEqualTo(root.get("expectedCostPerHour"), request.getHourlyRateMax()));
            }


            // 🔹 Filter by  rate pf the client
            if (request.getRate() > 0) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.greaterThanOrEqualTo(root.get("client").get("rate"), request.getRate()));
            }

            // 🔹 Filter by required skills
            if (request.getSkills() != null && !request.getSkills().isEmpty()) {
                Join<Freelancer, Skill> skillJoin = root.join("skills");
                predicate = criteriaBuilder.and(predicate,
                        skillJoin.in(request.getSkills()));
            }

            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(root.get("status"), Job.JobStatus.NOT_ASSIGNED)
            );
            return predicate;
        };
    }
}

