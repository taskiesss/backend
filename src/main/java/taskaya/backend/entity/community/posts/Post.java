package taskaya.backend.entity.community.posts;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    private String id;

    @Field(name = "community_id")
    private String communityId;

    @Field(name = "owner_id")
    private String ownerId;

    @Field(name = "title")
    private String title;

    @Field(name = "content")
    private String content;

    @Field(name = "comment_id")
    private List<String> commentId = new ArrayList<>();

    @Field(name = "liker_id")
    private List<String> likerId = new ArrayList<>();

    @Field(name = "created_at")
    private Date createdAt = new Date();
}