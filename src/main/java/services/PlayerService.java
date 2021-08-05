package services;

import controllers.handlers.TasksHandler;
import dto.request.player.AuthPlayerRequest;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.LogoutPlayerRequest;
import dto.response.game.GameBoardResponse;
import exception.GameErrorCode;
import exception.GameException;
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

    public static synchronized User createPlayer(final CreatePlayerRequest createPlayerRequest, final ClientConnection connection) throws GameException {
        requestIsNotNull(createPlayerRequest);
        nicknameIsNotNull(createPlayerRequest);
        validateNickname(createPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        connectionIsAuthed(connection);
        String nickname = createPlayerRequest.getNickname();
        nicknameIsUsedAlready(nickname);
        User user = putPlayer(nickname);
        putConnection(user.getId(), nickname, connection);
        connection.setUser(user);
        return user;
    }

    public static synchronized User authPlayer(final AuthPlayerRequest createPlayerRequest, final ClientConnection connection) throws GameException {
        requestIsNotNull(createPlayerRequest);
        nicknameIsNotNull(createPlayerRequest);
        validateNickname(createPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        connectionIsAuthed(connection);
        String nickname = createPlayerRequest.getNickname();
        User user = getPlayerByNickname(nickname);
        playerIsNotNull(user);
        putConnection(user.getId(), nickname, connection);
        connection.setUser(user);
        return user;
    }

    public static synchronized User logoutPlayer(final LogoutPlayerRequest logoutPlayerRequest, final ClientConnection connection) throws GameException {
        requestIsNotNull(logoutPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        User user = connection.getUser();
        playerIsNotNull(user);
        removeConnection(user.getId(), user.getNickname());
        connection.setUser(null);
        return user;
    }

    public static synchronized void autoLogoutPlayer(final ClientConnection connection) throws GameException {
        User user = connection.getUser();
        if (user == null) {
            return;
        }
        removeConnection(user.getId(), user.getNickname());
        connection.setUser(null);
        Game nowPlayGame = user.getNowPlaying();
        if (nowPlayGame == null) {
            return;
        }
        try {
            nowPlayGame.lock();
            if (nowPlayGame.getState() == GameState.END) {
                return;
            }
            nowPlayGame.setState(GameState.END);
            GameResult result = GameResult.techWinner(nowPlayGame, user);
            GameService.finishGame(result, nowPlayGame);

            ClientConnection whiteConnection = getConnectionById(nowPlayGame.getWhiteUser().getId());
            if (whiteConnection != null) {
                TasksHandler.sendResponse(whiteConnection, GameBoardResponse.toDto(nowPlayGame, nowPlayGame.getWhiteUser()));
            }

            ClientConnection blackConnection = getConnectionById(nowPlayGame.getBlackUser().getId());
            if (blackConnection != null) {
                TasksHandler.sendResponse(blackConnection, GameBoardResponse.toDto(nowPlayGame, nowPlayGame.getBlackUser()));
            }
        } catch (IOException ignore) {
            log.debug("Cant send info about tech win {}", nowPlayGame);
        } finally {
            nowPlayGame.unlock();
        }
    }

    public static ClientConnection getConnectionByPlayer(final User user) throws GameException {
        playerIsNotNull(user);
        ClientConnection connection = getConnectionById(user.getId());
        connectionIsNotNullAndConnected(connection);
        return connection;
    }

    public static boolean canSearchGame(final ClientConnection connection) {
        if (connection == null) {
            return false;
        }
        User user = connection.getUser();
        if (user == null || user.getState() != PlayerState.SEARCH_GAME) {
            return false;
        }
        return connection.isConnected();
    }

    public static void setPlayerStateNone(final ClientConnection connection) throws GameException {
        connectionIsNotNullAndConnected(connection);
        User user = connection.getUser();
        setPlayerStateNone(user);
    }

    public static void setPlayerStateNone(final User user) throws GameException {
        playerIsNotNull(user);
        user.lock();
        user.setState(PlayerState.NONE);
        user.setColor(PlayerColor.NONE);
        user.setNowPlaying(null);
        user.unlock();
    }


    public static User canPlayerSearchGame(final ClientConnection clientConnection) throws GameException {
        connectionIsNotNullAndConnected(clientConnection);
        User user = clientConnection.getUser();
        return canPlayerSearchGame(user);
    }

    public static User canPlayerSearchGame(final User user) throws GameException {
        playerIsNotNull(user);
        try {
            user.lock();
            playerIsNotStateNone(user);
            user.setState(PlayerState.SEARCH_GAME);
        } finally {
            user.unlock();
        }
        return user;
    }

    private static void nicknameIsNotNull(final CreatePlayerRequest createPlayerRequest) throws GameException {
        if (createPlayerRequest.getNickname() == null) {
            throw new GameException(GameErrorCode.INVALID_REQUEST);
        }
    }

    private static void validateNickname(final CreatePlayerRequest createPlayerRequest) throws GameException {
        String nickname = createPlayerRequest.getNickname().trim().toLowerCase();
        if (nickname.isEmpty()
                || nickname.length() < GameProperties.MIN_SIZE_NICKNAME
                || nickname.length() > GameProperties.MAX_SIZE_NICKNAME) {
            throw new GameException(GameErrorCode.INVALID_NICKNAME);
        }
        createPlayerRequest.setNickname(nickname);
    }

}
