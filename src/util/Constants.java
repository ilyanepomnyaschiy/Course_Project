package util;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String INPUT_DIR_PATH = "resources/input/transfers";
    public static final String INPUT_BALANCE_DIR_PATH = "resources/input/accounts";
    public static final String UPDATE_BALANCE_DIR_PATH = "resources/input/accounts/balance.txt";
    public static final String OUTPUT_FILES_PATH = "resources/archive/allTransfer.txt";
    public static final String CORRECT_TRANSFERS_FILES_PATH = "resources/archive/completedTransfer.txt";
    public static final String ERROR_TRANSFERS_FILES_PATH = "resources/archive/failedTransfer.txt";
    public static final String HEADER1 = "Наименование файла Дата ФИО плательщика Номер счета списания ФИО получателя Номер счета зачисления Сумма Валюта платежа Примечание";
    public static final String HEADER2 = "№п/п Номер счета Остаток баланса, RUB";
    public static final String STATISTIC_SEPARATOR = "-------------------------------------------------------------------------------------------------";
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

}
