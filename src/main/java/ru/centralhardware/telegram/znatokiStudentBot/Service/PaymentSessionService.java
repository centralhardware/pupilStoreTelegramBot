package ru.centralhardware.telegram.znatokiStudentBot.Service;

import org.springframework.stereotype.Service;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.PaymentsSession;
import ru.centralhardware.telegram.znatokiStudentBot.Repository.PaymentSessionRepository;

import java.util.Optional;

@Service
public class PaymentSessionService {

    private final PaymentSessionRepository paymentSessionRepository;

    public PaymentSessionService(PaymentSessionRepository paymentSessionRepository, PaymentService paymentService) {
        this.paymentSessionRepository = paymentSessionRepository;
    }

    public PaymentsSession create() {
        return paymentSessionRepository.save(new PaymentsSession());
    }

    public Optional<PaymentsSession> find(String sessionId) {
        return paymentSessionRepository.findById(sessionId);
    }


}
