package taskaya.backend.repository.community;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.community.Vote;
import taskaya.backend.entity.work.Contract;

import java.util.List;
import java.util.Optional;


@Repository
public interface CommunityVoteRepository extends JpaRepository<Vote, Integer> {
    List<Vote> findAllByContract(Contract contract);
    Optional<Vote> findByContractAndCommunityMember(Contract contract, CommunityMember communityMember);
}
