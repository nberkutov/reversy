package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.GameRequest;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.CacheDataBaseDao;
import org.example.models.DataBaseDao;
import org.example.models.base.PlayerState;
import org.example.models.game.Game;
import org.example.models.game.GameResult;
import org.example.models.game.Room;
import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataBaseService {
    @Autowired
    protected PlayerService ps;
    @Autowired
    protected SenderService ss;
    @Autowired
    protected GameService gs;
    @Autowired
    protected RoomService rs;
    @Autowired
    protected DataBaseDao dbd;
    @Autowired
    protected CacheDataBaseDao cdbd;

    /**
     * Функция провероки
     * Если game равен null, то выбрасывает GameException.
     *
     * @param game - класс игры
     */
    protected void gameIsNotNull(final Game game) throws ServerException {
        if (game == null) {
            throw new ServerException(GameErrorCode.GAME_NOT_FOUND);
        }
    }

    protected void gameResultIsNotNull(final GameResult game) throws ServerException {
        if (game == null) {
            throw new ServerException(GameErrorCode.GAME_RESULT_NOT_FOUND);
        }
    }

    protected void connectionIsAuthed(final UserConnection connection) throws ServerException {
        if (connection.getUserId() != -1) {
            throw new ServerException(GameErrorCode.PLAYER_IS_AUTH);
        }
    }

    /**
     * Функция провероки
     * Если player равен null, то выбрасывает GameException.
     *
     * @param user - класс игрока
     */
    protected void userIsNotNull(final User user) throws ServerException {
        if (user == null) {
            throw new ServerException(GameErrorCode.PLAYER_NOT_FOUND);
        }
    }

    protected void roomIsNotNull(final Room room) throws ServerException {
        if (room == null) {
            throw new ServerException(GameErrorCode.ROOM_NOT_FOUND);
        }
    }

    protected void userIsNotStateNone(final User user) throws ServerException {
        if (user.getState() != PlayerState.NONE) {
            throw new ServerException(GameErrorCode.PLAYER_CANT_PERFORM);
        }
    }

    protected void connectionIsNotNullAndConnected(final UserConnection connection) throws ServerException {
        if (connection == null || !connection.isConnected()) {
            throw new ServerException(GameErrorCode.CONNECTION_LOST);
        }
    }

    protected void requestIsNotNull(final GameRequest request) throws ServerException {
        if (request == null) {
            throw new ServerException(GameErrorCode.INVALID_REQUEST);
        }
    }

    protected void checkRequestAndConnection(final GameRequest request, final UserConnection connection) throws ServerException {
        requestIsNotNull(request);
        connectionIsNotNullAndConnected(connection);
    }
}
