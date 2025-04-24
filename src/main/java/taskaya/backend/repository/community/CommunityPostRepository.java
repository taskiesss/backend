package taskaya.backend.repository.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.community.posts.Post;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityPostRepository extends MongoRepository<Post, String> {
    List<Post> findByCommunityId(String id);

}
