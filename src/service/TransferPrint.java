package service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Читает текстовый файл с переводами и выводит его содержимое в консоль.
 *
 * <p>Каждая непустая строка файла трактуется как один перевод и выводится как есть,
 * без разбора и без валидации — проверка формата лежит на вызывающем коде
 * (см. {@code io.TransferParse}).
 *
 * <p>Класс работает с файлом в два прохода: сначала считает непустые строки,
 * затем выводит их. Если файла нет, он пуст или содержит только пустые строки —
 * выводится соответствующее сообщение, а флаг {@link #isTransferts} сбрасывается в {@code false}.
 *
 * <p>Вывод сопровождается заголовком (зависит от категории), текущей датой и временем.
 *
 * <p>Побочные эффекты: запись в {@code System.out}, изменение статического поля
 * {@link #isTransferts}. Класс не потокобезопасен.
 */
public class TransferPrint {
    public static boolean isTransferts;

    public static void readAlltransfer(String outputStr, String category) {
        File file = new File(outputStr);
        int countLine = 0;
        try (BufferedReader fr = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fr.readLine()) != null) {
                if (!line.isEmpty()) countLine++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл с переводами не найден");
            return;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        if (countLine == 0) {
            isTransferts = false;
            System.out.println("Переводы не обнаружены");
            return;
        }
        printHeader(category);
        isTransferts = true;
        try (BufferedReader fr = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fr.readLine()) != null) {
                if (!line.isEmpty()) System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printHeader(String category) {
        if (category.equals("correctTransfers")) {
            System.out.println("Список успешно выполненных переводов");
        } else if (category.equals("errorTransfers")) {
            System.out.println("Список не выполненных переводов");
        } else {
            System.out.println("Список всех переводов");
        }
    }
}
