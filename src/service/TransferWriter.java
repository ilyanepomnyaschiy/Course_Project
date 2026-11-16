package service;

import exception.NoInputFilesException;
import io.TransferProcessor;
import util.Constants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Записывает результаты обработки переводов в выходные файлы.
 *
 * <p>Отвечает за три операции:
 * <ul>
 *   <li>{@link #writeAllTransfer(String, List)} — создаёт/перезаписывает файл и пишет в него
 *       заголовок отчёта и тело (список строк переводов);</li>
 *   <li>{@link #outputStatictick(String)} — дописывает в существующий файл разделитель
 *       и три строки со сводной статистикой (всего / успешных / ошибочных);</li>
 *   <li>{@link #writeCompletedTransfer(List)} и {@link #writeFailedTransfer(List)} —
 *       получают из {@link TransferProcessor} отфильтрованные списки и сохраняют их
 *       в предопределённые файлы из {@link Constants}.</li>
 * </ul>
 *
 * <p><b>Важно:</b> статистика и тексты сообщений формируются из счётчиков
 * {@link TransferProcessor#getCountIsTransferCompleted()} и {@link TransferProcessor#getCountIsTransferFailed()}.
 * Тексты должны вычисляться в момент вызова {@code parseStatisctick}, а не инициализироваться
 * на уровне статических полей класса — иначе значения зафиксируются на момент загрузки
 * класса (то есть, как правило, до начала парсинга) и будут нулевыми.
 *
 * <p>Все методы подавляют {@link IOException}: при ошибке записи выводится сообщение
 * в {@code System.out}, исключение наружу не пробрасывается. Вызывающий код не получает
 * сигнала о неудаче записи.
 */
public class TransferWriter {
    public static void writeAllTransfer(String outputStr, List<String> transfer) {
        try {
            Files.write(Path.of(outputStr), List.of(Constants.HEADER1), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(Path.of(outputStr), transfer, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            System.out.println("Парсинг файлов успешно завершен: " + outputStr);
        } catch (IOException e) {
            System.out.println("Ошибка записи файла: " + e.getMessage());
        }
    }

    //Метод для записи распаршенных файлов
    public static void writaFilesParse(String nameFile, List<String> transfer) {
        int dot = nameFile.lastIndexOf('.');
        String base = (dot <= 0) ? nameFile : nameFile.substring(0, dot);
        String inputParse = "resources/archive/" + base + "Parse.txt";
        TransferWriter.writeAllTransfer(inputParse, transfer);
    }

    public record Transferstats(int total, int completed, int failed) {
        public static Transferstats snapshot(TransferProcessor processor) {
            int countCompletedTransfer = processor.getCountIsTransferCompleted();
            int countFailedTransfer = processor.getCountIsTransferFailed();
            return new Transferstats(countFailedTransfer + countCompletedTransfer, countCompletedTransfer, countFailedTransfer);
        }
    }

    public List<String> formStatictick(String category, Transferstats stats) {
        List<String> lines = new ArrayList<>();
        lines.add(Constants.STATISTIC_SEPARATOR);
        if (category.equalsIgnoreCase("all")) {
            lines.add("Общее количество переводов: " + stats.total);
        }
        if (category.equalsIgnoreCase("all") || category.equalsIgnoreCase("completed")) {
            lines.add("Общее количество выполненных переводов: " + stats.completed);
        }
        if (category.equalsIgnoreCase("all") || category.equalsIgnoreCase("failed")) {
            lines.add("Общее количество не выполненных переводов: " + stats.failed);
        }
        lines.add("Дата: " + LocalDate.now().format(Constants.DATE_FMT));
        lines.add("Время: " + LocalTime.now().format(Constants.TIME_FMT));
        return lines;
    }

    public static void writeStatictick(String outputStr, List<String> lines) {
        try {
            Files.write(Path.of(outputStr), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.out.println("Ошибка записи файла: " + e.getMessage());
        }
    }

    public void writeCompletedTransfer(TransferProcessor processor, List<String> transfers) throws NoInputFilesException {
        List<String> listTransfersCorrect = processor.getCompletedTransfers(transfers);
        System.out.print("Файл с корректными переводами: ");
        writeAllTransfer(Constants.CORRECT_TRANSFERS_FILES_PATH, listTransfersCorrect);
        writeStatictick(Constants.CORRECT_TRANSFERS_FILES_PATH, formStatictick("completed", Transferstats.snapshot(processor)));
    }

    public void writeFailedTransfer(TransferProcessor processor, List<String> transfers) throws NoInputFilesException {
        List<String> listTransfersErrors = processor.getFailedTransfers(transfers);
        System.out.print("Файл с ошибочными переводами: ");
        writeAllTransfer(Constants.ERROR_TRANSFERS_FILES_PATH, listTransfersErrors);
        writeStatictick(Constants.ERROR_TRANSFERS_FILES_PATH, formStatictick("failed", Transferstats.snapshot(processor)));
    }
}
