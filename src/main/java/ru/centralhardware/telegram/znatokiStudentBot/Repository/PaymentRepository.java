package ru.centralhardware.telegram.znatokiStudentBot.Repository;

import org.springframework.data.repository.CrudRepository;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Payment;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, String> {

    List<Payment> findAll();

}
