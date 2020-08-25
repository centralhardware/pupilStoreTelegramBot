package ru.centralhardware.telegram.znatokiStudentBot.Service;

import org.springframework.stereotype.Service;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Action;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Statistic;
import ru.centralhardware.telegram.znatokiStudentBot.Repository.StatisticRepository;

@Service
public class StatisticService {

    private final StatisticRepository statisticRepository;
    private final TelegramService telegramService;

    public StatisticService(StatisticRepository statisticRepository, TelegramService telegramService) {
        this.statisticRepository = statisticRepository;
        this.telegramService = telegramService;
    }

    public void save(Action action, Long userId) {
        telegramService.findById(userId).ifPresent(user ->
                statisticRepository.save(new Statistic(action, user, user.getUsername())));
    }

    public void save(Action action, Integer userId) {
        telegramService.findById(Long.valueOf(userId)).ifPresent(user ->
                statisticRepository.save(new Statistic(action, user, user.getUsername())));
    }

}
