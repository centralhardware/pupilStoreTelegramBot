package ru.centralhardware.telegram.znatokiStudentBot.Repository;

import org.springframework.data.repository.CrudRepository;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.PaymentsSession;

import java.util.List;

public interface PaymentSessionRepository extends CrudRepository<PaymentsSession, String> {

    List<PaymentsSession> findAll();

}
