package services;

import dto.request.GameRequest;
import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.base.PlayerState;
import models.game.Game;
import models.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DataBaseService {
    private static final Map<Integer, Game> games = new ConcurrentHashMap<>();
    private static final Map<Integer, Player> players = new ConcurrentHashMap<>();
    private static final Map<String, Player> login_players = new ConcurrentHashMap<>();
    private static final Map<String, ClientConnection> login_connects = new ConcurrentHashMap<>();
    private static final Map<Integer, ClientConnection> connects = new ConcurrentHashMap<>();
    //MiniBD
    private static int gameIncrement = 0;
    private static int playerIncrement = 0;

    protected static synchronized int getPlayerId() {
        return playerIncrement++;
    }

    protected static synchronized int getGameId() {
        return gameIncrement++;
    }

    public static void clearAll() {
        games.clear();
        login_players.clear();
        players.clear();
        login_connects.clear();
        connects.clear();
    }

    public static Game getGameById(final int gameId) {
        return games.get(gameId);
    }

    public static synchronized Game putGame(final int id, final Game game) {
        return games.put(id, game);
    }

    protected static synchronized void nicknameIsUsedAlready(final String nickname) throws GameException {
        if (login_players.get(nickname) != null || login_connects.get(nickname) != null) {
            throw new GameException(GameErrorCode.NICKNAME_ALREADY_USED);
        }
    }

    public static synchronized Player putPlayer(final int id, final String nickname) {
        Player player = new Player(id, nickname);
        login_players.put(player.getNickname(), player);
        players.put(id, player);
        return player;
    }

    public static void putConnection(final int id, final String nickname, final ClientConnection connection) {
        connects.put(id, connection);
        putConnection(nickname, connection);
    }

    public static void putConnection(final String nickname, final ClientConnection connection) {
        login_connects.put(nickname, connection);
    }

    public static Player getPlayerByNickname(final String nickname) {
        return login_players.get(nickname);
    }

    public static List<Player> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    public static List<Game> getAllGames() {
        return new ArrayList<>(games.values());
    }

    public static ClientConnection getConnectionById(final int id) {
        return connects.get(id);
    }

    /**
     * Функция провероки
     * Если game равен null, то выбрасывает GameException.
     *
     * @param game - класс игры
     */
    protected static void gameIsNotNull(final Game game) throws GameException {
        if (game == null) {
            throw new GameException(GameErrorCode.GAME_NOT_FOUND);
        }
    }

    protected static void connectionIsAuthed(ClientConnection connection) throws GameException {
        if (connection.getPlayer() != null) {
            throw new GameException(GameErrorCode.PLAYER_IS_AUTH);
        }
    }

    /**
     * Функция провероки
     * Если player равен null, то выбрасывает GameException.
     *
     * @param player - класс игрока
     */
    protected static void playerIsNotNull(final Player player) throws GameException {
        if (player == null) {
            throw new GameException(GameErrorCode.PLAYER_NOT_FOUND);
        }
    }

    protected static void playerIsNotPlaying(final Player player) throws GameException {
        if (player.getState() == PlayerState.PLAYING) {
            throw new GameException(GameErrorCode.PLAYER_ALREADY_PLAYING);
        }
    }

    protected static void checkPlayerConnection(final Player player) throws GameException {
        ClientConnection connection = getConnectionById(player.getId());
        connectionIsNotNullAndConnected(connection);
    }

    protected static void connectionIsNotNullAndConnected(final ClientConnection connection) throws GameException {
        if (connection == null || !connection.isConnected()) {
            throw new GameException(GameErrorCode.CONNECTION_LOST);
        }
    }

    protected static void requestIsNotNull(final GameRequest request) throws GameException {
        if (request == null) {
            throw new GameException(GameErrorCode.INVALID_REQUEST);
        }
    }
}
