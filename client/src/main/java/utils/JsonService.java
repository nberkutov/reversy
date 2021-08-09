package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.commands.CommandRequest;
import dto.commands.CommandResponse;
import dto.request.GameRequest;
import dto.response.GameResponse;
import exception.GameErrorCode;
import exception.ServerException;
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

    public static String toMsgParser(final GameRequest request) throws ServerException {
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
