package taskaya.backend.DTO.payments.responses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import taskaya.backend.entity.Payment;
import taskaya.backend.entity.User;

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

            dto.setDescription("Payment for "+payment.getContract().getJob().getTitle());

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
