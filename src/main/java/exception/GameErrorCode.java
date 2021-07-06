package exception;

public enum GameErrorCode {
    BAD_POINT(""),
    CELL_IS_EMPTY("");
    private final String message;

    GameErrorCode(String message) {
        this.message = message;
    }
}
