package taskaya.backend.DTO.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.communities.responses.CommunityPostCommentResponseDTO;
import taskaya.backend.DTO.contracts.responses.ContractConversationResponseDTO;
import taskaya.backend.DTO.milestones.responses.MilestonesDetailsResponseDTO;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.community.posts.PostComment;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.ContractConversation;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Component
public class ContractConvoResponseMapper {
    public static ContractConversationResponseDTO toDTO(ContractConversation conversation, Freelancer convoOwner) {
        return ContractConversationResponseDTO.builder()
                .convoId(conversation.getId().toString())
                .convoOwner(NameAndPictureResponseMapper.toDTO(convoOwner))
                .content(conversation.getContent())
                .date(conversation.getCreatedAt())
                .build();
    }

    public static ContractConversationResponseDTO toDTO(ContractConversation conversation, Client convoOwner) {
        return ContractConversationResponseDTO.builder()
                .convoId(conversation.getId().toString())
                .convoOwner(NameAndPictureResponseMapper.toDTO(convoOwner))
                .content(conversation.getContent())
                .date(conversation.getCreatedAt())
                .build();
    }
}

