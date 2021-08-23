package gui;

import org.example.dto.response.player.PlayerResponse;
import org.example.dto.response.room.ListRoomResponse;
import org.example.dto.response.room.RoomResponse;
import org.example.models.ClientConnection;
import org.example.models.base.GameState;
import org.example.models.base.interfaces.GameBoard;

public interface GUI {

    void init(ClientConnection connection);

    void closeAuthAndInitMenu(long playerId, String nickname);

    void updateMenu(ListRoomResponse rooms);

    void closeMenuAndInitAuth();

    void initGame(long gameId, String nickOpponent);

    void initRoom(RoomResponse roomResponse);

    void closeRoom();

    void initPlayerInfo(PlayerResponse playerResponse);

    void updateGameTitle(String title);

    void updateGame(GameBoard board, GameState state);

    void closeGame();

    void createError(String error);

    void createInfo(String info);

    void createMessage(String message);
}
