package services;

import dto.request.GameRequest;
import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.base.PlayerState;
import models.base.RoomState;
import models.game.Game;
import models.game.Room;
import models.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DataBaseService {
    private static final Map<Integer, Game> games = new ConcurrentHashMap<>();
    private static final Map<Integer, Player> players = new ConcurrentHashMap<>();
    private static final Map<String, Player> login_players = new ConcurrentHashMap<>();
    private static final Map<String, ClientConnection> login_connects = new ConcurrentHashMap<>();
    private static final Map<Integer, ClientConnection> connects = new ConcurrentHashMap<>();
    private static final Map<Integer, Room> rooms = new ConcurrentHashMap<>();
    private static int gameIncrement = 0;
    private static int playerIncrement = 0;
    private static int roomIncrement = 0;

    protected static synchronized int getPlayerId() {
        return playerIncrement++;
    }

    protected static synchronized int getGameId() {
        return gameIncrement++;
    }

    protected static synchronized int getRoomId() {
        return roomIncrement++;
    }

    public static void clearAll() {
        games.clear();
        login_players.clear();
        players.clear();
        login_connects.clear();
        connects.clear();
        rooms.clear();
    }

    public static Game getGameById(final int gameId) {
        return games.get(gameId);
    }

    public static Room getRoomById(final int roomId) {
        return rooms.get(roomId);
    }

    public static synchronized Game putGame(final Player first, final Player second) {
        int id = getRoomId();
        Game game = new Game(id, first, second);
        games.put(id, game);
        return game;
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

    public static synchronized Room putRoom() {
        int id = getRoomId();
        Room room = new Room(id);
        rooms.put(id, room);
        return room;
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

    public static List<Room> getRooms(final boolean needClose, final int limit) {
        Stream<Room> stream = rooms.values().stream();
        if (!needClose) {
            stream = stream.filter(room -> room.getState() == RoomState.CLOSE);
        }
        return stream.limit(limit).collect(Collectors.toList());
    }

    public static List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
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

    protected static void roomIsNotNull(final Room room) throws GameException {
        if (room == null) {
            throw new GameException(GameErrorCode.ROOM_NOT_FOUND);
        }
    }

    protected static void playerIsNotPlaying(final Player player) throws GameException {
        if (player.getState() == PlayerState.PLAYING) {
            throw new GameException(GameErrorCode.PLAYER_ALREADY_PLAYING);
        }
    }

    protected static void playerIsStateNone(final Player player) throws GameException {
        if (player.getState() == PlayerState.NONE) {
            throw new GameException(GameErrorCode.PLAYER_CANT_PERFORM);
        }
    }

    protected static void playerIsNotStateNone(final Player player) throws GameException {
        if (player.getState() != PlayerState.NONE) {
            throw new GameException(GameErrorCode.PLAYER_CANT_PERFORM);
        }
    }

    protected static void playerIsNotInRoom(final Player player) throws GameException {
        if (player.getState() == PlayerState.WAITING_ROOM) {
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

    protected static void checkRequestAndConnection(GameRequest request, ClientConnection connection) throws GameException {
        requestIsNotNull(request);
        connectionIsNotNullAndConnected(connection);
    }
}
