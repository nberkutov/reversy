package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controllers.commands.CommandRequest;
import controllers.commands.CommandResponse;
import dto.request.GameRequest;
import dto.response.GameResponse;
import exception.GameErrorCode;
import exception.GameException;
import models.base.interfaces.GameBoard;
import models.base.modifiedSerializer.GameBoardDeserializer;
import models.base.modifiedSerializer.GameBoardSerializer;

public class JsonService {
    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(GameBoard.class, new GameBoardSerializer())
            .registerTypeAdapter(GameBoard.class, new GameBoardDeserializer())
            .create();

    public static <T> String toJson(final T t) {
        return GSON.toJson(t);
    }

    public static <T> T fromJson(final String msg, Class<T> nameClass) {
        return GSON.fromJson(msg.trim(), nameClass);
    }

    public static CommandRequest getCommandByRequest(final GameRequest request) throws GameException {
        requestIsNotNull(request);
        for (final CommandRequest commandRequest : CommandRequest.values()) {
            if (commandRequest.getRequest().equals(request.getClass())) {
                return commandRequest;
            }
        }
        throw new GameException(GameErrorCode.UNKNOWN_REQUEST);
    }

    public static String toMsgParser(final GameRequest request) throws GameException {
        requestIsNotNull(request);
        for (final CommandRequest commandRequest : CommandRequest.values()) {
            if (commandRequest.getRequest().equals(request.getClass())) {
                return commandRequest.getCommandName() +
                        " " +
                        toJson(request);
            }
        }
        throw new GameException(GameErrorCode.UNKNOWN_REQUEST);
    }

    public static GameRequest getRequestFromMsg(final String msg) throws GameException {
        msgIsNotNullAndNotEmpty(msg);
        String[] command = msg.split(" ");
        for (final CommandRequest commandRequest : CommandRequest.values()) {
            if (commandRequest.equalCommand(command[0])) {
                String json = msg.substring(command[0].length() + 1);
                return (GameRequest) fromJson(json, (Class<?>) commandRequest.getRequest());
            }
        }
        throw new GameException(GameErrorCode.UNKNOWN_REQUEST);
    }

    public static CommandResponse getCommandByResponse(final GameResponse response) throws GameException {
        responseIsNotNull(response);
        for (final CommandResponse commandResponse : CommandResponse.values()) {
            if (commandResponse.getResponse().equals(response.getClass())) {
                return commandResponse;
            }
        }
        throw new GameException(GameErrorCode.UNKNOWN_RESPONSE);
    }

    public static String toMsgParser(final GameResponse response) throws GameException {
        responseIsNotNull(response);
        for (final CommandResponse commandResponse : CommandResponse.values()) {
            if (commandResponse.getResponse().equals(response.getClass())) {
                return commandResponse.getCommandName() +
                        " " +
                        JsonService.toJson(response);
            }
        }
        throw new GameException(GameErrorCode.UNKNOWN_RESPONSE);
    }

    public static GameResponse getResponseFromMsg(final String msg) throws GameException {
        msgIsNotNullAndNotEmpty(msg);
        String[] command = msg.split(" ");
        for (final CommandResponse commandResponse : CommandResponse.values()) {
            if (commandResponse.equalCommand(command[0])) {
                String json = msg.substring(command[0].length() + 1);
                return (GameResponse) JsonService.fromJson(json, (Class<?>) commandResponse.getResponse());
            }
        }
        throw new GameException(GameErrorCode.UNKNOWN_RESPONSE);
    }


    private static void requestIsNotNull(final GameRequest gameRequest) throws GameException {
        if (gameRequest == null) {
            throw new GameException(GameErrorCode.INVALID_REQUEST);
        }
    }

    private static void responseIsNotNull(final GameResponse gameResponse) throws GameException {
        if (gameResponse == null) {
            throw new GameException(GameErrorCode.INVALID_RESPONSE);
        }
    }

    private static void msgIsNotNullAndNotEmpty(final String msg) throws GameException {
        if (msg == null || msg.trim().isEmpty()) {
            throw new GameException(GameErrorCode.INVALID_MESSAGE_DTO);
        }
    }
}
