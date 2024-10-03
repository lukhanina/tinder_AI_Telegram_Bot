package com.javarush.telegram;

import com.javarush.telegram.ChatGPTService;
import com.javarush.telegram.DialogMode;
import com.javarush.telegram.MultiSessionTelegramBot;
import com.javarush.telegram.UserInfo;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "hanna_tinder_ai_bot";
    public static final String TELEGRAM_BOT_TOKEN = "token";
    public static final String OPEN_AI_TOKEN = "token";

    private ChatGPTService chatGPT = new ChatGPTService(OPEN_AI_TOKEN);

    private DialogMode currentMode = null;

    private ArrayList<String> list = new ArrayList<>();

    private UserInfo me;

    private UserInfo she;

    private int questionCount;

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        String message = getMessageText();

        if (message.equals("/start")) {
            currentMode = DialogMode.MAIN;
            sendPhotoMessage("main");
            String text = loadMessage("main");
            sendTextMessage(text);
            showMainMenu("main menu of the bot", "/start", "generate a Tinder profile \uD83D\uDE0E", "/profile", "opening message for dating \uD83E\uDD70", "/opener", "conversation on your behalf \uD83D\uDE08", "/message", "conversation with celebrities \uD83D\uDD25", "/date", "ask a question to the GPT chat \uD83E\uDDE0", "/gpt");
            return;
        };

        if (message.equals("/gpt")) {
            currentMode = DialogMode.GPT;
            sendPhotoMessage("gpt");
            String text = loadMessage("gpt");
            sendTextMessage(text);
            return;
        };

        if (currentMode == DialogMode.GPT & !isMessageCommand()) {
            String prompt = loadPrompt("gpt");
            Message msg = sendTextMessage("Please, wait, ChatGPT thinks \uD83E\uDDE0...");
            String answer = chatGPT.sendMessage(prompt, message);
            updateTextMessage(msg, answer);
            return;
        };

        if (message.equals("/date")) {
            currentMode = DialogMode.DATE;
            sendPhotoMessage("date");
            String text = loadMessage("date");
            sendTextButtonsMessage(text, "Ryan Gosling", "date_gosling",
                    "Ariana Grande", "date_grande",
                    "Margot Robbie", "date_robbie",
                    "Zendaya", "date_zendaya",
                    "Tom Hardy", "date_hardy");
            return;
        };

        if (currentMode == DialogMode.DATE & !isMessageCommand()) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("date_")) {
                sendPhotoMessage(query);
                sendTextMessage("Nice choose! You need to ask to date a celebrity with 5 messages");
                String prompt = loadPrompt(query);
                chatGPT.setPrompt(prompt);
                return;
            }
            Message msg = sendTextMessage("Please, wait, He/she is typing \uD83E\uDDE0...");
            String answer = chatGPT.addMessage(message);
            updateTextMessage(msg, answer);
            return;
        };

        if (message.equals("/message")) {
            currentMode = DialogMode.MESSAGE;
            sendPhotoMessage("message");
            sendTextButtonsMessage("Send to the chat your correspondence",
                    "Next message", "message_next",
                    "Ask to date", "message_date");
            String text = loadMessage("message");
            sendTextMessage(text);
            return;
        };

        if (currentMode == DialogMode.MESSAGE & !isMessageCommand()) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("message_")) {
                String prompt = loadPrompt(query);
                String userChatHistory = String.join("\n\n", list);
                Message msg = sendTextMessage("Please, wait, ChatGPT thinks \uD83E\uDDE0...");
                String answer = chatGPT.sendMessage(prompt, userChatHistory);
                updateTextMessage(msg, answer);
                return;
            }
            list.add(message);
            return;
        };

        if (message.equals("/profile")) {
            currentMode = DialogMode.PROFILE;
            sendPhotoMessage("profile");

            me = new UserInfo();
            questionCount = 1;

            String text = loadMessage("profile");
            sendTextMessage(text);
            sendTextMessage("How old are you?");
            return;
        };

        if (currentMode == DialogMode.PROFILE & !isMessageCommand()) {
            switch (questionCount) {
                case 1:
                    me.age = message;
                    questionCount = 2;
                    sendTextMessage("What do you do for a living?");
                    return;
                case 2:
                    me.occupation = message;
                    questionCount = 3;
                    sendTextMessage("Do you have hobbies?");
                    return;
                case 3:
                    me.hobby = message;
                    questionCount = 4;
                    sendTextMessage("What do you dislike about people?");
                    return;
                case 4:
                    me.annoys = message;
                    questionCount = 5;
                    sendTextMessage("Goal of dating?");
                    return;
                case 5:
                    me.goals = message;

                    String aboutMyself = me.toString();
                    String prompt = loadPrompt("profile");
                    Message msg = sendTextMessage("Please, wait, ChatGPT thinks \uD83E\uDDE0...");
                    String answer = chatGPT.sendMessage(prompt, aboutMyself);
                    updateTextMessage(msg, answer);
                    return;
            }
            return;
        };

        if (message.equals("/opener")) {
            currentMode = DialogMode.OPENER;
            sendPhotoMessage("opener");

            she = new UserInfo();
            questionCount = 1;

            String text = loadMessage("opener");
            sendTextMessage(text);
            sendTextMessage("What is her name?");
            return;
        };

        if (currentMode == DialogMode.OPENER & !isMessageCommand()) {
            switch (questionCount) {
                case 1:
                    she.name = message;
                    questionCount = 2;
                    sendTextMessage("How old is she / he?");
                    return;
                case 2:
                    she.age = message;
                    questionCount = 3;
                    sendTextMessage("Does she / he have hobbies and what?");
                    return;
                case 3:
                    she.hobby = message;
                    questionCount = 4;
                    sendTextMessage("What does she / he do for living?");
                    return;
                case 4:
                    she.occupation = message;
                    questionCount = 5;
                    sendTextMessage("What is her / his goal of dating?");
                    return;
                case 5:
                    she.goals = message;

                    String aboutFriend = she.toString();
                    String prompt = loadPrompt("opener");
                    Message msg = sendTextMessage("Please, wait, ChatGPT thinks \uD83E\uDDE0...");
                    String answer = chatGPT.sendMessage(prompt, aboutFriend);
                    updateTextMessage(msg, answer);
                    return;
            }
            return;
        };

        sendTextMessage("*Hi there!*");
        sendTextMessage("_How it's going?_");
        sendTextMessage("You wrote " + message);
        showMainMenu("main menu of the bot", "/start", "generate a Tinder profile \uD83D\uDE0E", "/profile", "opening message for dating \uD83E\uDD70", "/opener", "conversation on your behalf \uD83D\uDE08", "/message", "conversation with celebrities \uD83D\uDD25", "/date", "ask a question to the GPT chat \uD83E\uDDE0", "/gpt");
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
