package taskaya.backend.repository;

import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.Payment;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.community.Community;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> , JpaSpecificationExecutor<Payment> {
}
