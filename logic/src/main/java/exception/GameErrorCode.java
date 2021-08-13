package exception;

public enum GameErrorCode {
    FILE_PATH_INVALID("The file path is not correct"),
    AI_ERROR("The Ai returned an error"),
    SAVE_FILE_ERROR("The file cannot be saved"),
    UPLOAD_FILE_ERROR("The file cannot be upload"),
    SERVER_NOT_STARTED("Server not started"),
    POINTS_NOT_FOUND("Points not found"),
    GAME_ENDED("The game is already completed"),
    GAME_NOT_FINISHED("Game is not finished yet."),
    BAD_POINT("Bad point."),
    CONNECTION_LOST("Connection lost"),
    INVALID_NICKNAME("Invalid nickname"),
    NICKNAME_ALREADY_USED("Nickname already used"),
    INVALID_CELL("Invalid cell."),
    INVALID_PLAYER_COLOR("Invalid player color."),
    PLAYER_NOT_FOUND("Player not found."),
    ILLEGAL_REQUEST("Illegal request"),
    INVALID_MESSAGE_DTO("Invalid message dto"),
    UNKNOWN_REQUEST("Unknown request"),
    UNKNOWN_RESPONSE("Unknown response"),
    PLAYER_IS_AUTH("Player is authorized"),
    PLAYER_CANT_PERFORM("Player can't perform this action"),
    PLAYER_ALREADY_PLAYING("Player already playing"),
    PLAYER_CANNOT_FIND_GAME("The player cannot start searching for the game"),
    GAME_NOT_FOUND("Game not found"),
    GAME_RESULT_NOT_FOUND("Game result not found"),
    ROOM_NOT_FOUND("Room not found"),
    ROOM_IS_CLOSED("Room closed"),
    BOARD_NOT_FOUND("Board not found"),
    INVALID_MOVE("Invalid move."),
    INVALID_REQUEST("Invalid request."),
    INVALID_RESPONSE("Invalid response."),
    CELL_IS_EMPTY("Cell is empty."),
    SOCKET_IMPOSSIBLE_READ_MSG("The socket has an error"),
    SERVER_ERROR("Server not responding");
    private final String message;

    GameErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
