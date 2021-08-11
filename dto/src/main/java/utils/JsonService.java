package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import commands.CommandRequest;
import commands.CommandResponse;
import dto.request.GameRequest;
import dto.response.GameResponse;
import exception.GameErrorCode;
import exception.ServerException;
import models.base.interfaces.GameBoard;
import models.base.modifiedSerializer.GameBoardDeserializer;
import models.base.modifiedSerializer.GameBoardSerializer;
import models.base.modifiedSerializer.LockDeserializer;
import models.base.modifiedSerializer.LockSerializer;

import java.util.concurrent.locks.Lock;

public class JsonService {
    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(GameBoard.class, new GameBoardSerializer())
            .registerTypeAdapter(GameBoard.class, new GameBoardDeserializer())
            .registerTypeAdapter(Lock.class, new LockSerializer())
            .registerTypeAdapter(Lock.class, new LockDeserializer())
            .create();

    public static <T> String toJson(final T t) {
        return GSON.toJson(t);
    }

    public static <T> T fromJson(final String msg, Class<T> nameClass) {
        return GSON.fromJson(msg.trim(), nameClass);
    }

    public static CommandRequest getCommandByRequest(final GameRequest request) throws ServerException {
        requestIsNotNull(request);
        for (final CommandRequest commandRequest : CommandRequest.values()) {
            if (commandRequest.getRequest().equals(request.getClass())) {
                return commandRequest;
            }
        }
        throw new ServerException(GameErrorCode.UNKNOWN_REQUEST);
    }

    public static String toMsgParser(final GameRequest request) throws ServerException {
        requestIsNotNull(request);
        for (final CommandRequest commandRequest : CommandRequest.values()) {
            if (commandRequest.getRequest().equals(request.getClass())) {
                return commandRequest.getCommandName() +
                        " " +
                        toJson(request);
            }
        }
        throw new ServerException(GameErrorCode.UNKNOWN_REQUEST);
    }

    public static GameRequest getRequestFromMsg(final String msg) throws ServerException {
        msgIsNotNullAndNotEmpty(msg);
        String[] command = msg.split(" ");
        for (final CommandRequest commandRequest : CommandRequest.values()) {
            if (commandRequest.equalCommand(command[0])) {
                String json = msg.substring(command[0].length() + 1);
                return (GameRequest) fromJson(json, (Class<?>) commandRequest.getRequest());
            }
        }
        throw new ServerException(GameErrorCode.UNKNOWN_REQUEST);
    }

    public static CommandResponse getCommandByResponse(final GameResponse response) throws ServerException {
        responseIsNotNull(response);
        for (final CommandResponse commandResponse : CommandResponse.values()) {
            if (commandResponse.getResponse().equals(response.getClass())) {
                return commandResponse;
            }
        }
        throw new ServerException(GameErrorCode.UNKNOWN_RESPONSE);
    }

    public static String toMsgParser(final GameResponse response) throws ServerException {
        responseIsNotNull(response);
        for (final CommandResponse commandResponse : CommandResponse.values()) {
            if (commandResponse.getResponse().equals(response.getClass())) {
                return commandResponse.getCommandName() +
                        " " +
                        JsonService.toJson(response);
            }
        }
        throw new ServerException(GameErrorCode.UNKNOWN_RESPONSE);
    }

    public static GameResponse getResponseFromMsg(final String msg) throws ServerException {
        msgIsNotNullAndNotEmpty(msg);
        String[] command = msg.split(" ");
        for (final CommandResponse commandResponse : CommandResponse.values()) {
            if (commandResponse.equalCommand(command[0])) {
                String json = msg.substring(command[0].length() + 1);
                return (GameResponse) JsonService.fromJson(json, (Class<?>) commandResponse.getResponse());
            }
        }
        throw new ServerException(GameErrorCode.UNKNOWN_RESPONSE);
    }


    private static void requestIsNotNull(final GameRequest gameRequest) throws ServerException {
        if (gameRequest == null) {
            throw new ServerException(GameErrorCode.INVALID_REQUEST);
        }
    }

    private static void responseIsNotNull(final GameResponse gameResponse) throws ServerException {
        if (gameResponse == null) {
            throw new ServerException(GameErrorCode.INVALID_RESPONSE);
        }
    }

    private static void msgIsNotNullAndNotEmpty(final String msg) throws ServerException {
        if (msg == null || msg.trim().isEmpty()) {
            throw new ServerException(GameErrorCode.INVALID_MESSAGE_DTO);
        }
    }
}
