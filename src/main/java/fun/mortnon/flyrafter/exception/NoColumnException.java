package fun.mortnon.flyrafter.exception;

/**
 * @author Moon Wu
 * @date 2021/4/23
 */
public class NoColumnException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "None attribute in the class ";

    public NoColumnException(String className) {
        super(DEFAULT_MESSAGE + className);
    }
}
