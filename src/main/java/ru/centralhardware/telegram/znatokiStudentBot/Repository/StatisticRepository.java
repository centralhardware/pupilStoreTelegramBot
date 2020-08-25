package ru.centralhardware.telegram.znatokiStudentBot.Repository;

import org.springframework.data.repository.CrudRepository;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Statistic;

public interface StatisticRepository extends CrudRepository<Statistic, Integer> {
}
