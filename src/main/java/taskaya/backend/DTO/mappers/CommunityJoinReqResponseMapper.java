package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.communities.responses.CommunityJoinReqResponseDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.JoinRequest;
import taskaya.backend.entity.freelancer.Freelancer;

import java.util.LinkedList;
import java.util.List;

@Component
public class CommunityJoinReqResponseMapper {
    public static CommunityJoinReqResponseDTO toDTO(JoinRequest joinRequest){
        Freelancer freelancer = joinRequest.getFreelancer();
        String nameOrUsername = (freelancer.getName()==null || freelancer.getName().isEmpty() )? freelancer.getUser().getUsername() : freelancer.getName();
        return CommunityJoinReqResponseDTO.builder()
                .freelancerID(freelancer.getId())
                .name(nameOrUsername)
                .profilePicture(freelancer.getProfilePicture())
                .positionName(joinRequest.getPosition().toString())
                .build();
    }

    public static List<CommunityJoinReqResponseDTO> toDTOList(List<JoinRequest> joinRequests){

        List<CommunityJoinReqResponseDTO> result = new LinkedList<>();
        for (JoinRequest joinRequest :joinRequests){
            result.add(toDTO(joinRequest));
        }
        return result;
    }

    public static Page<CommunityJoinReqResponseDTO> toDTOPage(Page<JoinRequest> joinRequests){
        return new PageImpl<>(toDTOList(joinRequests.getContent()), joinRequests.getPageable(), joinRequests.getTotalElements());
    }
}
