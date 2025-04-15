package taskaya.backend.services.community;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.communities.communityMember.requests.CommunityMemberUpdateRequestDTO;
import taskaya.backend.DTO.communities.communityMember.responses.CommunityMemberSettingsResponseDTO;
import taskaya.backend.DTO.mappers.CommunityPositionAndRoleResponseMapper;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.community.CommunityMemberRepository;
import taskaya.backend.repository.community.CommunityRepository;

import java.util.List;
import java.util.UUID;


@Service
public class CommunityMemberService {
    @Autowired
    private CommunityMemberRepository communityMemberRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private JwtService jwtService;

    private CommunityService communityService;

    @Autowired
    public void setCommunityService(@Lazy CommunityService communityService) {
        this.communityService = communityService;
    }

    @Transactional
    public void saveMember(CommunityMember communityMember){
        communityMemberRepository.save(communityMember);
        if (!communityMember.getCommunity().getCommunityMembers().contains(communityMember)) {
            communityMember.getCommunity().getCommunityMembers().add(communityMember);
        }
        communityService.updateIsFull(communityMember.getCommunity());
    }

    public CommunityMember findById(Integer id){
        return communityMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CommunityMember not found with id: " + id));
    }

    public List<CommunityMember> getAssignedMembers(List<CommunityMember> positions){
        return positions.stream().filter(communityMember -> communityMember.getFreelancer()!=null).toList();
    }

    public List<CommunityMember> getNotAssignedPositions(List<CommunityMember> positions){
        return positions.stream().filter(communityMember -> communityMember.getFreelancer()==null).toList();
    }

    public CommunityMemberSettingsResponseDTO getMembersSettingsPosition(String communityId){
        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));


        return CommunityMemberSettingsResponseDTO.builder()
                .isUserAdmin(jwtService.isCommunityAdmin(communityId))
                .communityMembers(CommunityPositionAndRoleResponseMapper.toDTOList(community.getCommunityMembers()))
                .build();
    }

    @Transactional
    public void updateCommunityMembers(String communityId, List<CommunityMemberUpdateRequestDTO> membersDTOs){
        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        List<CommunityMember> existingMembers = community.getCommunityMembers();
        List<Long> dtoMemberIds = membersDTOs.stream()
                .map(CommunityMemberUpdateRequestDTO::getPositionId)
                .toList();

        UUID adminId = community.getAdmin().getId();
        boolean adminExists = existingMembers.stream()
                .filter(member -> dtoMemberIds.contains(member.getId()))
                .anyMatch(member -> member.getFreelancer() != null && member.getFreelancer().getId().equals(adminId));

        if (!adminExists) {
            throw new RuntimeException("Admin cannot be removed from the community");
        }

        List<CommunityMember> toDelete = existingMembers.stream()
                .filter(member -> member.getId() != null && !dtoMemberIds.contains(member.getId()))
                .toList();

        toDelete.forEach(member -> {
            communityMemberRepository.delete(member);
            existingMembers.remove(member);
        });

        for(CommunityMemberUpdateRequestDTO member : membersDTOs){
            if(member.getPositionId() != null && member.getPositionId() != 0L){
                CommunityMember communityMember = communityMemberRepository.findByIdAndCommunityUuid(member.getPositionId(),UUID.fromString(communityId))
                        .orElseThrow(()-> new NotFoundException("Member Not Found!"));

                communityMember.setPositionName(member.getPositionName());
                communityMember.setPositionPercent(member.getFinancialPercent());
                communityMember.setDescription(member.getDescription());
                communityMemberRepository.save(communityMember);

            } else if (member.getPositionId() != null){
                CommunityMember newMember = CommunityMember.builder()
                        .community(community)
                        .positionPercent(member.getFinancialPercent())
                        .description(member.getDescription())
                        .positionName(member.getPositionName())
                        .build();

                communityMemberRepository.save(newMember);
                existingMembers.add(newMember);
            }
        }

        if (!isTotalPercentagesValid(existingMembers)) {
            throw new RuntimeException("Total financial percentage of all positions must equal 100%");
        }
    }

    public boolean isTotalPercentagesValid(List<CommunityMember> communityMembers) {
        int totalPercent = communityMembers.stream()
                .mapToInt(member -> (int) member.getPositionPercent())
                .sum();
        return totalPercent == 100;
    }

}
