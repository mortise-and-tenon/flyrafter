package fun.mortnon.flyrafter.exception;

/**
 * @author Moon Wu
 * @date 2021/4/25
 */
public class NoTargetFolderException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "can't get flyway sql folder.";

    public NoTargetFolderException() {
        super(DEFAULT_MESSAGE);
    }
}
