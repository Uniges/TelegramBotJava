package org.eugene.telegram.component;

import org.eugene.telegram.dao.Subscribe;
import org.eugene.telegram.util.BotUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Weather forecast newsletter
 */
@Component
public class SendMessageService {
    private final UserService userService;
    private final ApplicationContext applicationContext;

    public SendMessageService(UserService userService, ApplicationContext applicationContext) {
        this.userService = userService;
        this.applicationContext = applicationContext;
    }

    //@Scheduled(fixedRate = 15000)
    @Scheduled(cron = "0 0 10 * * *", zone = "Europe/Moscow")
    public void sendWeatherToSubscriber() {
        Bot bot = applicationContext.getBean(Bot.class);
        for (Subscribe subscribe : userService.getAllSubscribes()) {
            bot.sendMessageToUser
                    (subscribe.getChat_id(), BotUtil.getWeatherByCity(subscribe.getCity(), bot.weatherToken));
        }
    }
}
