package controllers.commands;

import dto.response.*;
import dto.response.player.CreatePlayerResponse;
import exception.GameErrorCode;
import exception.GameException;
import services.JsonService;

public enum CommandResponse {
    ERROR("error", ErrorResponse.class),
    MESSAGE("message", MessageResponse.class),
    GAME_PLAYING("game_playing", GameBoardResponse.class),
    GAME_START("game_start", SearchGameResponse.class),
    CREATE_PLAYER("new_player", CreatePlayerResponse.class);

    private final String commandName;
    private final Class response;

    CommandResponse(final String commandName, Class response) {
        this.commandName = commandName;
        this.response = response;
    }

    boolean equalCommand(final String message) {
        return commandName.equals(message);
    }

    public Class getResponse() {
        return response;
    }

    static boolean isCommandMessage(final String message) {
        for (final CommandResponse commandResponse : values()) {
            if (commandResponse.equalCommand(message)) {
                return true;
            }
        }
        return false;
    }

    public static CommandResponse getCommandByResponse(GameResponse response) throws GameException {
        for (final CommandResponse commandResponse : values()) {
            if (commandResponse.getResponse().equals(response.getClass())) {
                return commandResponse;
            }
        }
        //TODO
        throw new GameException(GameErrorCode.INVALID_REQUEST);
    }

    public synchronized static String toJsonParser(GameResponse response) throws GameException {
        for (final CommandResponse commandResponse : values()) {
            if (commandResponse.getResponse().equals(response.getClass())) {
                StringBuilder builder = new StringBuilder();
                builder.append(commandResponse.commandName);
                builder.append(" ");
                builder.append(JsonService.toJson(response));
                return builder.toString();
            }
        }
        //TODO
        throw new GameException(GameErrorCode.INVALID_REQUEST);
    }

    public synchronized static GameResponse getResponseFromJson(String msg) throws GameException {
        String[] splits = msg.split(" ");
        for (final CommandResponse commandResponse : values()) {
            if (commandResponse.equalCommand(splits[0])) {
                String json = msg.substring(splits[0].length() + 1);
                return (GameResponse) JsonService.fromJson(json, commandResponse.getResponse());
            }
        }
        //TODO
        throw new GameException(GameErrorCode.INVALID_REQUEST);
    }
}
