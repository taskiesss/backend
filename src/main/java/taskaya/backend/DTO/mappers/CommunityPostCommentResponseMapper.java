package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.communities.responses.CommunityPostCommentResponseDTO;
import taskaya.backend.entity.community.posts.PostComment;
import taskaya.backend.entity.freelancer.Freelancer;

@Component
public class CommunityPostCommentResponseMapper {
    public static CommunityPostCommentResponseDTO toDTO(PostComment comment, Freelancer owner){
        return CommunityPostCommentResponseDTO.builder()
                .commentId(comment.getId())
                .postOwner(NameAndPictureResponseMapper.toDTO(owner))
                .content(comment.getContent())
                .date(comment.getCreatedAt())
                .build();
    }
}
