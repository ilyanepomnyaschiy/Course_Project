package exception;
/**
 * Сигнализирует, что входная директория успешно прочитана,
 * но не содержит ни одного файла с расширением txt.
 */
public class NoInputFilesException extends AppException{
    public NoInputFilesException(String message) {
        super(message);
    }
}
