package taskaya.backend.repository.community;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.community.posts.PostComment;

import java.util.List;

@Repository
public interface CommunityPostCommentRepository extends MongoRepository<PostComment, String> {
    List<PostComment> findByPostId(String postId);
}
