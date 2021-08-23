package org.example.models;

import org.example.exception.ServerException;
import org.example.models.game.Game;
import org.example.models.game.Room;
import org.example.models.player.User;

import java.util.List;

public interface DataBaseDao {
    Game getGameById(final long gameId);

    Game saveGame(Game game) throws ServerException;

    Room getRoomById(final long roomId);

    Room saveRoom(Room room) throws ServerException;

    void removeRoom(Room room) throws ServerException;

    User getUserById(final long id);

    User getUserByNickname(final String nickname);

    User saveUser(User user) throws ServerException;

    List<User> getAllPlayers();

    List<Game> getAllGames();

    List<Room> getRooms(final boolean needClose, final int offset, final int limit) throws ServerException;

    List<Room> getAllRooms();

    void clearAll();
}
