package exception;

public enum GameErrorCode {
    BAD_POINT(""),
    POINTS_NOT_FOUND("Points not found"),
    INVALID_MOVE(""),
    INVALID_REQUEST(""),
    CELL_IS_EMPTY("");
    private final String message;

    GameErrorCode(String message) {
        this.message = message;
    }
}
