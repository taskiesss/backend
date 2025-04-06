package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import taskaya.backend.DTO.communities.responses.CommunityAvailablePositionsResponseDTO;
import taskaya.backend.entity.community.CommunityMember;

import java.util.LinkedList;
import java.util.List;

public class    CommunityAvailablePositionsResponseMapper {
    public static CommunityAvailablePositionsResponseDTO toDTO(CommunityMember communityMember){
        return CommunityAvailablePositionsResponseDTO.builder()
                .id(communityMember.getId())
                .positionName(communityMember.getPositionName())
                .percentage(communityMember.getPositionPercent())
                .description(communityMember.getDescription())
                .build();
    }

    public static List<CommunityAvailablePositionsResponseDTO> toDTOList(List<CommunityMember> communityMembers) {
        List<CommunityAvailablePositionsResponseDTO> result = new LinkedList<>();
        for (CommunityMember communityMember : communityMembers) {
            result.add(toDTO(communityMember));
        }
        return result;
    }

    public static Page<CommunityAvailablePositionsResponseDTO> toDTOPage(Page<CommunityMember> communityMembers) {
        return new PageImpl<>(toDTOList(communityMembers.getContent()), communityMembers.getPageable(), communityMembers.getTotalElements());
    }
}
