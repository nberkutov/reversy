package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.controllers.TaskResponse;
import org.example.controllers.mapper.Mapper;
import org.example.dto.response.GameResponse;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.CacheDataBaseDao;
import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SenderService {
    @Autowired
    private CacheDataBaseDao cdbd;
    @Autowired
    private PlayerService ps;
    public void broadcastMessageForAll(final String message) throws ServerException {
        final List<UserConnection> list = cdbd.getAllConnections();
        broadcastResponse(list, Mapper.toDtoMessage(message));
    }

    public void broadcastResponseForAuthConnections(final GameResponse response) throws ServerException {
        final List<UserConnection> list = cdbd.getAuthConnections();
        broadcastResponse(list, response);
    }

    private void broadcastResponse(final List<UserConnection> connections, final GameResponse response) throws ServerException {
        responseNotNull(response);
        for (final UserConnection connection : connections) {
            sendResponse(connection, response);
        }
    }

    public void sendResponse(final UserConnection connection, final GameResponse response) throws ServerException {
        responseNotNull(response);
        if (connection == null) {
            return;
        }
        try {
            TaskResponse.createAndSend(connection, response);
        } catch (final ServerException e) {
            log.warn("Can't send message to {}, {}", connection, e.getMessage());
        }
    }

    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void sendResponse(final User user, final GameResponse response) throws ServerException {
        sendResponse(ps.getConnectionByPlayer(user), response);
    }

    private static void responseNotNull(final GameResponse response) throws ServerException {
        if (response == null) {
            throw new ServerException(GameErrorCode.INVALID_RESPONSE);
        }
    }

}
