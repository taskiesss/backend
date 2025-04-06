package taskaya.backend.controller.community;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.SimpleResponseDTO;
import taskaya.backend.DTO.communities.requests.AcceptToJoinRequestDTO;
import taskaya.backend.DTO.communities.requests.CommunitySearchRequestDTO;
import taskaya.backend.DTO.communities.requests.VoteRequestDTO;
import taskaya.backend.DTO.communities.responses.*;
import taskaya.backend.DTO.freelancers.requests.DescriptionPatchRequestDTO;
import taskaya.backend.DTO.freelancers.requests.HeaderSectionUpdateRequestDTO;
import taskaya.backend.DTO.freelancers.requests.SkillsUpdateRequestDTO;
import taskaya.backend.DTO.workerEntity.responses.WorkerEntityWorkdoneResponseDTO;
import taskaya.backend.entity.community.Community;
import taskaya.backend.services.community.CommunityService;

import java.io.IOException;

@RestController
@RequestMapping
public class CommunityController {
    @Autowired
    CommunityService communityService;

    @PostMapping
    public Community getCommunity(@RequestParam String commName){
        return communityService.getCommunityByName(commName);
    }

    @PostMapping("/communities/search")
    public ResponseEntity<Page<CommunitySearchResponseDTO> > searchCommunity(
            @RequestBody CommunitySearchRequestDTO requestDTO) {
        return ResponseEntity.ok(communityService.searchCommunities(requestDTO));
    }


    @GetMapping("/communities/{communityId}/profile")
    public ResponseEntity<?> getCommunityProfile(@PathVariable String communityId){
        return ResponseEntity.ok(communityService.getCommunityProfile(communityId));
    }

    @GetMapping("/communities/{id}/workdone")
    public ResponseEntity<Page<WorkerEntityWorkdoneResponseDTO>> communityWorkdone(
            @PathVariable String id, @RequestParam int page, @RequestParam int size
    ){
        return ResponseEntity.ok(communityService.getCommunityWorkdone(id, page, size));
    }

    @PatchMapping(value = "/communities/{id}/profile-picture", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProfilePicture(
            @PathVariable String id,
            @RequestPart(value = "profilePicture") MultipartFile profilePicture) throws IOException {
        communityService.updateProfilePicture(id, profilePicture);
        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("Profile Picture Updated!").build());
    }

    @PatchMapping(value = "/communities/{id}/cover-picture", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateCoverPicture(
            @PathVariable String id,
            @RequestPart(value = "coverPicture") MultipartFile coverPicture) throws IOException {
        communityService.updateCoverPicture(id, coverPicture);
        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("Cover Picture Updated!").build());
    }

    @PatchMapping("/communities/{id}/skills")
    public ResponseEntity<?> updateSkills(
            @PathVariable String id,
            @RequestBody SkillsUpdateRequestDTO skills){
        communityService.updateSkills(id, skills);
        return new ResponseEntity<>(SimpleResponseDTO.builder().message("Skills updated successfully.").build(),HttpStatus.OK);
    }

    @PatchMapping("/communities/{id}/description")
    public ResponseEntity<?> updateDescription(
            @PathVariable String id,
            @RequestBody DescriptionPatchRequestDTO request) {
        communityService.updateDescription(id, request);
        return ResponseEntity.ok(SimpleResponseDTO.builder().message("true").build());
    }

    @PatchMapping("/communities/{id}/header-section")
    public ResponseEntity<?> updateHeaderSection(
            @PathVariable String id,
            @RequestBody HeaderSectionUpdateRequestDTO requestDTO){
        communityService.updateHeaderSection(id, requestDTO);
        return new ResponseEntity<>(SimpleResponseDTO.builder().message("header updated successfully.").build(),HttpStatus.OK);
    }

    @GetMapping("/freelancers/communities/{communityId}/joinrequests")
    @PreAuthorize("@jwtService.isCommunityMember(#communityId)")
    public ResponseEntity<Page<CommunityJoinReqResponseDTO>> joinRequests (
            @PathVariable String communityId,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size){
        return ResponseEntity.ok(communityService.getJoinRequests(communityId, page, size));

    }

    @GetMapping("/freelancers/communities/{communityId}/offers")
    @PreAuthorize("@jwtService.isCommunityMember(#communityId)")
    public ResponseEntity<Page<CommunityOfferResponseDTO>> offers (
            @PathVariable String communityId,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size){
        return ResponseEntity.ok(communityService.getOffers(communityId, page, size));

    }

    @PostMapping("/freelancers/communities/{communityId}/accept-to-join")
    @PreAuthorize("@jwtService.isCommunityAdmin(#communityId)")
    public ResponseEntity<?> acceptToJoin(
            @PathVariable String communityId,
            @RequestBody AcceptToJoinRequestDTO request
    ) throws MessagingException {
        communityService.acceptToJoin(communityId, request);
        return new ResponseEntity<>(SimpleResponseDTO.builder().message("Freelancer request processed successfully.").build(),HttpStatus.OK);
    }

    @PostMapping("/freelancers/communities/{communityId}/vote")
    @PreAuthorize("@jwtService.isCommunityMember(#communityId)")
    public ResponseEntity<?> vote(
            @PathVariable String communityId,
            @RequestBody VoteRequestDTO request
    ){
        communityService.vote(communityId, request);
        return new ResponseEntity<>(SimpleResponseDTO.builder().message("Vote recorded successfully.").build(),HttpStatus.OK);
    }

    @GetMapping("/freelancers/communities/{communityId}/votes/{contractId}")
    @PreAuthorize("@jwtService.isCommunityMember(#communityId)")
    public ResponseEntity<CommunityVotesDetailsResponseDTO> getVotesDetails(
            @PathVariable String communityId,
            @PathVariable String contractId
    ){
        return ResponseEntity.ok(communityService.getVotesDetails(communityId,contractId));
    }

    @GetMapping("/freelancers/communities/{communityId}/available-positions")
    @PreAuthorize("@jwtService.isNotCommunityMember(#communityId)")
    public ResponseEntity<Page<CommunityAvailablePositionsResponseDTO>>getAvailablePositions(
            @PathVariable String communityId,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size
    ){
        return ResponseEntity.ok(communityService.getAvailablePositions(communityId,page,size));
    }

    @PostMapping("/freelancers/communities/{communityId}/join-request/{positionId}")
    @PreAuthorize("@jwtService.isNotCommunityMember(#communityId)")
    public ResponseEntity<?> requestToJoin(
            @PathVariable String communityId,
            @PathVariable long positionId
    ){
        communityService.requestToJoin(communityId, positionId);
        return new ResponseEntity<>(SimpleResponseDTO.builder().message("accepted.").build(),HttpStatus.OK);
    }

}
