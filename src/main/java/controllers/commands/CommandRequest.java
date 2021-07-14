package controllers.commands;

import dto.request.player.CreatePlayerRequest;
import dto.request.player.GameRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.WantPlayRequest;
import dto.request.server.CreateGameRequest;
import exception.GameErrorCode;
import exception.GameException;

import static services.BaseService.GSON;

public enum CommandRequest {
    CREATE_PLAYER("create_player", CreatePlayerRequest.class),
    WANT_PLAY("want_play", WantPlayRequest.class),
    PRIVATE_CREATE_GAME("create_private_game", CreateGameRequest.class),
    PLAYING_MOVE("playing_move", MovePlayerRequest.class);

    private final String commandName;
    private final Class request;

    CommandRequest(final String commandName, final Class request) {
        this.commandName = commandName;
        this.request = request;
    }

    public Class getRequest() {
        return request;
    }

    boolean equalCommand(final String message) {
        return commandName.equals(message);
    }

    static boolean isCommandMessage(final String message) {
        for (final CommandRequest commandRequest : values()) {
            if (commandRequest.equalCommand(message)) {
                return true;
            }
        }
        return false;
    }


    public static CommandRequest getCommandByRequest(GameRequest request) throws GameException {
        for (final CommandRequest commandRequest : values()) {
            if (commandRequest.getRequest().equals(request.getClass())) {
                return commandRequest;
            }
        }
        //TODO
        throw new GameException(GameErrorCode.INVALID_REQUEST);
    }

    public static String toJsonParser(GameRequest request) throws GameException {
        for (final CommandRequest commandRequest : values()) {
            if (commandRequest.getRequest().equals(request.getClass())) {
                StringBuilder builder = new StringBuilder();
                builder.append(commandRequest.commandName);
                builder.append(" ");
                builder.append(GSON.toJson(request));
                return builder.toString();
            }
        }
        //TODO
        throw new GameException(GameErrorCode.INVALID_REQUEST);
    }

    public static GameRequest getRequestFromJson(String msg) throws GameException {
        String[] splits = msg.split(" ", 2);
        for (final CommandRequest commandRequest : values()) {
            if (commandRequest.equalCommand(splits[0])) {
                String json = msg.substring(splits[0].length() + 1);
                return (GameRequest) GSON.fromJson(json, commandRequest.getRequest());
            }
        }
        throw new GameException(GameErrorCode.INVALID_REQUEST);
    }
}
