package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;
import static java.math.RoundingMode.HALF_UP;


public class Bot extends TelegramLongPollingBot {
    String operation;
    String operationMoney;
    boolean check;

    String nameMoney;
    int money;
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message message = update.getMessage(); //извлекли сообщение
                String chatId = message.getChatId().toString(); // извлекли чат айди
                String response = parseMess(message.getText());
                SendMessage outMessage = new SendMessage(); // наш ответ пользователю
                outMessage.setChatId(chatId);
                outMessage.setText(response);
                outMessage.setParseMode("HTML");
                // Создаем кнопки "Лонг" и "Шорт"
                if (message.getText().equals("/start")) {
                    outMessage.setReplyMarkup(getOperationKeyboard());
                }
                execute(outMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String parseMess(String textMess) {
        String response = null;
        if (textMess.equals("/start")) {
            response = "Бот умеет \n 1) Считать позиции (Лонг/Шорт)";
        } else if (textMess.equals("Лонг") || textMess.equals("Шорт")) {
            check=false;
            response = "Введите размер депозита (без плеча)";
            operation=textMess;
            operationMoney = "депозит";
        }  else if (!check && operationMoney.equals("депозит")){
            money = Integer.parseInt(textMess)*2;
            operationMoney=null;
            check=true;
            response = "Введите название монеты:";
        } else if (textMess.matches("[A-Za-z]+")) {
            nameMoney = textMess;
            response = "Введите точку входа:";
        } else if (isNumeric(textMess)) {
            double entryPoint = Double.parseDouble(textMess);

            int amount1, amount2, amount3;
            double cost2, cost3, averageCost, averageCost2,exitPrice, exitPrice2, profit, profit2, exitPrice1,profit1;
            if (operation.equals("Лонг")) {

                amount1 = (int) Math.ceil((money / entryPoint) * 0.3);
                exitPrice1 = 1.03 * entryPoint;
                BigDecimal resultExitPrice1 = new BigDecimal(exitPrice1).setScale(4,HALF_UP);

                cost2 = 0.95 * entryPoint;
                BigDecimal resultCost2 = new BigDecimal(cost2).setScale(4, HALF_UP);
                averageCost = (cost2 + entryPoint) / 2;
                amount2 = amount1;
                exitPrice = 1.05*averageCost;
                BigDecimal resultExitPrice =  new BigDecimal(exitPrice).setScale(4,HALF_UP);




                cost3 = 0.9 * entryPoint;
                BigDecimal resultCost3 = new BigDecimal(cost3).setScale(4, HALF_UP);
                amount3 = (int) ceil((money/ entryPoint) * 0.4);
                averageCost2 = (amount3 * cost3 + amount2 * cost2 + amount1 * entryPoint) / (amount1 + amount2 + amount3);
                exitPrice2 = 1.1 * averageCost2;
                BigDecimal resultExitPrice2 = new BigDecimal(exitPrice2).setScale(4, HALF_UP);


                response = ("<b> Монета: " + nameMoney + " </b> (Лонг) " + "\n" +
                        "\uD83D\uDFE2<b>Точка  входа 1.  --> </b> " + entryPoint +
                        "\n<b>Размер позиции: </b>" + amount1  + "\n" +
                        "<b>Точка выхода (3%): </b>" + resultExitPrice1 + "\n" + "\n"
                        + "\uD83D\uDFE2 \uD83D\uDFE2  <b> Точка входа 2. </b> " + resultCost2 +
                        "\n<b>Размер позиции: </b>" + amount2 +  "\n"
                        + "<b>Точка выхода (5%): </b>" + resultExitPrice + "\n" + "\n"
                        + "\uD83D\uDFE2 \uD83D\uDFE2 \uD83D\uDFE2  <b> Точка входа 3.</b>" + resultCost3+
                        "\n<b>Размер позиции: </b>" + amount3 +  "\n"
                        + "<b>Точка выхода (10%): </b>" + resultExitPrice2);

            } else if (operation.equals("Шорт")) {
                amount1 = (int) Math.ceil((money / entryPoint) * 0.3);
                exitPrice1 = 1.03 * entryPoint;
                BigDecimal resultExitPrice1 = new BigDecimal(exitPrice1).setScale(4, HALF_UP);

                cost2 = 1.05 * entryPoint;
                BigDecimal resultCost2 = new BigDecimal(cost2).setScale(4, HALF_UP);
                averageCost = (cost2 + entryPoint) / 2;

                amount2 = amount1;
                exitPrice = 0.95 * averageCost;
                BigDecimal resultExitPrice = new BigDecimal(exitPrice).setScale(4, HALF_UP);



                cost3 = 1.1 * entryPoint;
                BigDecimal resultCost3 = new BigDecimal(cost3);
                resultCost3 = resultCost3.setScale(4, HALF_UP);
                amount3 = (int) ceil((money / entryPoint) * 0.4);
                averageCost2 = (amount3 * cost3 + amount2 * cost2 + amount1 * entryPoint) / (amount1 + amount2 + amount3);
                exitPrice2 = 0.9 * averageCost2;
                BigDecimal resultExitPrice2 = new BigDecimal(exitPrice2).setScale(4, HALF_UP);



                response = ("Монета: " + nameMoney + "(Шорт)" + "\n" +
                        "\uD83D\uDD34 <b>Точка  входа 1. </b> " + entryPoint +
                        "\n<b>Размер позиции: </b>" + amount1  + "\n" +
                        "<b>Точка выхода (3%): </b>" + resultExitPrice1 + "\n" + "\n"
                        + "\uD83D\uDD34 \uD83D\uDD34 <b> Точка входа 2. </b> " + resultCost2 +
                        "\n<b>Размер позиции: </b>" + amount2 +  "\n"
                        + "<b>Точка выхода (5%): </b>" + resultExitPrice + "\n" + "\n"
                        + "\uD83D\uDD34 \uD83D\uDD34 \uD83D\uDD34 <b> Точка входа 3.</b>" + resultCost3+
                        "\n<b>Размер позиции: </b>" + amount3 +  "\n"
                        + "<b>Точка выхода (10%): </b>" + resultExitPrice2);
            }

        } else {
            response = "Введи название на английском или используй точку при вводе числа";
        }
        return response;
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // Метод создания кнопок "Лонг" и "Шорт" и "Конвертор валют"
    public static ReplyKeyboardMarkup getOperationKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Лонг");
        row.add("Шорт");
        keyboard.add(row);
        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);
        return markup;
    }


    @Override
    public String getBotUsername() {
        return "Papa_Ak_bot";
    }

    @Override
    public String getBotToken() {
        return "6270969692:AAElvvioZSRGftnZfCSAVHWf9c4gZ9U-umY";
    }
}



