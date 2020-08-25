package ru.centralhardware.telegram.znatokiStudentBot.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Pupil;

import java.util.List;

@Repository
public interface PupilRepository extends CrudRepository<Pupil, Integer> {

    List<Pupil> findAll();

    List<Pupil> findAllByNameAndSecondNameAndLastName(String name, String secondName, String lastName);

    List<Pupil> findByTelephone(String telephone);

    List<Pupil> findByTelephoneMother(String telephoneMother);

    List<Pupil> findByTelephoneFather(String telephoneFather);

    List<Pupil> findByTelephoneGrandMother(String telephoneGrandMother);

    List<Pupil> findByEmail(String email);

}
