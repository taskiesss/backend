package taskaya.backend.services.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.community.Vote;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.repository.community.CommunityVoteRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.services.work.ContractService;

import java.util.LinkedList;
import java.util.List;

@Service
public class VoteService {
    ContractService contractService;
    @Autowired
    CommunityVoteRepository communityVoteRepository;

    public void setContractService(@Autowired ContractService contractService) {
        this.contractService = contractService;
    }


    public void createVotesForNewMember(CommunityMember communityMember , List<Contract> contracts){
        if (communityMember.getFreelancer() == null ){
            throw new IllegalArgumentException("position doesn't have freelancer");
        }
        List<Vote>votes = new LinkedList<>();

        for (Contract contract : contracts){

            if (contract.getStatus()!= Contract.ContractStatus.PENDING){
                throw new IllegalArgumentException("contract is not pending");
            }
            votes.add(Vote.builder()
                    .agreed(null)
                    .communityMember(communityMember)
                    .contract(contract)
                    .build());
        }
        communityVoteRepository.saveAll(votes);
    }

    public boolean isVoteDone(Contract contract){
        if (contract.getStatus() != Contract.ContractStatus.PENDING){
            throw new IllegalArgumentException("contract is not pending");
        }
        List<Vote> votes = communityVoteRepository.findAllByContract(contract);
        return votes.stream().allMatch(vote -> vote.getAgreed()!=null && vote.getAgreed());
    }


    public void createVotesForNewContract(Contract contract, List<CommunityMember> communityMembers){
        if (contract.getStatus() != Contract.ContractStatus.PENDING){
            throw new IllegalArgumentException("contract is not pending");
        }
        List<Vote>votes = new LinkedList<>();

        for (CommunityMember communityMember : communityMembers){
            if (communityMember.getFreelancer()!=null) {
                votes.add(Vote.builder()
                        .agreed(null)
                        .communityMember(communityMember)
                        .contract(contract)
                        .build());
            }
        }
        communityVoteRepository.saveAll(votes);
    }
}
