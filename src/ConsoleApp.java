import util.Constants;
import exception.NoInputFilesException;
import io.FileTransferReader;
import io.TransferProcessor;
import service.TransferPrint;
import service.TransferWriter;

import java.util.*;

public class ConsoleApp {
    /**
     * Консольный интерфейс приложения.
     * ...
     */
    public final Scanner sc = new Scanner(System.in);
    public static List<String> getTransferParse;
    TransferProcessor processor = new TransferProcessor();
    TransferWriter writer = new TransferWriter();

    public void launchConsole() {
        // Основное меню
        while (true) {
            System.out.println("Введите команду (0, 1 или 2):");
            System.out.println("  0 — выход");
            System.out.println("  1 — парсинг файлов перевода из папки input");
            System.out.println("  2 — вывод списка всех переводов из файла-отчёта");
            try {
                int numberComand = sc.nextInt();
                if (numberComand == 1) {
                    try {
                        Map<String, List<String>> listTransfer = FileTransferReader.readTxtFilesInputs(Constants.INPUT_DIR_PATH);
                        getTransferParse = processor.getValidTransfers(listTransfer);

                    } catch (NoInputFilesException e) {
                        System.out.println("Ошибка чтения входных файлов: " + e.getMessage());
                        return; // без данных запускать меню бессмысленно
                    }
                    TransferWriter.writeAllTransfer(Constants.OUTPUT_FILES_PATH, getTransferParse);
                    TransferWriter.writeStatictick(Constants.OUTPUT_FILES_PATH, writer.formStatictick("all", TransferWriter.Transferstats.snapshot(processor)));
                    // подменю
                    while (true) {
                        System.out.println("Введите команду (0 или 2):");
                        System.out.println("  0 — выход");
                        System.out.println("  2 — вывод списка всех переводов из файла-отчёта");
                        try {
                            int numberComand2 = sc.nextInt();
                            if (numberComand2 == 0) {
                                System.out.println("Выход из программы");
                                return;
                            } else if (numberComand2 == 2) {
                                TransferPrint.readAlltransfer(Constants.OUTPUT_FILES_PATH, "allTransfer");
                                if (TransferPrint.isTransferts) {
                                    parseCorrectAndErrorsTransfer();
                                } else {
                                    System.out.println("Переводы не обнаружены");
                                    return;
                                }
                            } else {
                                System.out.println("Введена неверная команда. Повторите ввод.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Необходимо ввести число");
                            sc.nextLine();
                        }
                    }
                } else if (numberComand == 2) {
                    TransferPrint.readAlltransfer(Constants.OUTPUT_FILES_PATH, "allTransfer");
                    if (TransferPrint.isTransferts) {
                        readerCorrectAndErrorsTransfer();
                    } else {
                        return;
                    }
                } else if (numberComand == 0) {
                    System.out.println("Выход из программы");
                    return;

                } else {
                    System.out.println("Введена неверная команда. Повторите ввод.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Необходимо ввести число");
                sc.nextLine();
            }
        }
    }

    public void parseCorrectAndErrorsTransfer() {
        sc.nextLine();
        String userChoice;
        do {
            System.out.println("Сформировать файлы с успешными и неуспешными переводами (y/n)?");
            userChoice = sc.nextLine().trim().toLowerCase();
            if (userChoice.equalsIgnoreCase("y")) {
                try {
                    writer.writeCompletedTransfer(processor, getTransferParse);
                } catch (NoInputFilesException e) {
                    System.out.println("Файл не найден");
                }
                try {
                    writer.writeFailedTransfer(processor, getTransferParse);
                } catch (NoInputFilesException e) {
                    System.out.println("Файл не найден");
                }
                readerCorrectAndErrorsTransfer();

            } else if (userChoice.equalsIgnoreCase("n")) {
                System.exit(0);
            } else System.out.println("Введена неверная команда");
        }
        while (!userChoice.toLowerCase().equals("y") && !userChoice.equals("n"));
    }

    public void readerCorrectAndErrorsTransfer() {
        while (true) {
            System.out.println("Введите команду (0, 1 или 2):");
            System.out.println("  0 — выход");
            System.out.println("  1 — вывод успешно выполненных переводов");
            System.out.println("  2 — вывод ошибочных переводов");
            try {
                int userChoice2 = sc.nextInt();
                sc.nextLine();
                if (userChoice2 == 0) {
                    System.out.println("Выход из программы");
                    System.exit(0);
                } else if (userChoice2 == 1) {
                    TransferPrint.readAlltransfer(Constants.CORRECT_TRANSFERS_FILES_PATH, "correctTransfers");
                    System.exit(0);
                } else if (userChoice2 == 2) {
                    TransferPrint.readAlltransfer(Constants.ERROR_TRANSFERS_FILES_PATH, "errorTransfers");
                    System.exit(0);
                } else {
                    System.out.println("Введена неверная команда. Повторите ввод.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Необходимо ввести число");
                sc.nextLine();
            }
        }
    }
}
