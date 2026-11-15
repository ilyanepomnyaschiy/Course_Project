package io;

import exception.NoInputFilesException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileTransferReader {
    /**
     * Читает все .txt-файлы из директории {@code dirPath} и возвращает
     * список содержательных строк, к каждой из которых дописано имя файла-источника.
     * <p>
     * Алгоритм работы:
     * Получает список файлов через {@link FileFinder#findTxtFilesInputs(String)
     * Для каждого файла открывает {@link BufferedReader} с кодировкой UTF-8.
     * Пропускает пустые строки ({@link String#isBlank()}
     * Пропускает строки-заголовки: содержащие слова
     * «номер счета», «фио», «дата», «сумма» (без учёта регистра).
     * К остальным строкам добавляет {@code " " + имя файла + "\n"}
     * и складывает их в результирующий список.
     * * Ошибки чтения отдельных файлов ({@link IOException}) не прерывают
     * обработку остальных файлов — сообщение об ошибке выводится в консоль,
     * и цикл продолжается.
     *
     * @param dirPath путь к директории, в которой ищутся {@code .txt}-файлы.
     *                Не может быть {@code null}.
     * @return список строк с добавленным именем файла-источника в конце каждой строки;
     * пустой список, если подходящих файлов или содержательных строк нет.
     * Никогда не возвращает {@code null}.
     * @throws NoInputFilesException если в директории {@code dirPath}
     *                               не найдено ни одного {@code .txt}-файла
     *                               (пробрасывается из {@link FileFinder#findTxtFilesInputs(String)}).
     */


    public static Map<String, List<String>> readTxtFilesInputs(String dirPath) throws NoInputFilesException {

        //Список файлов из дирректории
        List<File> listFilesTxt = FileFinder.findTxtFilesInputs(dirPath);
        Map<String, List<String>> mapTransfers = new LinkedHashMap<>();
        for (File f : listFilesTxt) {
            List<String> listFileText = new ArrayList<>();
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
                String line;
                while ((line = bf.readLine()) != null) {
                    if (line.isBlank()) continue;
                    String lower = line.toLowerCase();
                    if (lower.contains("№п/п") || lower.contains("номер счета") || lower.contains("фио") || lower.contains("дата") || lower.contains("сумма")) {
                        continue;
                    }
                    listFileText.add(line);
                }
                mapTransfers.put(f.getName(), listFileText);
            } catch (IOException e) {
                throw new NoInputFilesException("Ошибка чтения " + f.getName() + ": " + e.getMessage() + ". Парсинг не возможно произвести.");
            }
        }
        return mapTransfers;
    }
}
