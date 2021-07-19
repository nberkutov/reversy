package services;

import dto.request.player.CreatePlayerRequest;
import exception.GameErrorCode;
import exception.GameException;
import models.ClientConnection;
import models.base.PlayerState;
import models.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PlayerServicesTest {
    private static DataBaseService bs;

    @BeforeAll
    private static void clearDateBase() {
        bs = new DataBaseService();
        DataBaseService.clearAll();
    }

    @Test
    void testIsPlayerCanSearchGameException() throws IOException, GameException {
        final int PORT = 8082;
        final String IP = "127.0.0.1";

        try {
            PlayerService.canPlayerSearchGame((ClientConnection) null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        try {
            PlayerService.canPlayerSearchGame((Player) null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }

        try {
            PlayerService.canPlayerSearchGame(new ClientConnection());
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        ServerSocket socket = new ServerSocket(PORT);
        Socket client = new Socket(IP, PORT);
        ClientConnection connection = new ClientConnection(client);
        Player player = PlayerService.createPlayer(new CreatePlayerRequest(), connection);
        player.setState(PlayerState.SEARCH_GAME);
        try {
            PlayerService.canPlayerSearchGame(player);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_CANNOT_FIND_GAME);
        }
        player.setState(PlayerState.PLAYING);
        try {
            PlayerService.canPlayerSearchGame(player);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_CANNOT_FIND_GAME);
        }
        connection.close();
        socket.close();
    }

    @Test
    void setNoneStatePlayer() throws GameException {
        Player player = new Player();
        player.setState(PlayerState.PLAYING);
        PlayerService.setPlayerStateNone(player);
        assertEquals(player.getState(), PlayerState.NONE);
    }

    @Test
    void setNoneStatePlayerException() {
        try {
            PlayerService.setPlayerStateNone((Player) null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
        try {
            PlayerService.setPlayerStateNone((ClientConnection) null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

    }

}
