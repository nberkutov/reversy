package services;

import controllers.handlers.TasksHandler;
import controllers.mapper.Mapper;
import dto.request.player.AuthPlayerRequest;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.LogoutPlayerRequest;
import exception.GameErrorCode;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.GameProperties;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.game.Game;
import models.game.GameResult;
import models.player.User;

import java.io.IOException;

@Slf4j
public class PlayerService extends DataBaseService {

    public static synchronized User createPlayer(final CreatePlayerRequest createPlayerRequest, final ClientConnection connection) throws ServerException {
        requestIsNotNull(createPlayerRequest);
        nicknameIsNotNull(createPlayerRequest);
        final String nickname = validateNickname(createPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        connectionIsAuthed(connection);
        nicknameIsUsedAlready(nickname);
        final User user = putPlayer(nickname);
        putConnection(user.getId(), nickname, connection);
        connection.setUser(user);
        return user;
    }

    public static synchronized User authPlayer(final AuthPlayerRequest createPlayerRequest, final ClientConnection connection) throws ServerException {
        requestIsNotNull(createPlayerRequest);
        nicknameIsNotNull(createPlayerRequest);
        final String nickname = validateNickname(createPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        connectionIsAuthed(connection);
        final User user = getPlayerByNickname(nickname);
        userIsNotNull(user);
        putConnection(user.getId(), nickname, connection);
        connection.setUser(user);
        return user;
    }

    public static synchronized User logoutPlayer(final LogoutPlayerRequest logoutPlayerRequest, final ClientConnection connection) throws ServerException {
        requestIsNotNull(logoutPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        final User user = connection.getUser();
        userIsNotNull(user);
        removeConnection(user.getId(), user.getNickname());
        connection.setUser(null);
        return user;
    }

    public static synchronized void autoLogoutPlayer(final ClientConnection connection) throws ServerException {
        final User user = connection.getUser();
        if (user == null) {
            return;
        }
        removeConnection(user.getId(), user.getNickname());
        connection.setUser(null);
        final Game nowPlayGame = user.getNowPlaying();
        if (nowPlayGame == null) {
            return;
        }
        try {
            nowPlayGame.lock();
            if (nowPlayGame.getState() == GameState.END) {
                return;
            }
            nowPlayGame.setState(GameState.END);
            final GameResult result = GameResult.techWinner(nowPlayGame, user);
            GameService.finishGame(result, nowPlayGame);

            final ClientConnection whiteConnection = getConnectionById(nowPlayGame.getWhiteUser().getId());
            sendResponse(whiteConnection, nowPlayGame, nowPlayGame.getWhiteUser());

            final ClientConnection blackConnection = getConnectionById(nowPlayGame.getBlackUser().getId());
            sendResponse(blackConnection, nowPlayGame, nowPlayGame.getBlackUser());

        } catch (IOException ignore) {
            log.debug("Cant send info about tech win {}", nowPlayGame);
        } finally {
            nowPlayGame.unlock();
        }
    }

    private static void sendResponse(final ClientConnection connection, final Game game, final User user) throws ServerException, IOException {
        if (connection != null) {
            TasksHandler.sendResponse(connection, Mapper.toDtoGame(game, user));
        }
    }

    public static ClientConnection getConnectionByPlayer(final User user) throws ServerException {
        userIsNotNull(user);
        final ClientConnection connection = getConnectionById(user.getId());
        connectionIsNotNullAndConnected(connection);
        return connection;
    }

    public static boolean canSearchGame(final ClientConnection connection) {
        if (connection == null) {
            return false;
        }
        final User user = connection.getUser();
        if (user == null || user.getState() != PlayerState.SEARCH_GAME) {
            return false;
        }
        return connection.isConnected();
    }

    public static void setPlayerStateNone(final ClientConnection connection) throws ServerException {
        connectionIsNotNullAndConnected(connection);
        final User user = connection.getUser();
        setPlayerStateNone(user);
    }

    public static void setPlayerStateNone(final User user) throws ServerException {
        userIsNotNull(user);
        user.lock();
        user.setState(PlayerState.NONE);
        user.setColor(PlayerColor.NONE);
        user.setNowPlaying(null);
        user.unlock();
    }


    public static User canPlayerSearchGame(final ClientConnection clientConnection) throws ServerException {
        connectionIsNotNullAndConnected(clientConnection);
        final User user = clientConnection.getUser();
        return canPlayerSearchGame(user);
    }

    public static User canPlayerSearchGame(final User user) throws ServerException {
        userIsNotNull(user);
        try {
            user.lock();
            userIsNotStateNone(user);
            user.setState(PlayerState.SEARCH_GAME);
        } finally {
            user.unlock();
        }
        return user;
    }

    private static void nicknameIsNotNull(final CreatePlayerRequest createPlayerRequest) throws ServerException {
        if (createPlayerRequest.getNickname() == null) {
            throw new ServerException(GameErrorCode.INVALID_REQUEST);
        }
    }

    private static String validateNickname(final CreatePlayerRequest createPlayerRequest) throws ServerException {
        final String nickname =
                createPlayerRequest.getNickname()
                        .trim()
                        .toLowerCase();
        if (nickname.length() < GameProperties.MIN_SIZE_NICKNAME
                || nickname.length() > GameProperties.MAX_SIZE_NICKNAME) {
            throw new ServerException(GameErrorCode.INVALID_NICKNAME);
        }
        return nickname;
    }

}
