package taskaya.backend.DTO.communities.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.DTO.login.NameAndPictureResponseDTO;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityPostResponseDTO {
    String postID;
    NameAndPictureResponseDTO postOwner;
    String postTitle;
    String postContent;
    Boolean isLiked;
    Integer numberOfLikes;
    Integer numberOfComments;
    Date date;
}
