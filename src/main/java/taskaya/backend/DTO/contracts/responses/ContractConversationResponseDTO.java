package taskaya.backend.DTO.contracts.responses;

import lombok.Builder;
import lombok.Data;
import taskaya.backend.DTO.login.NameAndPictureResponseDTO;

import java.util.Date;

@Builder
@Data
public class ContractConversationResponseDTO {
    String convoId;
    NameAndPictureResponseDTO convoOwner;
    String content;
    Date date;
}
