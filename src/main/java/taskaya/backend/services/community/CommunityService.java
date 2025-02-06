package taskaya.backend.services.community;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.mappers.CommunitySearchResponseMapper;
import taskaya.backend.DTO.search.CommunitySearchRequestDTO;
import taskaya.backend.DTO.search.CommunitySearchResponseDTO;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.specifications.CommunitySpecification;

import java.util.UUID;

@Service
public class CommunityService {
    @Autowired
    private CommunityRepository communityRepository;

    public Community getCommunityByName(String communityName){
        return communityRepository.findByCommunityName(communityName)
                .orElseThrow(() -> new RuntimeException("Community not found with name: " + communityName));
    }

    public CommunitySearchResponseDTO searchCommunityByName(String communityName){
        return CommunitySearchResponseMapper.toDTO(communityRepository.findByCommunityName(communityName)
                .orElseThrow(() -> new RuntimeException("Community not found with name: " + communityName)));
    }


    @Transactional
    public void save(Community community){
        communityRepository.save(community);
    }

    public Page<CommunitySearchResponseDTO> searchCommunities(CommunitySearchRequestDTO requestDTO){
        Specification<Community> specCommunity = CommunitySpecification.searchCommunities(
                requestDTO.getSearch(),
                requestDTO.getSkills(),
                requestDTO.getExperienceLevel(),
                requestDTO.getHourlyRateMin(),
                requestDTO.getHourlyRateMax(),
                requestDTO.getRate()
        );

        Sort sort;
        if(SortDirection.DESC.equals(requestDTO.getSortDirection())){
            sort = Sort.by(Sort.Order.desc(requestDTO.getSortBy()));
        } else{
            sort = Sort.by(Sort.Order.asc(requestDTO.getSortBy()));
        }

        // Create Page Request for pagination
        Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getSize(), sort);

        // Get page of communities using specification, sorting and pagination
        Page<Community> communityPage = communityRepository.findAll(specCommunity, pageable);

        return CommunitySearchResponseMapper.toDTOPage(communityPage);
    }
}
