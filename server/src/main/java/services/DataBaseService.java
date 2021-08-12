package services;

import dto.request.GameRequest;
import exception.GameErrorCode;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.base.PlayerState;
import models.game.Game;
import models.game.GameResult;
import models.game.Room;
import models.player.User;

import java.io.Serializable;
import java.util.List;

@Slf4j
public class DataBaseService implements Serializable {

    protected static synchronized int getPlayerId() {
        return Server.database.getPlayerId();
    }

    protected static synchronized int getGameId() {
        return Server.database.getGameId();
    }

    protected static synchronized int getRoomId() {
        return Server.database.getRoomId();
    }

    public static void clearAll() {
        Server.database.clearAll();
    }

    public static Game getGameById(final int gameId) {
        return Server.database.getGameById(gameId);
    }

    public static Room getRoomById(final int roomId) {
        return Server.database.getRoomById(roomId);
    }

    public static Game putGame(final User first, final User second) {
        return Server.database.putGame(first, second);
    }

    public static User putPlayer(final String nickname) {
        return Server.database.putPlayer(nickname);
    }

    public static synchronized Room putRoom() {
        return Server.database.putRoom();
    }

    protected static synchronized void nicknameIsUsedAlready(final String nickname) throws ServerException {
        if (getPlayerByNickname(nickname) != null || getConnectionByNickname(nickname) != null) {
            throw new ServerException(GameErrorCode.NICKNAME_ALREADY_USED);
        }
    }


    public static void putConnection(final int id, final String nickname, final ClientConnection connection) {
        Server.database.putConnection(id, nickname, connection);
    }

    public static void removeConnection(final int id, final String nickname) {
        Server.database.removeConnection(id, nickname);
    }

    public static void removeConnection(final String nickname) {
        Server.database.removeConnection(nickname);
    }

    public static void putConnection(final String nickname, final ClientConnection connection) {
        Server.database.putConnection(nickname, connection);
    }

    public static User getPlayerByNickname(final String nickname) {
        return Server.database.getPlayerByNickname(nickname);
    }

    public static ClientConnection getConnectionByNickname(final String nickname) {
        return Server.database.getConnectionByNickname(nickname);
    }

    public static List<User> getAllPlayers() {
        return Server.database.getAllPlayers();
    }

    public static List<Game> getAllGames() {
        return Server.database.getAllGames();
    }

    public static List<Room> getRooms(final boolean needClose, final int limit) {
        return Server.database.getRooms(needClose, limit);
    }

    public static List<Room> getAllRooms() {
        return Server.database.getAllRooms();
    }

    public static ClientConnection getConnectionById(final int id) {
        return Server.database.getConnectionById(id);
    }

    /**
     * Функция провероки
     * Если game равен null, то выбрасывает GameException.
     *
     * @param game - класс игры
     */
    protected static void gameIsNotNull(final Game game) throws ServerException {
        if (game == null) {
            throw new ServerException(GameErrorCode.GAME_NOT_FOUND);
        }
    }

    protected static void gameResultIsNotNull(final GameResult game) throws ServerException {
        if (game == null) {
            throw new ServerException(GameErrorCode.GAME_RESULT_NOT_FOUND);
        }
    }

    protected static void connectionIsAuthed(ClientConnection connection) throws ServerException {
        if (connection.getUser() != null) {
            throw new ServerException(GameErrorCode.PLAYER_IS_AUTH);
        }
    }

    /**
     * Функция провероки
     * Если player равен null, то выбрасывает GameException.
     *
     * @param user - класс игрока
     */
    protected static void userIsNotNull(final User user) throws ServerException {
        if (user == null) {
            throw new ServerException(GameErrorCode.PLAYER_NOT_FOUND);
        }
    }

    protected static void roomIsNotNull(final Room room) throws ServerException {
        if (room == null) {
            throw new ServerException(GameErrorCode.ROOM_NOT_FOUND);
        }
    }

    protected static void playerIsNotPlaying(final User user) throws ServerException {
        if (user.getState() == PlayerState.PLAYING) {
            throw new ServerException(GameErrorCode.PLAYER_ALREADY_PLAYING);
        }
    }

    protected static void playerIsStateNone(final User user) throws ServerException {
        if (user.getState() == PlayerState.NONE) {
            throw new ServerException(GameErrorCode.PLAYER_CANT_PERFORM);
        }
    }

    protected static void userIsNotStateNone(final User user) throws ServerException {
        if (user.getState() != PlayerState.NONE) {
            throw new ServerException(GameErrorCode.PLAYER_CANT_PERFORM);
        }
    }

    protected static void playerIsNotInRoom(final User user) throws ServerException {
        if (user.getState() == PlayerState.WAITING_ROOM) {
            throw new ServerException(GameErrorCode.PLAYER_ALREADY_PLAYING);
        }
    }

    protected static void checkPlayerConnection(final User user) throws ServerException {
        ClientConnection connection = getConnectionById(user.getId());
        connectionIsNotNullAndConnected(connection);
    }

    protected static void connectionIsNotNullAndConnected(final ClientConnection connection) throws ServerException {
        if (connection == null || !connection.isConnected()) {
            throw new ServerException(GameErrorCode.CONNECTION_LOST);
        }
    }

    protected static void requestIsNotNull(final GameRequest request) throws ServerException {
        if (request == null) {
            throw new ServerException(GameErrorCode.INVALID_REQUEST);
        }
    }

    protected static void checkRequestAndConnection(GameRequest request, ClientConnection connection) throws ServerException {
        requestIsNotNull(request);
        connectionIsNotNullAndConnected(connection);
    }
}
