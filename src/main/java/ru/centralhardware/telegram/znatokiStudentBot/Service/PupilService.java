package ru.centralhardware.telegram.znatokiStudentBot.Service;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Service;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Pupil;
import ru.centralhardware.telegram.znatokiStudentBot.Repository.PupilRepository;
import ru.centralhardware.telegram.znatokiStudentBot.Web.Edit;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class PupilService {

    public static final String TELEPHONE = "telephone";
    private final PupilRepository repository;
    private final EntityManager entityManager;

    public PupilService(PupilRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    public Map<String, String> getEmail(){
        List<Pupil> list = repository.findAll();
        Map<String, String> result = new HashMap<>();
        list.forEach(it -> {
            if (it.getEmail() == null) return;
            if (it.isDeleted()) return;
            result.put(it.getEmail(), String.format("%s %s %s", it.getName(), it.getSecondName(), it.getLastName()));
        });
        return result;
    }

    public Map<String, String> getTelephone(){
        List<Pupil> list = repository.findAll();
        Map<String, String> result = new HashMap<>();
        list.forEach(it -> {
            if (it.getTelephone() == null) return;
            if (it.isDeleted()) return;
            result.put(it.getTelephone(),  String.format("%s %s %s", it.getName(), it.getSecondName(), it.getLastName()));
        });
        return result;
    }

    public List<Pupil> getAll(){
        List<Pupil> result = new ArrayList<>();
        repository.findAll().forEach(it -> {
            if (!it.isDeleted()) result.add(it);
        });
        return result;
    }

    public Pupil save(Pupil pupil){
        return repository.save(pupil);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<Pupil> search(String text) throws InterruptedException {
        FullTextEntityManager fullTextEntityManager
                = Search.getFullTextEntityManager(entityManager);
        fullTextEntityManager.createIndexer().startAndWait();
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Pupil.class)
                .get();
        org.apache.lucene.search.Query query = queryBuilder
                .keyword().
                        fuzzy()
                .onField("name").
                        andField(Edit.SECOND_NAME).
                        andField(Edit.LAST_NAME).
                        andField(TELEPHONE).
                        andField("telephoneMother").
                        andField("telephoneGrandMother").
                        andField(Edit.EMAIL).
                        andField("motherName").
                        andField("fatherName").
                        andField("grandMotherName")
                .matching(text)
                .createQuery();
        org.hibernate.search.jpa.FullTextQuery jpaQuery
                = fullTextEntityManager.createFullTextQuery(query, Pupil.class);
        List<Pupil> searchResult =  (List<Pupil>) jpaQuery.getResultList();
        List<Pupil> result = new ArrayList<>();
        searchResult.forEach(it -> {
            if (!it.isDeleted()){
                result.add(it);
            }
        });
        return result;
    }

    public Optional<Pupil> findById(Integer id){
        var result =  repository.findById(id);
        if (result.isEmpty())           return Optional.empty();
        if (result.get().isDeleted())   return Optional.empty();
        return result;
    }

    public boolean checkExistenceByFio(String name, String secondName, String lastName){
        int count = (int) repository.findAllByNameAndSecondNameAndLastName(name, secondName, lastName).stream().filter(it -> !it.isDeleted()).count();
        return count > 0 ;

    }

    public boolean existByTelephone(String telephone){
        return  repository.findByTelephone(telephone).size()            > 0   ||
                repository.findByTelephoneMother(telephone).size()      > 0   ||
                repository.findByTelephoneFather(telephone).size()      > 0   ||
                repository.findByTelephoneGrandMother(telephone).size() > 0;
    }

    public boolean canInsertTelephoneMother(String telephone){
        return repository.findByTelephoneMother(telephone).size() <=3;
    }

    public boolean canInsertTelephoneFather(String telephone){
        return repository.findByTelephoneFather(telephone).size() <=3;
    }

    public boolean canInsertTelephoneGrandMother(String telephone){
        return repository.findByTelephoneGrandMother(telephone).size() <=3;
    }

    public boolean existByEmail(String email){
        int count;
        count = (int) repository.findByEmail(email).stream().filter(it -> !it.isDeleted()).count();
        return count > 0;
    }

}
