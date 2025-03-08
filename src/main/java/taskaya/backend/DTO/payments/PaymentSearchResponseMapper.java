package taskaya.backend.DTO.payments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import taskaya.backend.DTO.communities.responses.CommunitySearchResponseDTO;
import taskaya.backend.DTO.payments.responses.PaymentSearchResponseDTO;
import taskaya.backend.entity.Payment;
import taskaya.backend.entity.User;
import taskaya.backend.entity.community.Community;

import java.util.LinkedList;
import java.util.List;

public class PaymentSearchResponseMapper {
    public static PaymentSearchResponseDTO toDTO(Payment payment , User user) {
        PaymentSearchResponseDTO dto = PaymentSearchResponseDTO.builder()
                .id(payment.getId())
                .date(payment.getDate())
                .type(payment.getType())
                .contractId(payment.getContract().getId())
                .build();



        if(payment.getType() == Payment.Type.TRANSACTION) {

            if (payment.getCommunity() != null) {
                dto.setDescription("payment for community " + payment.getCommunity().getCommunityName());
            } else {
                User sender = payment.getSender();
                User receiver = payment.getReceiver();
                if (sender != null && receiver != null) {
                    dto.setDescription("payment from " + sender.getUsername() + " to " + receiver.getUsername());
                }
            }

            if (payment.getSender() != null && payment.getSender().getId().equals(user.getId())) {
                dto.setAmount(-payment.getAmount());
            }
            if (payment.getReceiver() != null && payment.getReceiver().getId().equals(user.getId())) {
                dto.setAmount(payment.getAmount());
            }
        }


        if (payment.getType() == Payment.Type.WITHDRAWL){
            dto.setDescription("withdrawl");
            dto.setAmount(-payment.getAmount());
        }

        if (payment.getType() == Payment.Type.DEPOSIT){
            dto.setDescription("deposit");
            dto.setAmount(payment.getAmount());
        }

        return dto;
    }

    public static List<PaymentSearchResponseDTO> toDTOList(List<Payment> payments , User user) {
        List<PaymentSearchResponseDTO> result = new LinkedList<>();
        for (Payment payment : payments) {
            result.add(toDTO(payment,user));
        }
        return result;
    }

    public static Page<PaymentSearchResponseDTO> toDTOPage(Page<Payment> payments,User user) {
        return new PageImpl<>(toDTOList(payments.getContent(),user), payments.getPageable(), payments.getTotalElements());
    }
}
