package taskaya.backend.services.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.communities.responses.CommunityPostCommentResponseDTO;
import taskaya.backend.DTO.communities.responses.CommunityPostResponseDTO;
import taskaya.backend.DTO.mappers.CommunityPostCommentResponseMapper;
import taskaya.backend.DTO.mappers.CommunityPostResponseMapper;
import taskaya.backend.entity.community.posts.PostComment;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.repository.community.CommunityPostCommentRepository;
import taskaya.backend.services.freelancer.FreelancerService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CommunityPostCommentService {
    @Autowired
    CommunityPostCommentRepository communityPostCommentRepository;

    @Autowired
    FreelancerService freelancerService;

    @PreAuthorize("@jwtService.isCommunityMember(#communityId)")
    public Page<CommunityPostCommentResponseDTO> getCommunityPostComments(String communityId, String postId, int page, int size) {
        List<PostComment> comments = communityPostCommentRepository.findByPostId(postId);

        List<CommunityPostCommentResponseDTO> listDTOs = new ArrayList<>();
        for(PostComment comment:comments){
            Freelancer owner = freelancerService.getById(UUID.fromString(comment.getOwnerId()));
            CommunityPostCommentResponseDTO commentDTO = CommunityPostCommentResponseMapper.toDTO(comment, owner);
            listDTOs.add(commentDTO);
        }

        listDTOs.sort((comment1, comment2) -> comment2.getDate().compareTo(comment1.getDate()));
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), listDTOs.size());
        List<CommunityPostCommentResponseDTO> paginatedList = listDTOs.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, listDTOs.size());
    }
}
