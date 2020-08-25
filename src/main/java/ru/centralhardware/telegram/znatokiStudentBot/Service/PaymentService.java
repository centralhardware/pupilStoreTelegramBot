package ru.centralhardware.telegram.znatokiStudentBot.Service;

import org.springframework.stereotype.Service;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Payment;
import ru.centralhardware.telegram.znatokiStudentBot.Repository.PaymentRepository;
import ru.centralhardware.telegram.znatokiStudentBot.Web.Dto.PaymentDto;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<PaymentDto> getLastMonth() {
        return PaymentDto.createDtoList(paymentRepository.findAll());
    }

}
