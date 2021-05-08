package fun.mortnon.flyrafter.exception;

/**
 * 代码未实现异常
 *
 * @author Moon Wu
 * @date 2021/4/25
 */
public class NotImplementException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "the method is not implement.";

    public NotImplementException() {
        super(DEFAULT_MESSAGE);
    }
}
