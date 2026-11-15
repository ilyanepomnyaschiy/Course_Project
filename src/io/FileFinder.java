package io;

import exception.NoInputFilesException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileFinder {
    /**
     * Выполняет поиск файлов с расширением txt в директории с указанным путём.
     * Сканирование производится только на верхнем уровне директории —
     * вложенные подкаталоги не обходятся. Проверка расширения выполняется
     * без учёта регистра,
     *
     * @param dirPath путь к директории, в которой выполняется поиск файлов.
     *                Список никогда не пустой: если файлы не найдены, будет выброшено
     *                исключение NoInputFilesException}.
     * @throws NoInputFilesException если директория по указанному пути не существует
     *                               либо в ней отсутствуют файлы с расширением txt
     */

    public static List<File> findTxtFilesInputs(String dirPath) throws NoInputFilesException  {
        List<File> listTxtFiles = new ArrayList<>();
        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            throw new NoInputFilesException("Директория " + dirPath + " не обнаружена. Парсинг не возможно произвести.");
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
        if (files != null) {
            listTxtFiles.addAll(Arrays.asList(files));
        }
        if (listTxtFiles.isEmpty()) {
            throw new NoInputFilesException("В " + dirPath + " нет входных файлов с расширением txt. Парсинг не возможно произвести.");
        }
        return listTxtFiles;
    }


}
