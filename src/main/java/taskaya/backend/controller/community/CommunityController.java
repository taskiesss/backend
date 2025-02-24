package taskaya.backend.controller.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.communities.requests.CommunitySearchRequestDTO;
import taskaya.backend.DTO.communities.responses.CommunitySearchResponseDTO;
import taskaya.backend.DTO.workerEntity.responses.WorkerEntityWorkdoneResponseDTO;
import taskaya.backend.entity.community.Community;
import taskaya.backend.services.community.CommunityService;

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
}
