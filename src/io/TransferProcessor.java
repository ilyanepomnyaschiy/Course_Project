package io;

import exception.NoInputFilesException;
import util.Constants;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static util.Constants.INPUT_BALANCE_DIR_PATH;
import static util.Constants.UPDATE_BALANCE_DIR_PATH;

public class TransferProcessor {
    /**
     * Валидатор строк банковских переводов.
     * Класс принимает список строк текстового файла, каждая из которых описывает
     * один платёж, и проверяет её на соответствие формату. Строка считается
     * корректной, если она проходит все проверки: дата, ФИО плательщика и
     * получателя, номера счетов, сумма перевода и код валюты.
     */
    //Паттерн строки
    private static final String patternData = "(\\s*(?<day>0[1-9]|[12][0-9]|3[01])\\.(?<month>0[1-9]|1[0-2])\\.(?<year>20\\d{2})\\s*)";
    private static final String fioPayer = "\\s*(?<payerSurname>[A-Za-zА-Яа-яёЁ]+)\\s+(?<payerName>[A-Za-zА-Яа-яёЁ]+)(?:\\s+(?<payerPatronymic>[A-Za-zА-Яа-яёЁ]+))?\\s*";
    private static final String accountPayer = "\\s*(?<payerAccount>.\\S+-\\S+)\\s*";
    private static final String fioRecipient = "\\s*(?<recipientSurname>[A-Za-zА-Яа-яёЁ]+)\\s+(?<recipientName>[A-Za-zА-Яа-яёЁ]+)(?:\\s+(?<recipientPatronymic>[A-Za-zА-Яа-яёЁ]+))?\\s*";
    private static final String accountRecipient = "\\s*(?<recipientAccount>\\S+-\\S+)\\s*";
    private static final String amounTransfer = "\\s*(?<amount>-?\\d+\\.\\d{2})\\s*";
    private static final String currentPayment = "\\s*(?<currency>[A-Z]{3})\\s*";
    private static final Pattern pattern = Pattern.compile(patternData + fioPayer + accountPayer + fioRecipient + accountRecipient + amounTransfer + currentPayment);
    private List<String> listTransfersValid = new ArrayList<>();
    private static boolean transferIsFailed;
    private static String msgSuccessTransfer;
    public static Map<String, BigDecimal> upDateBalance = new LinkedHashMap<>();
    private int countIsTransferCompleted;
    private int countIsTransferFailed;
    public int getCountIsTransferCompleted() {
        return countIsTransferCompleted;
    }
    public int getCountIsTransferFailed() {
        return countIsTransferFailed;
    }

    public  List<String> getValidTransfers(Map<String, List<String>> listLine) throws NoInputFilesException {
        countIsTransferCompleted = 0;
        countIsTransferFailed = 0;
                //Чтение файла с остатками счетов
        Map<String, BigDecimal> mapBalanceAccounts = FileBalanceReader.readTxtFilesBalances(INPUT_BALANCE_DIR_PATH);
        for (Map.Entry<String, List<String>> str : listLine.entrySet()) {
            String nameFile = str.getKey();
            List<String> listTransfer = str.getValue();

            for (String transfer : listTransfer) {
                String clean = transfer.replaceFirst("\\s+\\S+\\.txt\\s*$", "");
                Matcher m = pattern.matcher(clean);
                List<String> errors = new ArrayList<>();
                String payerAccount = null;
                String recipientAccount = null;
                BigDecimal amount = null;
                BigDecimal newBalancePayer = null;
                BigDecimal newBalanceRecipient = null;

                if (!m.matches()) {
                    errors.add("Неверный формат данных");
                } else {
                    // Дата
                    int day = Integer.parseInt(m.group("day"));
                    int month = Integer.parseInt(m.group("month"));
                    int year = Integer.parseInt(m.group("year"));
                    try {
                        LocalDate.of(year, month, day);
                    } catch (DateTimeException e) {
                        errors.add("Даты " + m.group("day") + "." + m.group("month") + "." + m.group("year") + " не существует");
                    }

                    // ФИО
                    String payerSurname = m.group("payerSurname");
                    String payerName = m.group("payerName");
                    String payerPatronymic = m.group("payerPatronymic");
                    String recipientSurname = m.group("recipientSurname");
                    String recipientName = m.group("recipientName");
                    String recipientPatronymic = m.group("recipientPatronymic");
                    if (payerSurname.length() < 2) errors.add("Фамилия плательщика слишком короткая");
                    if (payerName.length() < 2) errors.add("Имя плательщика слишком короткое");
                    if (payerPatronymic != null) {
                        String p = payerPatronymic.toLowerCase();
                        if (!(p.endsWith("ич") || p.endsWith("на"))) {
                            errors.add("Отчество плательщика с ошибкой");
                        }
                    }

                    if (recipientSurname.length() < 2) errors.add("Фамилия получателя слишком короткая");
                    if (recipientName.length() < 2) errors.add("Имя получателя слишком короткое");
                    if (recipientPatronymic != null) {
                        String p = recipientPatronymic.toLowerCase();
                        if (!(p.endsWith("ич") || p.endsWith("на"))) {
                            errors.add("Отчество получателя с ошибкой");
                        }
                    }

                    // Проверка счетов
                    payerAccount = m.group("payerAccount");
                    //Сумма баланса на счете плательщика
                    BigDecimal sumBalancePayer = mapBalanceAccounts.get(payerAccount);
                    if (mapBalanceAccounts.containsKey(payerAccount)) {
                        if (!payerAccount.matches("\\d{5}-\\d{5}")) {
                            errors.add("Счет плательщика не соответствует формату.");
                        }
                    } else errors.add("Счета плательщика не существует");

                    recipientAccount = m.group("recipientAccount");
                    //Сумма баланса на счете получателя
                    if (mapBalanceAccounts.containsKey(recipientAccount)) {
                        if (!recipientAccount.matches("\\d{5}-\\d{5}")) {
                            errors.add("Счет получателя не соответствует формату.");
                        }
                    } else errors.add("Счета получателя не существует");


                    // Сумма
                    try {
                        amount = new BigDecimal(m.group("amount"));
                        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                            errors.add("Сумма должна быть больше нуля");
                        }
                        if (sumBalancePayer != null && amount.compareTo(sumBalancePayer) > 0) {
                            errors.add("На счете плательщика недостаточно средств");
                        }


                    } catch (NumberFormatException e) {
                        errors.add("Некорректная сумма");
                    }

                    // Валюта
                    String currency = m.group("currency");
                    if (!currency.equals("RUR") && !currency.equals("USD") && !currency.equals("EUR")) {
                        errors.add("Указан неверный код валюты.");
                    }
                }

                if (errors.isEmpty()) {
                    msgSuccessTransfer = "Перевод выполнен";
                    transferIsFailed = true;
                    countIsTransferCompleted++;
                    listTransfersValid.add(nameFile + " " + transfer + " " + msgSuccessTransfer);
                    //Обновление файла с балансом
                    newBalancePayer = mapBalanceAccounts.get(payerAccount).subtract(amount);
                    newBalanceRecipient = mapBalanceAccounts.get(recipientAccount).add(amount);
                    updateBalance(mapBalanceAccounts, payerAccount, newBalancePayer);
                    updateBalance(mapBalanceAccounts, recipientAccount, newBalanceRecipient);
                    updateFileBalance(UPDATE_BALANCE_DIR_PATH, mapBalanceAccounts);


                } else {
                    msgSuccessTransfer = "Перевод не выполнен";
                    transferIsFailed = false;
                    countIsTransferFailed++;
                    listTransfersValid.add(nameFile + " " + transfer + " " + msgSuccessTransfer + " " + errors);

                }

            }
        }
        return listTransfersValid;
    }


    //Метод для формирования коллекции выполненных переводов
    public List<String> getCompletedTransfers(List<String> listLine) {
        return listLine.stream()
                .filter(l -> l.contains("Перевод выполнен"))
                .collect(Collectors.toList());

    }

    //Метод для формирования коллекции не выполненных переводов
    public List<String> getFailedTransfers(List<String> listLine) {
        return listLine.stream()
                .filter(l -> l.contains("Перевод не выполнен"))
                .collect(Collectors.toList());
    }

    //Метод для обновления баланса

    public static Map<String, BigDecimal> updateBalance(Map<String, BigDecimal> mapBalance, String account, BigDecimal balance) {
        mapBalance.put(account, balance);
        return mapBalance;
    }

    //Метод для перезаписи файла с балансом
    public static void updateFileBalance(String outputStr, Map<String, BigDecimal> mapBalance) {
        AtomicInteger countLineWrite = new AtomicInteger(0);
        List<String> linesToWrite = mapBalance.entrySet().stream()
                .map(e -> countLineWrite.incrementAndGet() + " " + e.getKey() + " " + e.getValue())
                .collect(Collectors.toList());
        try {
            Files.write(Path.of(outputStr),
                    List.of(Constants.HEADER2),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(Path.of(outputStr), linesToWrite, StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Ошибка записи файла: " + e.getMessage());
        }

    }

}
