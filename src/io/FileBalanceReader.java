package io;

import exception.NoInputFilesException;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileBalanceReader {

    public static Map<String, BigDecimal> readTxtFilesBalances(String dirPath) throws NoInputFilesException {
        //Список файлов из дирректории
        List<File> listFilesTxt = FileFinder.findTxtFilesInputs(dirPath);
        Map<String, BigDecimal> mapBalanses = new LinkedHashMap<>();
        for (File f : listFilesTxt) {
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
                String line;
                while ((line = bf.readLine()) != null) {
                    if (line.isBlank()) continue;
                    String lower = line.toLowerCase();
                    if (lower.contains("№п/п") || lower.contains("номер счета") || lower.contains("фио") || lower.contains("дата") || lower.contains("сумма")) {
                        continue;
                    }
                    //Вывод номера счета
                    Matcher m = Pattern.compile("^\\s*\\d+\\s+(\\d{5}-\\d{5})\\s+([-+]?\\d[\\d\\s]*(?:\\.\\d{2})?)\\s*$", Pattern.MULTILINE).matcher(line);
                    while (m.find()) {
                        String numberAccount = m.group(1);
                        BigDecimal balanceAccount = new BigDecimal(m.group(2).replaceAll("\\s+", "")).setScale(2, RoundingMode.HALF_UP);
                        mapBalanses.put(numberAccount, balanceAccount);
                    }
                }
            } catch (IOException e) {
                throw new NoInputFilesException("Ошибка чтения " + f.getName() + ": " + e.getMessage());
            }
        }
        System.out.println(mapBalanses.size());
        return mapBalanses;
    }
}
