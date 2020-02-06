package org.eugene.telegram.component;

import org.eugene.telegram.dao.Subscribe;
import org.eugene.telegram.util.BotUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represent telegram bot and communication with users
 */
@Component
@PropertySource("classpath:application.properties")
public class Bot extends TelegramLongPollingBot {
    private static ExecutorService executorsService = Executors.newFixedThreadPool(100);

    private static final String START_MESSAGE =
            "Hello. I'm weather bot. Send me your location and I'll send you weather.";
    private static final String HELP_MESSAGE =
            "Send me your location and I'll send you weather. And also you can subscribe.";
    private static final String SUBSCRIBE_RULE =
            "Send me the name of your city to sign up for the daily weather newsletter.\n" +
            "Correct input example:\n/s Moscow,\n/s Санкт-Петербург";

    private static final String FAILED_SEARCH = "Subscribe has failed.\n" +
                                                "You entered failed city:\n%s";
    private static final String UNSUBSCRIBE_SUCCESS = "You have unsubscribed.";
    private static final String SUBSCRIBE_SUCCESS = "You have subscribed.\n" +
                                                    "I'll send you weather forecast for %s.\n" +
                                                    "Every day at 10 AM, Moscow Time.";

    private final UserService userService;

    public Bot(UserService userService) {
        this.userService = userService;
    }

    @Value("${bot.username}")
    private String userName;

    @Value("${bot.token}")
    private String botToken;

    @Value("${weather.token}")
    String weatherToken;

    @Value("${bot.admin.username}")
    String adminUsername;

    /**
     * Method handles user requests
     * @param update - api Param
     */
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasLocation()) {
            executorsService.submit(
                    () ->
                        sendMessageToUser(message.getChatId(),
                                BotUtil.getWeatherByLocation(message.getLocation(), weatherToken))
            );
        }

        else if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/help":
                    sendMessageToUser(message.getChatId(), HELP_MESSAGE);
                    break;
                case "/start":
                    sendMessageToUser(message.getChatId(), START_MESSAGE);
                    break;
                case "/subscribe":
                    sendMessageToUser(message.getChatId(), SUBSCRIBE_RULE);
                    break;
                case "/unsubscribe":
                    userService.deleteSubscriber(message.getChatId());
                    sendMessageToUser(message.getChatId(), UNSUBSCRIBE_SUCCESS);
                    break;
                case "/subs":
                    if (message.getFrom().getUserName() != null &&
                            message.getFrom().getUserName().equals(adminUsername)) {
                        for (Subscribe s : userService.getAllSubscribes()) {
                            sendMessageToUser(message.getChatId(), String.format
                                    ("Username: %s\n City: %s", s.getUserName(), s.getCity()));
                        }
                    }
                    else {
                        sendMessageToUser(message.getChatId(), "You have no permissions to use this command!");
                    }
                    break;
                default:
                    if (message.getText().startsWith("/s ")) {
                        String city = null;
                        try {
                            city = message.getText().split(" ")[1];
                        } catch (IndexOutOfBoundsException e) {
                            break;
                        }
                        try {
                            String infoAboutCity = BotUtil.getWeatherByCity(city, weatherToken);
                        } catch (NullPointerException ex) {
                            sendMessageToUser(message.getChatId(), String.format(FAILED_SEARCH, city));
                            break;
                        }
                        Subscribe subscribe = new Subscribe(message.getChatId(), message.getFrom().getUserName(), city);
                        userService.addSubscriber(subscribe);
                        sendMessageToUser(message.getChatId(), String.format(SUBSCRIBE_SUCCESS, city));
                    }
            }
        }
    }

    public void sendMessageToUser(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/subscribe"));
        keyboardFirstRow.add(new KeyboardButton("/unsubscribe"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

    }

    public String getBotUsername() {
        return this.userName;
    }

    public String getBotToken() {
        return this.botToken;
    }
}
