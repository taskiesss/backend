package taskaya.backend.controller.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.search.CommunitySearchRequestDTO;
import taskaya.backend.DTO.search.CommunitySearchResponseDTO;
import taskaya.backend.entity.community.Community;
import taskaya.backend.services.community.CommunityService;

@RestController
@RequestMapping("/communities")
public class CommunityController {
    @Autowired
    CommunityService communityService;

    @PostMapping
    public Community getCommunity(@RequestParam String commName){
        return communityService.getCommunityByName(commName);
    }

    @PostMapping("/search")
    public Page<CommunitySearchResponseDTO> searchCommunity(
            @RequestBody CommunitySearchRequestDTO requestDTO) {
        return communityService.searchCommunities(requestDTO);
    }
}
