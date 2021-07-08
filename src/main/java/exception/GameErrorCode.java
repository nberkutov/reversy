package exception;

public enum GameErrorCode {
    POINTS_NOT_FOUND("Points not found"),
    GAME_NOT_FINISHED("Game is not finished yet."),
    BAD_POINT("Bad point."),
    INVALID_CELL(""),
    INVALID_PLAYER_COLOR(""),
    PLAYER_NOT_FOUND(""),
    INVALID_MOVE("Invalid move."),
    INVALID_REQUEST("Invalid request."),
    CELL_IS_EMPTY("Cell is empty.");
    private final String message;

    GameErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
