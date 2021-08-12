package services;

import dto.request.player.CreatePlayerRequest;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import exception.GameErrorCode;
import exception.ServerException;
import models.ClientConnection;
import models.base.PlayerState;
import models.game.Room;
import models.player.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class RoomServiceTest {
    private static final Server server = new Server();

    @BeforeEach
    private void clearDateBase() {
        DataBaseService.clearAll();
    }

    @Test
    void createRoomException() throws IOException, ServerException {
        final int PORT = 8087;
        final String IP = "127.0.0.1";
        try {
            RoomService.createRoom(null, null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            RoomService.createRoom(new CreateRoomRequest(), null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final ClientConnection connection = new ClientConnection(client);


        try {
            RoomService.createRoom(new CreateRoomRequest(), connection);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }

        PlayerService.createPlayer(new CreatePlayerRequest("Boooot"), connection);

        RoomService.createRoom(new CreateRoomRequest(), connection);

        final Socket client2 = new Socket(IP, PORT);
        final ClientConnection connection2 = new ClientConnection(client2);
        final User user = PlayerService.createPlayer(new CreatePlayerRequest("Boooot2"), connection2);
        user.setState(PlayerState.SEARCH_GAME);

        try {
            RoomService.createRoom(new CreateRoomRequest(), connection2);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_CANT_PERFORM);
        }

        socket.close();
    }

    @Test
    void joinRoomException() throws IOException, ServerException {
        final int PORT = 8087;
        final String IP = "127.0.0.1";
        try {
            RoomService.joinRoomAndCreateGame(null, null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            RoomService.joinRoomAndCreateGame(new JoinRoomRequest(0), null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final ClientConnection connection = new ClientConnection(client);


        try {
            RoomService.joinRoomAndCreateGame(new JoinRoomRequest(0), connection);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }

        PlayerService.createPlayer(new CreatePlayerRequest("Boooot"), connection);

        try {
            RoomService.joinRoomAndCreateGame(new JoinRoomRequest(-1), connection);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.ROOM_NOT_FOUND);
        }

        final Room room = RoomService.createRoom(new CreateRoomRequest(), connection);

        final Socket client2 = new Socket(IP, PORT);
        final ClientConnection connection2 = new ClientConnection(client2);
        PlayerService.createPlayer(new CreatePlayerRequest("Boooot2"), connection2);

        RoomService.joinRoomAndCreateGame(new JoinRoomRequest(room.getId()), connection2);


        final Socket client3 = new Socket(IP, PORT);
        final ClientConnection connection3 = new ClientConnection(client3);
        PlayerService.createPlayer(new CreatePlayerRequest("Boooot3"), connection3);
        try {
            RoomService.joinRoomAndCreateGame(new JoinRoomRequest(room.getId()), connection3);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.ROOM_IS_CLOSED);
        }

        socket.close();
    }

    @Test
    void getRoomsException() {
        try {
            RoomService.getRooms(null, null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            RoomService.getRooms(new GetRoomsRequest(), null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }
    }

    @Test
    void getRooms() throws IOException, ServerException {
        final int PORT = 8087;
        final String IP = "127.0.0.1";
        new ServerSocket(PORT);
        for (int i = 0; i < 10; i++) {
            final Socket client = new Socket(IP, PORT);
            final ClientConnection connection = new ClientConnection(client);

            PlayerService.createPlayer(new CreatePlayerRequest("Boooot" + i), connection);

            RoomService.createRoom(new CreateRoomRequest(), connection);
        }
        final Socket observer = new Socket(IP, PORT);
        final ClientConnection connectionObserver = new ClientConnection(observer);
        PlayerService.createPlayer(new CreatePlayerRequest("BooootObs"), connectionObserver);

        assertEquals(RoomService.getRooms(new GetRoomsRequest(), connectionObserver).size(), 10);
    }
}