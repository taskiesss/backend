package taskaya.backend.services.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.repository.community.CommunityPostCommentRepository;

@Service
public class CommunityPostCommentService {
    @Autowired
    CommunityPostCommentRepository communityPostCommentRepository;
}
