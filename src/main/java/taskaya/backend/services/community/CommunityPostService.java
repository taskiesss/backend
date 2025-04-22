package taskaya.backend.services.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.communities.responses.CommunityPostResponseDTO;
import taskaya.backend.DTO.login.NameAndPictureResponseDTO;
import taskaya.backend.DTO.mappers.CommunityPostResponseMapper;
import taskaya.backend.entity.community.posts.Post;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.repository.community.CommunityPostRepository;
import taskaya.backend.services.freelancer.FreelancerService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
public class CommunityPostService {
    @Autowired
    CommunityPostRepository communityPostRepository;

    @Autowired
    FreelancerService freelancerService;

    @PreAuthorize("@jwtService.isCommunityMember(#communityId)")
    public Page<CommunityPostResponseDTO> getCommunityPosts(String communityId, int page, int size) {
        List<Post> posts = communityPostRepository.findByCommunityId(communityId);

        Freelancer currentUser = freelancerService.getFreelancerFromJWT();

        List<CommunityPostResponseDTO> listDTOs = new ArrayList<>();
        for(Post post :posts){
            Freelancer owner = freelancerService.getById(UUID.fromString(post.getOwnerId()));
            CommunityPostResponseDTO postDTO = CommunityPostResponseMapper.toDTO(post, owner, currentUser);
            listDTOs.add(postDTO);
        }

        listDTOs.sort((post1, post2) -> post2.getDate().compareTo(post1.getDate()));

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), listDTOs.size());
        List<CommunityPostResponseDTO> paginatedList = listDTOs.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, listDTOs.size());

    }

    @PreAuthorize("@jwtService.isCommunityMember(#communityId)")
    public List<NameAndPictureResponseDTO> getCommunityPostLikes(String communityId, String postId){
        Post post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found!"));

        List<NameAndPictureResponseDTO> likes = new ArrayList<>();
        for (String id : post.getLikerId()) {
            Freelancer freelancer = freelancerService.getById(UUID.fromString(id));
            NameAndPictureResponseDTO like = NameAndPictureResponseDTO.builder()
                    .profilePicture(freelancer.getProfilePicture())
                    .name(freelancer.getUser().getUsername())
                    .role(freelancer.getUser().getRole())
                    .id(freelancer.getId().toString())
                    .build();
            likes.add(like);
        }
        return likes;
    }
}
