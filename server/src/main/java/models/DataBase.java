package models;

import lombok.Data;
import models.base.RoomState;
import models.game.Game;
import models.game.Room;
import models.player.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class DataBase implements Serializable, Cloneable {
    private Map<Integer, Game> games = new ConcurrentHashMap<>();
    private Map<Integer, User> players = new ConcurrentHashMap<>();
    private Map<String, User> login_players = new ConcurrentHashMap<>();
    private Map<String, ClientConnection> login_connects = new ConcurrentHashMap<>();
    private Map<Integer, ClientConnection> connects = new ConcurrentHashMap<>();
    private Map<Integer, Room> rooms = new ConcurrentHashMap<>();
    private int gameIncrement = 0;
    private int playerIncrement = 0;
    private int roomIncrement = 0;

    public synchronized int getPlayerId() {
        return playerIncrement++;
    }

    public synchronized int getGameId() {
        return gameIncrement++;
    }

    public synchronized int getRoomId() {
        return roomIncrement++;
    }

    public void clearAll() {
        games.clear();
        login_players.clear();
        players.clear();
        login_connects.clear();
        connects.clear();
        rooms.clear();
    }

    public Game getGameById(final int gameId) {
        return games.get(gameId);
    }

    public Room getRoomById(final int roomId) {
        return rooms.get(roomId);
    }

    public synchronized Game putGame(final User first, final User second) {
        int id = getGameId();
        Game game = new Game(id, first, second);
        games.put(id, game);
        return game;
    }

    public synchronized User putPlayer(final String nickname) {
        int id = getPlayerId();
        User user = new User(id, nickname);
        login_players.put(user.getNickname(), user);
        players.put(id, user);
        return user;
    }

    public synchronized Room putRoom() {
        int id = getRoomId();
        Room room = new Room(id);
        rooms.put(id, room);
        return room;
    }

    public void putConnection(final int id, final String nickname, final ClientConnection connection) {
        connects.put(id, connection);
        putConnection(nickname, connection);
    }

    public void removeConnection(final int id, final String nickname) {
        connects.remove(id);
        removeConnection(nickname);
    }

    public void removeConnection(final String nickname) {
        login_connects.remove(nickname);
    }

    public void putConnection(final String nickname, final ClientConnection connection) {
        login_connects.put(nickname, connection);
    }

    public User getPlayerByNickname(final String nickname) {
        return login_players.get(nickname);
    }

    public ClientConnection getConnectionByNickname(final String nickname) {
        return login_connects.get(nickname);
    }

    public ClientConnection getConnectionById(final int id) {
        return connects.get(id);
    }

    public void removeAllConnects() {
        for (Integer id : connects.keySet()) {
            ClientConnection connection = connects.get(id);
            User user = connection.getUser();
            if (user != null) {
                login_connects.remove(user.getNickname());
            }
            connects.remove(id);
        }
    }

    public List<User> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    public List<ClientConnection> getAllConnection() {
        return new ArrayList<>(connects.values());
    }

    public List<Game> getAllGames() {
        return new ArrayList<>(games.values());
    }

    public List<Room> getRooms(final boolean needClose, final int limit) {
        Stream<Room> stream = rooms.values().stream();
        if (!needClose) {
            stream = stream.filter(room -> room.getState() == RoomState.CLOSE);
        }
        return stream.limit(limit).collect(Collectors.toList());
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    @Override
    public DataBase clone() {
        DataBase db = new DataBase();
        db.setLogin_connects(new ConcurrentHashMap<>(login_connects));
        db.setConnects(new ConcurrentHashMap<>(connects));
        db.setGames(new ConcurrentHashMap<>(games));
        db.setLogin_players(new ConcurrentHashMap<>(login_players));
        db.setPlayers(new ConcurrentHashMap<>(players));
        db.setRooms(new ConcurrentHashMap<>(rooms));
        db.setGameIncrement(gameIncrement);
        db.setPlayerIncrement(playerIncrement);
        db.setRoomIncrement(roomIncrement);
        return db;
    }

    @Override
    public String toString() {
        return "{" +
                "games=" + games.size() +
                ", players=" + players.size() +
                ", login_players=" + login_players.size() +
                ", login_connects=" + login_connects.size() +
                ", connects=" + connects.size() +
                ", rooms=" + rooms.size() +
                '}';
    }
}
