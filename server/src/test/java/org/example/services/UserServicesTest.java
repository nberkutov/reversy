package org.example.services;

import org.example.dto.request.player.AuthUserRequest;
import org.example.dto.request.player.CreateUserRequest;
import org.example.dto.request.player.LogoutPlayerRequest;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.base.PlayerState;
import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class UserServicesTest extends BaseServiceTest {

    @BeforeEach
    private void clearDateBase() {
        dataBaseDao.clearAll();
        cacheDataBaseDao.clearAll();
    }

    private static Stream<Arguments> getCreatePlayerByNickname() {
        return Stream.of(
                Arguments.of(new CreateUserRequest("a")),
                Arguments.of(new CreateUserRequest("      ")),
                Arguments.of(new CreateUserRequest("a     ")),
                Arguments.of(new CreateUserRequest("aaaaaaaaaaaaaaaaaaaaa")),
                Arguments.of(new CreateUserRequest("")),
                Arguments.of(new CreateUserRequest("                      "))
        );
    }

    @ParameterizedTest
    @MethodSource("getCreatePlayerByNickname")
    void testValidateNickname(final CreateUserRequest request) throws IOException {
        final int PORT = 8086;
        final String IP = "127.0.0.1";
//        DataBaseService.clearAll();
        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final UserConnection connection = new UserConnection(client);
        try {
            playerService.createUser(request, connection);
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_NICKNAME, e.getErrorCode());
        }
        client.close();
        socket.close();
    }

    @Test
    void testCreatePlayerAndAuthPlayerException() throws IOException, ServerException {
//        DataBaseService.clearAll();
        final int PORT = 8082;
        final String IP = "127.0.0.1";

        try {
            playerService.createUser(null, null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_REQUEST, e.getErrorCode());
        }

        try {
            playerService.createUser(new CreateUserRequest("Boooot"), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.CONNECTION_LOST, e.getErrorCode());
        }

        try {
            playerService.authPlayer(null, null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_REQUEST, e.getErrorCode());
        }

        try {
            playerService.authPlayer(new AuthUserRequest("Boooot"), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.CONNECTION_LOST, e.getErrorCode());
        }

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final UserConnection connection = new UserConnection(client);
        final Socket client2 = new Socket(IP, PORT);
        final UserConnection connection2 = new UserConnection(client2);
        playerService.createUser(new CreateUserRequest("Boooot"), connection);

        try {
            playerService.createUser(new CreateUserRequest("Boooot"), connection);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_IS_AUTH, e.getErrorCode());
        }

        try {
            playerService.createUser(new CreateUserRequest("Boooot"), connection2);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.NICKNAME_ALREADY_USED, e.getErrorCode());
        }

        try {
            playerService.authPlayer(new AuthUserRequest("Boooot"), connection);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_IS_AUTH, e.getErrorCode());
        }

        connection.close();
        connection2.close();
        socket.close();
    }

    @Test
    void testLogoutPlayerException() throws IOException, ServerException {
//        DataBaseService.clearAll();
        final int PORT = 8082;
        final String IP = "127.0.0.1";

        try {
            playerService.logoutPlayer(null, null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_REQUEST, e.getErrorCode());
        }

        try {
            playerService.logoutPlayer(new LogoutPlayerRequest(), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.CONNECTION_LOST, e.getErrorCode());
        }

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final UserConnection connection = new UserConnection(client);
        final Socket client2 = new Socket(IP, PORT);
        final UserConnection connection2 = new UserConnection(client2);
        playerService.createUser(new CreateUserRequest("Boooot"), connection);

        try {
            playerService.logoutPlayer(new LogoutPlayerRequest(), connection2);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_NOT_FOUND, e.getErrorCode());
        }

        connection.close();
        connection2.close();
        socket.close();
    }

    @Test
    void testIsPlayerCanSearchGameException() throws IOException, ServerException {
//        DataBaseService.clearAll();
        final int PORT = 8082;
        final String IP = "127.0.0.1";

        try {
            playerService.canPlayerSearchGame((UserConnection) null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.CONNECTION_LOST, e.getErrorCode());
        }

        try {
            playerService.canPlayerSearchGame((User) null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_NOT_FOUND, e.getErrorCode());
        }

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final UserConnection connection = new UserConnection(client);
        final User user = playerService.createUser(new CreateUserRequest("Booooot"), connection);
        user.setState(PlayerState.SEARCH_GAME);
        try {
            playerService.canPlayerSearchGame(user);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_CANT_PERFORM, e.getErrorCode());
        }
        user.setState(PlayerState.PLAYING);
        try {
            playerService.canPlayerSearchGame(user);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_CANT_PERFORM, e.getErrorCode());
        }
        connection.close();
        socket.close();
    }

    @Test
    void setNoneStatePlayer() throws ServerException {
        final User user = new User("bot");
        user.setState(PlayerState.PLAYING);
        playerService.setPlayerStateNone(user);
        assertEquals(PlayerState.NONE, user.getState());
    }

    @Test
    void setNoneStatePlayerException() {
        try {
            playerService.setPlayerStateNone((User) null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_NOT_FOUND, e.getErrorCode());
        }
        try {
            playerService.setPlayerStateNone((UserConnection) null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.CONNECTION_LOST, e.getErrorCode());
        }

    }

}
