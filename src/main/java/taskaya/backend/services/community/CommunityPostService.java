package taskaya.backend.services.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.repository.community.CommunityPostRepository;

@Service
public class CommunityPostService {
    @Autowired
    CommunityPostRepository communityPostRepository;

}
