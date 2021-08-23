package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.controllers.mapper.Mapper;
import org.example.dto.request.player.*;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.GameProperties;
import org.example.models.base.GameState;
import org.example.models.base.PlayerColor;
import org.example.models.base.PlayerState;
import org.example.models.game.Game;
import org.example.models.game.GameResult;
import org.example.models.game.Room;
import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
public class PlayerService extends DataBaseService {

    public User createUser(final CreateUserRequest createUserRequest, final UserConnection connection) throws ServerException {
        requestIsNotNull(createUserRequest);
        nicknameIsNotNull(createUserRequest);
        final String nickname = validateNickname(createUserRequest);
        connectionIsNotNullAndConnected(connection);
        connectionIsAuthed(connection);
        final User user = new User(nickname);
        dbd.saveUser(user);
        cdbd.putConnection(user.getId(), nickname, connection);
        connection.setUserId(user.getId());
        return user;
    }

    public User authPlayer(final AuthUserRequest createPlayerRequest, final UserConnection connection) throws ServerException {
        requestIsNotNull(createPlayerRequest);
        nicknameIsNotNull(createPlayerRequest);
        final String nickname = validateNickname(createPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        connectionIsAuthed(connection);
        final User user = dbd.getUserByNickname(nickname);
        userIsNotNull(user);
        cdbd.putConnection(user.getId(), nickname, connection);
        connection.setUserId(user.getId());
        return user;
    }

    public User logoutPlayer(final LogoutPlayerRequest logoutPlayerRequest, final UserConnection connection) throws ServerException {
        requestIsNotNull(logoutPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        final User user = dbd.getUserById(connection.getUserId());
        userIsNotNull(user);
        cdbd.removeConnection(user.getId(), user.getNickname());
        connection.setUserId(-1);
        dbd.saveUser(user);
        return user;
    }

    public void autoLogoutPlayer(final UserConnection connection) throws ServerException {
        final User user = dbd.getUserById(connection.getUserId());
        if (user == null) {
            return;
        }
        cdbd.removeConnection(user.getId(), user.getNickname());
        connection.setUserId(-1);

        final Game nowPlayGame = user.getNowPlaying();
        final Room nowRoom = user.getNowRoom();
        if (nowPlayGame != null && nowPlayGame.getState() != GameState.END) {
            nowPlayGame.setState(GameState.END);
            final GameResult result = GameResult.techWinner(nowPlayGame, user);
            gs.finishGame(result, nowPlayGame);
            dbd.saveGame(nowPlayGame);

            final UserConnection whiteConnection = cdbd.getConnectionById(nowPlayGame.getWhiteUser().getId());
            final UserConnection blackConnection = cdbd.getConnectionById(nowPlayGame.getBlackUser().getId());
            ss.sendResponse(whiteConnection, Mapper.toDtoGame(nowPlayGame));
            ss.sendResponse(whiteConnection, Mapper.toDtoMessage("Ваш оппонент вышел из игры. Вы выиграли!"));
            ss.sendResponse(blackConnection, Mapper.toDtoGame(nowPlayGame));
            ss.sendResponse(blackConnection, Mapper.toDtoMessage("Ваш оппонент вышел из игры. Вы выиграли!"));
        }

        if (nowRoom != null) {
            nowRoom.setWhiteUser(null);
            nowRoom.setBlackUser(null);
            dbd.removeRoom(nowRoom);
            user.setNowRoom(null);
        }
        user.setState(PlayerState.NONE);
        dbd.saveUser(user);
    }

    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public UserConnection getConnectionByPlayer(final User user) throws ServerException {
        userIsNotNull(user);
        final UserConnection connection = cdbd.getConnectionById(user.getId());
        return connection;
    }

    public boolean canSearchGame(final UserConnection connection) {
        if (connection == null) {
            return false;
        }
        final User user = dbd.getUserById(connection.getUserId());
        if (user == null || user.getState() != PlayerState.SEARCH_GAME) {
            return false;
        }
        return connection.isConnected();
    }

    public void setPlayerStateNone(final UserConnection connection) throws ServerException {
        connectionIsNotNullAndConnected(connection);
        final User user = dbd.getUserById(connection.getUserId());
        setPlayerStateNone(user);
    }

    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void setPlayerStateNone(final User user) throws ServerException {
        userIsNotNull(user);
        user.setState(PlayerState.NONE);
        user.setColor(PlayerColor.NONE);
        user.setNowPlaying(null);
        user.setNowRoom(null);
        dbd.saveUser(user);
    }

    //    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public User canPlayerSearchGame(final UserConnection userConnection) throws ServerException {
        connectionIsNotNullAndConnected(userConnection);
        final User user = dbd.getUserById(userConnection.getUserId());
        return canPlayerSearchGame(user);
    }

    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public User canPlayerSearchGame(final User user) throws ServerException {
        userIsNotNull(user);
        userIsNotStateNone(user);
        user.setState(PlayerState.SEARCH_GAME);
        dbd.saveUser(user);
        return user;
    }

    private static void nicknameIsNotNull(final WithNicknameRequest withNicknameRequest) throws ServerException {
        if (withNicknameRequest.getNickname() == null) {
            throw new ServerException(GameErrorCode.INVALID_REQUEST);
        }
    }

    private static String validateNickname(final WithNicknameRequest withNicknameRequest) throws ServerException {
        final String nickname =
                withNicknameRequest.getNickname()
                        .trim()
                        .toLowerCase();
        if (nickname.length() < GameProperties.MIN_SIZE_NICKNAME
                || nickname.length() > GameProperties.MAX_SIZE_NICKNAME) {
            throw new ServerException(GameErrorCode.INVALID_NICKNAME);
        }
        return nickname;
    }

    public User getInfoAboutUser(final GetInfoAboutUserRequest getInfo, final UserConnection connection) throws ServerException {
        requestIsNotNull(getInfo);
        nicknameIsNotNull(getInfo);
        connectionIsNotNullAndConnected(connection);
        final String nickname = validateNickname(getInfo);
        final User user = dbd.getUserByNickname(nickname);
        userIsNotNull(user);
        return user;
    }
}
