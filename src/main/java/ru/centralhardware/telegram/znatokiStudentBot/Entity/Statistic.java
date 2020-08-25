package ru.centralhardware.telegram.znatokiStudentBot.Entity;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Action;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@NoArgsConstructor
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Column
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
    private TelegramUser performedBy;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date performedDate;

    public Statistic(Action action, TelegramUser performedBy, String username) {
        this.action = action;
        this.performedBy = performedBy;
        this.username = username;
    }
}
