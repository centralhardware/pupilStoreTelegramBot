package ru.centralhardware.telegram.znatokiStudentBot.Entity;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TimeStampUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class PaymentsSession {

    private static final int expireTime = 3600000;
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    @Getter
    private String uuid;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    public boolean isExpire() {
        return TimeStampUtils.getTimestamp() - createDate.getTime() > expireTime;
    }
}
