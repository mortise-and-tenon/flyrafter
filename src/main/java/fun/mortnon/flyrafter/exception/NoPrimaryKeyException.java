package fun.mortnon.flyrafter.exception;

/**
 * @author Moon Wu
 * @date 2021/4/23
 */
public class NoPrimaryKeyException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "No Primary key,check if your entity class contains @Id annotation in ";

    public NoPrimaryKeyException(String className) {
        super(DEFAULT_MESSAGE + className);
    }
}
