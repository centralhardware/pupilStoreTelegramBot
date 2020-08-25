package ru.centralhardware.telegram.znatokiStudentBot.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 *
 */
@Entity
@Table
@NoArgsConstructor
public class Email {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    @Getter
    private String uuid;
    @Getter
    private String subject;
    @Getter
    private String text;
    @Getter
    private Boolean isExecuted;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    public Email(@NonNull String subject, @NonNull String text) {
        this.subject = subject;
        this.text = text;
        isExecuted = false;
    }
}
