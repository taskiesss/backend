package taskaya.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.Payment;
import taskaya.backend.entity.User;
import taskaya.backend.repository.PaymentRepository;
import taskaya.backend.specifications.PaymentSpecification;

import java.util.Date;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Page<Payment> findPayments(User sender, User receiver, Date startDate, Date endDate, Payment.Type type, int page, int size) {
        var specification = PaymentSpecification.searchContract(receiver, sender, startDate, endDate, type);
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findAll(specification, pageable);
    }
}