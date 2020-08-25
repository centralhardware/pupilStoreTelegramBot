package ru.centralhardware.telegram.znatokiStudentBot.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TimeStampUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@NoArgsConstructor
public class Session {

    private static final int expireTime = 600000;
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    @Getter
    private String uuid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pupil")
    @Getter
    private Pupil pupil;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updateBy")
    @Getter
    private TelegramUser updateBy;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    public Session(@NonNull Pupil pupil, @NonNull TelegramUser updateBy) {
        this.pupil = pupil;
        this.updateBy = updateBy;
    }

    public boolean isExpire() {
        return TimeStampUtils.getTimestamp() - createDate.getTime() > expireTime;
    }
}
