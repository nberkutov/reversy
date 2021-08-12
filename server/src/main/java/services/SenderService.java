package services;

import controllers.TaskResponse;
import controllers.mapper.Mapper;
import dto.response.GameResponse;
import exception.GameErrorCode;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.player.User;

import java.util.List;

@Slf4j
public class SenderService extends DataBaseService {

    private SenderService() {
    }

    public static void broadcastMessageForAll(final String message) throws ServerException {
        final List<ClientConnection> list = getAllConnections();
        broadcastResponse(list, Mapper.toDtoMessage(message));
    }

    public static void broadcastResponseForAuthConnections(final GameResponse response) throws ServerException {
        final List<ClientConnection> list = getAuthConnections();
        broadcastResponse(list, response);
    }

    private static void broadcastResponse(final List<ClientConnection> connections, final GameResponse response) throws ServerException {
        responseNotNull(response);
        for (final ClientConnection connection : connections) {
            sendResponse(connection, response);
        }
    }

    public static void sendResponse(final ClientConnection connection, final GameResponse response) throws ServerException {
        responseNotNull(response);
        if (connection == null) {
            return;
        }
        TaskResponse.createAndSend(connection, response);
    }

    public static void sendResponse(final User user, final GameResponse response) throws ServerException {
        sendResponse(PlayerService.getConnectionByPlayer(user), response);
    }

    private static void responseNotNull(final GameResponse response) throws ServerException {
        if (response == null) {
            throw new ServerException(GameErrorCode.INVALID_RESPONSE);
        }
    }

}
