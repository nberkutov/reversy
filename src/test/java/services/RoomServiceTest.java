package services;

import dto.request.player.CreatePlayerRequest;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import exception.GameErrorCode;
import exception.GameException;
import models.ClientConnection;
import models.base.PlayerState;
import models.game.Room;
import models.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class RoomServiceTest {
    private static DataBaseService bs;

    @BeforeAll
    private static void clearDateBase() {
        bs = new DataBaseService();
        DataBaseService.clearAll();
    }

    @Test
    void createRoomException() throws IOException, GameException {
        DataBaseService.clearAll();
        final int PORT = 8087;
        final String IP = "127.0.0.1";
        try {
            RoomService.createRoom(null, null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            RoomService.createRoom(new CreateRoomRequest(), null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        try {
            RoomService.createRoom(new CreateRoomRequest(), new ClientConnection(new Socket(IP, PORT)));
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        ServerSocket socket = new ServerSocket(PORT);
        Socket client = new Socket(IP, PORT);
        ClientConnection connection = new ClientConnection(client);


        try {
            RoomService.createRoom(new CreateRoomRequest(), connection);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }

        PlayerService.createPlayer(new CreatePlayerRequest("Boooot"), connection);

        RoomService.createRoom(new CreateRoomRequest(), connection);

        Socket client2 = new Socket(IP, PORT);
        ClientConnection connection2 = new ClientConnection(client2);
        Player player = PlayerService.createPlayer(new CreatePlayerRequest("Boooot2"), connection2);
        player.setState(PlayerState.SEARCH_GAME);

        try {
            RoomService.createRoom(new CreateRoomRequest(), connection2);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_CANT_PERFORM);
        }

        socket.close();
    }

    @Test
    void joinRoomException() throws IOException, GameException {
        DataBaseService.clearAll();
        final int PORT = 8087;
        final String IP = "127.0.0.1";
        try {
            RoomService.joinRoom(null, null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            RoomService.joinRoom(new JoinRoomRequest(), null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        try {
            RoomService.joinRoom(new JoinRoomRequest(), new ClientConnection(new Socket(IP, PORT)));
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        ServerSocket socket = new ServerSocket(PORT);
        Socket client = new Socket(IP, PORT);
        ClientConnection connection = new ClientConnection(client);


        try {
            RoomService.joinRoom(new JoinRoomRequest(), connection);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }

        PlayerService.createPlayer(new CreatePlayerRequest("Boooot"), connection);

        try {
            RoomService.joinRoom(new JoinRoomRequest(-1), connection);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.ROOM_NOT_FOUND);
        }

        Room room = RoomService.createRoom(new CreateRoomRequest(), connection);

        Socket client2 = new Socket(IP, PORT);
        ClientConnection connection2 = new ClientConnection(client2);
        PlayerService.createPlayer(new CreatePlayerRequest("Boooot2"), connection2);

        RoomService.joinRoom(new JoinRoomRequest(room.getId()), connection2);


        Socket client3 = new Socket(IP, PORT);
        ClientConnection connection3 = new ClientConnection(client3);
        PlayerService.createPlayer(new CreatePlayerRequest("Boooot3"), connection3);
        try {
            RoomService.joinRoom(new JoinRoomRequest(room.getId()), connection3);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.ROOM_IS_CLOSED);
        }

        socket.close();
    }

    @Test
    void getRoomsException() {
        try {
            RoomService.getRooms(null, null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            RoomService.getRooms(new GetRoomsRequest(), null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }
    }

    @Test
    void getRooms() throws IOException, GameException {
        DataBaseService.clearAll();
        final int PORT = 8087;
        final String IP = "127.0.0.1";
        ServerSocket socket = new ServerSocket(PORT);
        for (int i = 0; i < 10; i++) {
            Socket client = new Socket(IP, PORT);
            ClientConnection connection = new ClientConnection(client);

            PlayerService.createPlayer(new CreatePlayerRequest("Boooot" + i), connection);

            RoomService.createRoom(new CreateRoomRequest(), connection);
        }
        Socket observer = new Socket(IP, PORT);
        ClientConnection connectionObserver = new ClientConnection(observer);
        PlayerService.createPlayer(new CreatePlayerRequest("BooootObs"), connectionObserver);

        assertEquals(RoomService.getRooms(new GetRoomsRequest(), connectionObserver).size(), 10);
    }
}