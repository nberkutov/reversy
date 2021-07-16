package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controllers.commands.CommandRequest;
import controllers.commands.CommandResponse;
import dto.request.player.GameRequest;
import dto.response.GameResponse;
import exception.GameErrorCode;
import exception.GameException;

public class JsonService {
    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .create();

    public static <T> String toJson(T t) {
        synchronized (GSON) {
            return GSON.toJson(t);
        }
    }

    public static <T> T fromJson(String msg, Class<T> nameClass) {
        synchronized (GSON) {
            return GSON.fromJson(msg, nameClass);
        }
    }

    public static CommandRequest getCommandByRequest(GameRequest request) throws GameException {
        requestIsNotNull(request);
        for (final CommandRequest commandRequest : CommandRequest.values()) {
            if (commandRequest.getRequest().equals(request.getClass())) {
                return commandRequest;
            }
        }
        throw new GameException(GameErrorCode.UNKNOWN_REQUEST);
    }

    public static String toJsonParser(GameRequest request) throws GameException {
        requestIsNotNull(request);
        for (final CommandRequest commandRequest : CommandRequest.values()) {
            if (commandRequest.getRequest().equals(request.getClass())) {
                StringBuilder builder = new StringBuilder();
                builder.append(commandRequest.getCommandName());
                builder.append(" ");
                builder.append(toJson(request));
                return builder.toString();
            }
        }
        throw new GameException(GameErrorCode.UNKNOWN_REQUEST);
    }

    public static GameRequest getRequestFromJson(String msg) throws GameException {
        String[] command = msg.split(" ");
        for (final CommandRequest commandRequest : CommandRequest.values()) {
            if (commandRequest.equalCommand(command[0])) {
                String json = msg.substring(command[0].length() + 1);
                return (GameRequest) fromJson(json, (Class<?>) commandRequest.getRequest());
            }
        }
        throw new GameException(GameErrorCode.INVALID_REQUEST);
    }

    public static CommandResponse getCommandByResponse(GameResponse response) throws GameException {
        responseIsNotNull(response);
        for (final CommandResponse commandResponse : CommandResponse.values()) {
            if (commandResponse.getResponse().equals(response.getClass())) {
                return commandResponse;
            }
        }
        throw new GameException(GameErrorCode.UNKNOWN_RESPONSE);
    }

    public synchronized static String toJsonParser(GameResponse response) throws GameException {
        responseIsNotNull(response);
        for (final CommandResponse commandResponse : CommandResponse.values()) {
            if (commandResponse.getResponse().equals(response.getClass())) {
                StringBuilder builder = new StringBuilder();
                builder.append(commandResponse.getCommandName());
                builder.append(" ");
                builder.append(JsonService.toJson(response));
                return builder.toString();
            }
        }
        throw new GameException(GameErrorCode.UNKNOWN_RESPONSE);
    }

    public synchronized static GameResponse getResponseFromJson(String msg) throws GameException {
        String[] command = msg.split(" ");
        for (final CommandResponse commandResponse : CommandResponse.values()) {
            if (commandResponse.equalCommand(command[0])) {
                String json = msg.substring(command[0].length() + 1);
                return (GameResponse) JsonService.fromJson(json, (Class<?>) commandResponse.getResponse());
            }
        }
        throw new GameException(GameErrorCode.UNKNOWN_RESPONSE);
    }


    private static void requestIsNotNull(GameRequest gameRequest) throws GameException {
        if (gameRequest == null) {
            throw new GameException(GameErrorCode.INVALID_REQUEST);
        }
    }

    private static void responseIsNotNull(GameResponse gameResponse) throws GameException {
        if (gameResponse == null) {
            throw new GameException(GameErrorCode.INVALID_RESPONSE);
        }
    }
}
