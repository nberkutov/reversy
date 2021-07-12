package exception;

public enum GameErrorCode {
    POINTS_NOT_FOUND("Points not found"),
    GAME_NOT_FINISHED("Game is not finished yet."),
    BAD_POINT("Bad point."),
    CONNECTION_LOST(""),
    INVALID_CELL("Invalid cell."),
    INVALID_PLAYER_COLOR("Invalid player color."),
    PLAYER_NOT_FOUND("Player not found."),
    GAME_NOT_FOUND(""),
    BOARD_NOT_FOUND(""),
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
