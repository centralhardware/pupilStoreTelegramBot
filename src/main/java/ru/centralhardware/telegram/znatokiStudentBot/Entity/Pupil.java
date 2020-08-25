package ru.centralhardware.telegram.znatokiStudentBot.Entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.CongenitalDiseases;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.HowToKnow;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Subject;
import ru.centralhardware.telegram.znatokiStudentBot.Util.MapsUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelephoneUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Table
@Indexed
@Slf4j
public class Pupil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;
    /**
     * имя
     */
    @Column(nullable = false)
    @Field(store = Store.YES)
    @Getter
    @Setter
    private String name;
    /**
     * фамилия
     */
    @Column(nullable = false)
    @Field(store = Store.YES)
    @Getter
    @Setter
    private String secondName;
    /**
     * отчество
     */
    @Column(nullable = false)
    @Field(store = Store.YES)
    @Getter
    @Setter
    private String lastName;
    /**
     * 1-11
     * -1 preschool age
     */
    @Column(nullable = false)
    @Getter
    @Setter
    private int classNumber;
    @Column(nullable = false)
    @Getter
    @Setter
    private String address;
    @Column(nullable = false)
    @Getter
    @Setter
    private Date dateOfRecord;
    @Column(nullable = false)
    @Getter
    @Setter
    private Date dateOfBirth;
    /**
     * only mobile
     */
    @Column(nullable = false)
    @Field(store = Store.YES)
    @Getter
    @Setter
    private String telephone;
    @Column(length = 11)
    @Field(store = Store.YES)
    @Getter
    @Setter
    private String telephoneMother;
    @Column(length = 11)
    @Field(store = Store.YES)
    @Getter
    @Setter
    private String telephoneFather;
    @Column(length = 11)
    @Field(store = Store.YES)
    @Getter
    @Setter
    private String telephoneGrandMother;
    @Column
    @Field(store = Store.YES)
    @Email
    @Getter
    @Setter
    private String email;
    @ElementCollection(targetClass=Subject.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING) // Possibly optional (I'm not sure) but defaults to ORDINAL.
    @CollectionTable(name="subjects")
    @Column() // Column name in person_interest
    @Getter
    private final Set<Subject>  subjects = new HashSet<>(Subject.values().length);
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private HowToKnow howToKnow;
    @Column
    @Getter
    @Setter
    private String hobbies;
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private CongenitalDiseases congenitalDiseases;
    @Column
    @Getter
    @Setter
    private String placeOfWorkMother;
    @Column
    @Getter
    @Setter
    private String placeOfWorkFather;
    @Column
    @Field(store = Store.YES)
    @Getter
    @Setter
    private String motherName;
    @Column
    @Field(store = Store.YES)
    @Getter
    @Setter
    private String fatherName;
    @Column
    @Field(store = Store.YES)
    @Getter
    @Setter
    private String grandMotherName;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "createdBy")
    @Setter
    private TelegramUser createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updateBy")
    @Getter
    @Setter
    private TelegramUser updateBy;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    @Setter
    @Getter
    private boolean deleted;

    @OneToMany(mappedBy = "pupil")
    private Set<Lesson> lessons;

    public boolean checkTelephoneUnique(@NonNull String telephone){
        return  Objects.equals(this.telephone, telephone)        ||
                Objects.equals(telephoneMother, telephone)  ||
                Objects.equals(telephoneFather, telephone)  ||
                Objects.equals(telephoneGrandMother, telephone);
    }

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public String toString() {
        String mail             = email             == null? "" : String.format("[%s](mailto:%s)", email, email);
        String know             = howToKnow         == null? "" : howToKnow.toString();
        String hobby            = hobbies           == null? "" : hobbies;
        String workMother       = placeOfWorkMother == null? "" : placeOfWorkMother;
        String workFather       = placeOfWorkFather == null? "" : placeOfWorkFather;
        String nameMother       = motherName        == null? "" : motherName;
        String nameFather       = fatherName        == null? "" : fatherName;
        String nameGrandMother  = grandMotherName   == null? "" : grandMotherName;
        String updated          = updateBy          == null? "" : updateBy.toString();

        return "id=" +                          TelegramUtils.makeBold(id)+
                "фамилия=" +                    TelegramUtils.makeBold(secondName) +
                "имя=" +                        TelegramUtils.makeBold(name) +
                "отчество=" +                   TelegramUtils.makeBold(lastName) +
                "класс=" +                      TelegramUtils.makeBold(classNumber == -1 ? "Дошкольник" : String.valueOf(classNumber)) +
                "адрес=" +                     String.format("[%s](%s)",address,MapsUtils.makeUrl(address)) +
                "дата записи=" +                TelegramUtils.makeBold(dateFormatter.format(dateOfRecord)) +
                "дата рождения=" +              TelegramUtils.makeBold(dateFormatter.format(dateOfBirth)) +
                "телефон=" +                    TelephoneUtils.format(telephone) +
                "телефон матери=" +             TelephoneUtils.format( telephoneMother) +
                "телефон отца=" +               TelephoneUtils.format( telephoneFather) +
                "телефон бабушки=" +            TelephoneUtils.format(telephoneGrandMother) +
                "email= " +                     mail + "\n" +
                "предметы=" +                   TelegramUtils.makeBold(subjects.toString()) +
                "как узнал=" +                  TelegramUtils.makeBold(know) +
                "увлечения=" +                  TelegramUtils.makeBold(hobby) +
                "хронические заболевания=" +    TelegramUtils.makeBold(congenitalDiseases.toString()) +
                "место работы матери=" +        TelegramUtils.makeBold(workMother) +
                "место работы отца=" +          TelegramUtils.makeBold(workFather) +
                "имя матери=" +                 TelegramUtils.makeBold(nameMother) +
                "имя отца=" +                   TelegramUtils.makeBold(nameFather) +
                "имя бабушки=" +                TelegramUtils.makeBold(nameGrandMother) +
                "дата создания=" +              TelegramUtils.makeBold(dateFormatter.format(createDate)) +
                "дата изменения=" +             TelegramUtils.makeBold(dateFormatter.format(modifyDate)) +
                "создано=" +                    createdBy + "\n" +
                "редактировано=" +              updated + "\n";
    }

    public String getFio(){
        return String.format("%s %s %s", secondName, name, lastName);
    }

    public void incrementClassNumber(){
        classNumber++;
        log.info("class number incremented to {} for pupil {} {} {}", classNumber, name, secondName, lastName);
    }

    public int getAge(){
        Calendar birthDay = new GregorianCalendar(Locale.US );
        Calendar now = new GregorianCalendar(Locale.US );
        birthDay.setTime(dateOfBirth);
        now.setTime(new Date());
        return now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
    }
}
