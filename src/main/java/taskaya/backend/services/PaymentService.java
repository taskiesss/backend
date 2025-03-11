package taskaya.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.payments.responses.PaymentSearchResponseMapper;
import taskaya.backend.DTO.payments.responses.PaymentSearchResponseDTO;
import taskaya.backend.entity.Payment;
import taskaya.backend.entity.User;
import taskaya.backend.repository.PaymentRepository;
import taskaya.backend.specifications.PaymentSpecification;

import java.util.Date;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Page<PaymentSearchResponseDTO> findPayments(User user ,  Date startDate, Date endDate, Payment.Type type, int page, int size) {
        var specification = PaymentSpecification.searchPayment(user, user, startDate, endDate, type);
        Sort sort= Sort.by(Sort.Order.desc("date"));
        Pageable pageable = PageRequest.of(page, size,sort);
        Page <Payment> paymentPage = paymentRepository.findAll(specification, pageable);
        return PaymentSearchResponseMapper.toDTOPage(paymentPage,user);
    }
}