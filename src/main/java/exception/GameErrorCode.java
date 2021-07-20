package exception;

public enum GameErrorCode {
    POINTS_NOT_FOUND("Points not found"),
    GAME_ENDED("The game is already completed"),
    GAME_NOT_FINISHED("Game is not finished yet."),
    BAD_POINT("Bad point."),
    CONNECTION_LOST("Connection lost"),
    INVALID_CELL("Invalid cell."),
    INVALID_PLAYER_COLOR("Invalid player color."),
    PLAYER_NOT_FOUND("Player not found."),
    ILLEGAL_REQUEST("Illegal request"),
    INVALID_MESSAGE_DTO("Invalid message dto"),
    UNKNOWN_REQUEST("Unknown request"),
    UNKNOWN_RESPONSE("Unknown response"),
    PLAYER_ALREADY_PLAYING("Player already playing"),
    PLAYER_CANNOT_FIND_GAME("The player cannot start searching for the game"),
    GAME_NOT_FOUND("Game not found"),
    BOARD_NOT_FOUND("Board not found"),
    INVALID_MOVE("Invalid move."),
    INVALID_REQUEST("Invalid request."),
    INVALID_RESPONSE("Invalid response."),
    CELL_IS_EMPTY("Cell is empty.");
    private final String message;

    GameErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
