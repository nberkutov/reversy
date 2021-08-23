package org.example.services;

import org.example.dto.request.player.CreateUserRequest;
import org.example.dto.request.room.CreateRoomRequest;
import org.example.dto.request.room.GetRoomsRequest;
import org.example.dto.request.room.JoinRoomRequest;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.base.PlayerState;
import org.example.models.game.Room;
import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class RoomServiceTest extends BaseServiceTest {

    @BeforeEach
    private void clearDateBase() {
        dataBaseDao.clearAll();
        cacheDataBaseDao.clearAll();
    }

    @Test
    void createRoomException() throws IOException, ServerException {
        final int PORT = 8087;
        final String IP = "127.0.0.1";
        try {
            roomService.createRoom(null, null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_REQUEST, e.getErrorCode());
        }

        try {
            roomService.createRoom(new CreateRoomRequest(), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.CONNECTION_LOST, e.getErrorCode());
        }

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final UserConnection connection = new UserConnection(client);


        try {
            roomService.createRoom(new CreateRoomRequest(), connection);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_NOT_FOUND, e.getErrorCode());
        }

        playerService.createUser(new CreateUserRequest("Boooot"), connection);

        roomService.createRoom(new CreateRoomRequest(), connection);

        final Socket client2 = new Socket(IP, PORT);
        final UserConnection connection2 = new UserConnection(client2);
        final User user = playerService.createUser(new CreateUserRequest("Boooot2"), connection2);
        user.setState(PlayerState.SEARCH_GAME);
        dataBaseDao.saveUser(user);
        try {
            roomService.createRoom(new CreateRoomRequest(), connection2);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_CANT_PERFORM, e.getErrorCode());
        }

        socket.close();
    }

    @Test
    void joinRoomException() throws IOException, ServerException {
        final int PORT = 8087;
        final String IP = "127.0.0.1";
        try {
            roomService.joinRoomAndCreateGame(null, null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_REQUEST, e.getErrorCode());
        }

        try {
            roomService.joinRoomAndCreateGame(new JoinRoomRequest(0), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.CONNECTION_LOST, e.getErrorCode());
        }

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final UserConnection connection = new UserConnection(client);


        try {
            roomService.joinRoomAndCreateGame(new JoinRoomRequest(0), connection);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_NOT_FOUND, e.getErrorCode());
        }

        playerService.createUser(new CreateUserRequest("Boooot"), connection);

        try {
            roomService.joinRoomAndCreateGame(new JoinRoomRequest(-1), connection);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.ROOM_NOT_FOUND, e.getErrorCode());
        }

        final Room room = roomService.createRoom(new CreateRoomRequest(), connection);

        final Socket client2 = new Socket(IP, PORT);
        final UserConnection connection2 = new UserConnection(client2);
        playerService.createUser(new CreateUserRequest("Boooot2"), connection2);

        roomService.joinRoomAndCreateGame(new JoinRoomRequest(room.getId()), connection2);


        final Socket client3 = new Socket(IP, PORT);
        final UserConnection connection3 = new UserConnection(client3);
        playerService.createUser(new CreateUserRequest("Boooot3"), connection3);
        try {
            roomService.joinRoomAndCreateGame(new JoinRoomRequest(room.getId()), connection3);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.ROOM_IS_CLOSED, e.getErrorCode());
        }

        socket.close();
    }

    @Test
    void getRoomsException() {
        try {
            roomService.getRooms(null, null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_REQUEST, e.getErrorCode());
        }

        try {
            roomService.getRooms(new GetRoomsRequest(), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.CONNECTION_LOST, e.getErrorCode());
        }
    }

    @Test
    void getRooms() throws IOException, ServerException {
        final int PORT = 8087;
        final String IP = "127.0.0.1";
        new ServerSocket(PORT);
        for (int i = 0; i < 10; i++) {
            final Socket client = new Socket(IP, PORT);
            final UserConnection connection = new UserConnection(client);

            playerService.createUser(new CreateUserRequest("Boooot" + i), connection);

            roomService.createRoom(new CreateRoomRequest(), connection);
        }
        final Socket observer = new Socket(IP, PORT);
        final UserConnection connectionObserver = new UserConnection(observer);
        playerService.createUser(new CreateUserRequest("BooootObs"), connectionObserver);

        assertEquals(10, roomService.getRooms(new GetRoomsRequest(), connectionObserver).size());
    }
}