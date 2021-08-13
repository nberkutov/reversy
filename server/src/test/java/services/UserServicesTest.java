package services;

import dto.request.player.AuthPlayerRequest;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.LogoutPlayerRequest;
import exception.GameErrorCode;
import exception.ServerException;
import models.ClientConnection;
import models.base.PlayerState;
import models.player.User;
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

public class UserServicesTest {
    private static final Server server = new Server();

    private static Stream<Arguments> getCreatePlayerByNickname() {
        return Stream.of(
                Arguments.of(new CreatePlayerRequest("a")),
                Arguments.of(new CreatePlayerRequest("      ")),
                Arguments.of(new CreatePlayerRequest("a     ")),
                Arguments.of(new CreatePlayerRequest("aaaaaaaaaaaaaaaaaaaaa")),
                Arguments.of(new CreatePlayerRequest("")),
                Arguments.of(new CreatePlayerRequest("                      "))
        );
    }

    @ParameterizedTest
    @MethodSource("getCreatePlayerByNickname")
    void testValidateNickname(CreatePlayerRequest request) throws IOException {
        final int PORT = 8086;
        final String IP = "127.0.0.1";
        DataBaseService.clearAll();
        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final ClientConnection connection = new ClientConnection(client);
        try {
            PlayerService.createPlayer(request, connection);
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_NICKNAME);
        }
        client.close();
        socket.close();
    }

    @Test
    void testCreatePlayerAndAuthPlayerException() throws IOException, ServerException {
        DataBaseService.clearAll();
        final int PORT = 8082;
        final String IP = "127.0.0.1";

        try {
            PlayerService.createPlayer(null, null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            PlayerService.createPlayer(new CreatePlayerRequest("Boooot"), null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        try {
            PlayerService.authPlayer(null, null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            PlayerService.authPlayer(new AuthPlayerRequest("Boooot"), null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final ClientConnection connection = new ClientConnection(client);
        final Socket client2 = new Socket(IP, PORT);
        final ClientConnection connection2 = new ClientConnection(client2);
        PlayerService.createPlayer(new CreatePlayerRequest("Boooot"), connection);

        try {
            PlayerService.createPlayer(new CreatePlayerRequest("Boooot"), connection);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_IS_AUTH);
        }

        try {
            PlayerService.createPlayer(new CreatePlayerRequest("Boooot"), connection2);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.NICKNAME_ALREADY_USED);
        }

        try {
            PlayerService.authPlayer(new AuthPlayerRequest("Boooot"), connection);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_IS_AUTH);
        }

        connection.close();
        connection2.close();
        socket.close();
    }

    @Test
    void testLogoutPlayerException() throws IOException, ServerException {
        DataBaseService.clearAll();
        final int PORT = 8082;
        final String IP = "127.0.0.1";

        try {
            PlayerService.logoutPlayer(null, null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            PlayerService.logoutPlayer(new LogoutPlayerRequest(), null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final ClientConnection connection = new ClientConnection(client);
        final Socket client2 = new Socket(IP, PORT);
        final ClientConnection connection2 = new ClientConnection(client2);
        PlayerService.createPlayer(new CreatePlayerRequest("Boooot"), connection);

        try {
            PlayerService.logoutPlayer(new LogoutPlayerRequest(), connection2);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }

        connection.close();
        connection2.close();
        socket.close();
    }

    @Test
    void testIsPlayerCanSearchGameException() throws IOException, ServerException {
        DataBaseService.clearAll();
        final int PORT = 8082;
        final String IP = "127.0.0.1";

        try {
            PlayerService.canPlayerSearchGame((ClientConnection) null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        try {
            PlayerService.canPlayerSearchGame((User) null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final ClientConnection connection = new ClientConnection(client);
        final User user = PlayerService.createPlayer(new CreatePlayerRequest("Booooot"), connection);
        user.setState(PlayerState.SEARCH_GAME);
        try {
            PlayerService.canPlayerSearchGame(user);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_CANT_PERFORM);
        }
        user.setState(PlayerState.PLAYING);
        try {
            PlayerService.canPlayerSearchGame(user);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_CANT_PERFORM);
        }
        connection.close();
        socket.close();
    }

    @Test
    void setNoneStatePlayer() throws ServerException {
        final User user = new User(0, "bot");
        user.setState(PlayerState.PLAYING);
        PlayerService.setPlayerStateNone(user);
        assertEquals(user.getState(), PlayerState.NONE);
    }

    @Test
    void setNoneStatePlayerException() {
        try {
            PlayerService.setPlayerStateNone((User) null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
        try {
            PlayerService.setPlayerStateNone((ClientConnection) null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

    }

}
