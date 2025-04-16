package taskaya.backend.entity.community.posts;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "postcomment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostComment {

    @Id
    private String id;

    @Field(name = "owner_id")
    private String owner_id;

    @Field(name = "post_id")
    private String post_id;

    @Field(name = "content")
    private String content;

    @Field(name = "created_at")
    private Date created_at = new Date();
}