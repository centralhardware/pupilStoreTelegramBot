package ru.centralhardware.telegram.znatokiStudentBot.Web.Dto;

import ru.centralhardware.telegram.znatokiStudentBot.Entity.Payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public record PaymentDto(
        String number,
        String date,
        String pupil,
        String amount,
        String subject,
        String howToPay,
        String added
) {
    public PaymentDto(Payment payment, int number) {
        this(
                String.valueOf(number),
                new SimpleDateFormat("dd-MM-yyyy").
                        format(payment.getDateOfPayment()),
                String.format("%s %s %s",
                        payment.getPupil().getSecondName(),
                        payment.getPupil().getName(),
                        payment.getPupil().getLastName()),
                String.valueOf(payment.getAmount()),
                payment.getSubject().getRusName(),
                payment.getHowToPay().getRusName(),
                String.format("%s %s %s",
                        payment.getCreatedBy().getUsername(),
                        payment.getCreatedBy().getFirstName(),
                        payment.getCreatedBy().getLastName())
        );
    }

    public static List<PaymentDto> createDtoList(List<? extends Payment> all) {
        var result = new ArrayList<PaymentDto>();
        all.forEach(it -> result.add(new PaymentDto(it, all.indexOf(it)+1)));
        return result;
    }
}
