package ru.centralhardware.telegram.znatokiStudentBot.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.HowToPay;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Subject;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    @Getter
    @Setter
    private String uuid;

    @Column(nullable = false)
    @Getter
    @Setter
    private int amount;

    @Column(nullable = false)
    @Getter
    @Setter
    private Date dateOfPayment;

    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private HowToPay howToPay;

    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
    @Getter
    private TelegramUser createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forPupil")
    @Getter
    private Pupil pupil;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    @Getter
    private Date createDate;

    public Payment(@NonNull TelegramUser createdBy, @NonNull Pupil pupil) {
        this.createdBy = createdBy;
        this.pupil = pupil;
    }

    @Override
    public String toString() {
        return "uuid " + TelegramUtils.makeBold(uuid) +
                "сумма: " + TelegramUtils.makeBold(amount) +
                "дата платежа: " + TelegramUtils.makeBold(new SimpleDateFormat("dd-MM-yyyy").format(dateOfPayment)) +
                "способ оплаты: " + TelegramUtils.makeBold(howToPay.getRusName()) +
                "предмет: " + TelegramUtils.makeBold(subject.getRusName()) +
                "ученик: " + TelegramUtils.makeBold(pupil.getFio()) +
                "дата создания: " + TelegramUtils.makeBold(new SimpleDateFormat("dd-MM-yyyy").format(createDate));
    }
}
