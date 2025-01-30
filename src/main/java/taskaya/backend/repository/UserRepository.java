package taskaya.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.User;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User , UUID> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
