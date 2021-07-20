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
    private static final Map<Integer, ClientConnection> connects = new ConcurrentHashMap<>();
    //MiniBD
    private static int gameIncrement = 0;
    private static int playerIncrement = 0;

    public static synchronized int getPlayerId() {
        return playerIncrement++;
    }

    public static synchronized int getGameId() {
        return gameIncrement++;
    }

    public static void clearAll() {
        games.clear();
        players.clear();
        connects.clear();
    }

    public static Game getGameById(final int gameId) {
        return games.get(gameId);
    }

    public static Game putGameIfAbsent(final int id, final Game game) {
        return games.putIfAbsent(id, game);
    }

    public static Player putPlayerIfAbsent(final int id, final Player player) {
        return players.putIfAbsent(id, player);
    }

    public static ClientConnection putConnectionIfAbsent(final int id, final ClientConnection connection) {
        return connects.putIfAbsent(id, connection);
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
    public static void gameIsNotNull(final Game game) throws GameException {
        if (game == null) {
            throw new GameException(GameErrorCode.GAME_NOT_FOUND);
        }
    }

    /**
     * Функция провероки
     * Если player равен null, то выбрасывает GameException.
     *
     * @param player - класс игрока
     */
    public static void playerIsNotNull(final Player player) throws GameException {
        if (player == null) {
            throw new GameException(GameErrorCode.PLAYER_NOT_FOUND);
        }
    }

    public static void playerIsNotPlaying(final Player player) throws GameException {
        if (player.getState() == PlayerState.PLAYING) {
            throw new GameException(GameErrorCode.PLAYER_ALREADY_PLAYING);
        }
    }

    public static void checkPlayerConnection(final Player player) throws GameException {
        ClientConnection connection = getConnectionById(player.getId());
        connectionIsNotNullAndConnected(connection);
    }

    public static void connectionIsNotNullAndConnected(final ClientConnection connection) throws GameException {
        if (connection == null || !connection.isConnected()) {
            throw new GameException(GameErrorCode.CONNECTION_LOST);
        }
    }

    public static void requestIsNotNull(final GameRequest request) throws GameException {
        if (request == null) {
            throw new GameException(GameErrorCode.INVALID_REQUEST);
        }
    }
}
