package taskaya.backend.services.community;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.communities.requests.CommunityPostRequestDTO;
import taskaya.backend.DTO.communities.responses.CommunityPostResponseDTO;
import taskaya.backend.DTO.login.NameAndPictureResponseDTO;
import taskaya.backend.DTO.mappers.CommunityPostResponseMapper;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.posts.Post;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.repository.community.CommunityPostRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.work.WorkerEntityRepository;
import taskaya.backend.services.NotificationService;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.services.work.WorkerEntityService;

import java.util.*;

@Service
public class CommunityPostService {
    @Autowired
    CommunityPostRepository communityPostRepository;

    @Autowired
    FreelancerService freelancerService;

    @Autowired
    CommunityPostCommentService communityPostCommentService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    WorkerEntityService workerEntityService;

    @Autowired
    CommunityRepository communityRepository;

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

    @Transactional
    @PreAuthorize("@jwtService.isCommunityMember(#communityId)")
    public void createCommunityPost(String communityId, CommunityPostRequestDTO requestDTO){
        Post post = Post.builder()
                .title(requestDTO.getPostTitle())
                .content(requestDTO.getPostContent())
                .communityId(communityId)
                .ownerId(freelancerService.getFreelancerFromJWT().getId().toString())
                .createdAt(new Date())
                .build();
        communityPostRepository.save(post);

        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()->new RuntimeException("Community not found!"));

        List<Freelancer>freelancers = workerEntityService.getFreelancersByWorkerEntity(community.getWorkerEntity());
        for(Freelancer freelancer:freelancers){
            if(freelancer != null){
                notificationService.sendNewPostNotification(post.getTitle(),community.getCommunityName(),freelancer.getUser(),communityId);
            }
        }

    }

    @Transactional
    @PreAuthorize("@jwtService.isCommunityMember(#communityId)")
    public void createCommunityPostComment(String communityId, String postId, String content){
        Post post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found!"));

        String commentId = communityPostCommentService.createPostComment(communityId, postId, content);
        post.getCommentId().add(commentId);
        communityPostRepository.save(post);
    }

    @Transactional
    @PreAuthorize("@jwtService.isCommunityMember(#communityId)")
    public void createCommunityPostLike(String communityId, String postId, boolean like){
        Post post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found!"));

        String userId = freelancerService.getFreelancerFromJWT().getId().toString();
        if(like && !post.getLikerId().contains(userId)){
            post.getLikerId().add(userId);
        } else if(!like){
            post.getLikerId().remove(userId);
        }
        communityPostRepository.save(post);
    }

    @Transactional
    @PreAuthorize("@jwtService.isCommunityAdmin(#communityId)")
    public void deleteCommunityPost(String communityId, String postId){
        Post post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found!"));
        communityPostRepository.delete(post);
        communityPostCommentService.deleteAllPostComments(communityId, postId);
    }

    @Transactional
    @PreAuthorize("@jwtService.isCommunityAdmin(#communityId)")
    public void deleteCommunityPostComment(String communityId, String postId,String commentId){
        communityPostCommentService.deletePostComment(communityId, commentId);

        Post post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found!"));
        post.getCommentId().remove(commentId);
        communityPostRepository.save(post);
    }
}
