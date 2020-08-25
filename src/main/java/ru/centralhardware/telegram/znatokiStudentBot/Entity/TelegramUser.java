package ru.centralhardware.telegram.znatokiStudentBot.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Role;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@NoArgsConstructor
public class TelegramUser {

    @Id
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Role role;
    @Column(name = "authorizeInGoogle", columnDefinition = "boolean default false")
    @Getter
    @Setter
    private boolean authorizeInGoogle;

    @OneToMany(mappedBy = "createdBy")
    private Set<Pupil> create;

    @OneToMany(mappedBy = "updateBy")
    private Set<Pupil> update;

    @OneToMany(mappedBy = "pupil")
    private Set<Session> sessions;

    @OneToMany(mappedBy = "updateBy")
    private Set<Session> updates;

    @OneToMany(mappedBy = "createdBy")
    private Set<Payment> cratePayments;

    @OneToMany(mappedBy = "createdBy")
    private Set<Lesson> lessons;

    @OneToMany(mappedBy = "performedBy")
    private Set<Statistic> statistics;

    public TelegramUser(Long id, String username, String firstName, String lastName, Role role) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    @Override
    public String toString() {
        if (username == null) {
            return String.format("*%s %s*", firstName, lastName);
        } else {
            return String.format("[%s](https://t.me/%s)", username, username);
        }
    }

    public String getFioForEmail() {
        if (username == null) {
            return String.format("*%s %s*", firstName, lastName);
        } else {
            return String.format("https://t.me/%s", username);
        }
    }

    public boolean hasReadRight() {
        if (role == null) return false;
        return role != Role.UNAUTHORIZED;
    }

    public boolean hasWriteRight() {
        if (role == null) return false;
        return role == Role.READ_WRITE || role == Role.ADMIN;
    }
}
