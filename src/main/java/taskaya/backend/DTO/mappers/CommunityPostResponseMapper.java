package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.communities.responses.CommunityPostResponseDTO;
import taskaya.backend.DTO.communities.responses.CommunitySearchResponseDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.posts.Post;
import taskaya.backend.entity.freelancer.Freelancer;

import java.util.LinkedList;
import java.util.List;

@Component
public class CommunityPostResponseMapper {
    public static CommunityPostResponseDTO toDTO(Post post, Freelancer owner, Freelancer currentFreelancer){
        return CommunityPostResponseDTO.builder()
                .postID(post.getId())
                .postOwner(NameAndPictureResponseMapper.toDTO(owner))
                .postTitle(post.getTitle())
                .postContent(post.getContent())
                .isLiked(post.getLikerId().contains(currentFreelancer.getId().toString()))
                .numberOfLikes(post.getLikerId().size())
                .numberOfComments(post.getCommentId().size())
                .date(post.getCreatedAt())
                .build();
    }
}
