package services;

import dto.request.player.CreatePlayerRequest;
import exception.GameErrorCode;
import exception.GameException;
import models.ClientConnection;
import models.Player;
import models.base.PlayerState;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PlayerServicesTest {
    private BaseService bs = new BaseService();

    @Test
    void testIsPlayerCanSearchGameException() throws IOException, GameException {
        try {
            PlayerService.canPlayerSearchGame(null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            PlayerService.canPlayerSearchGame(-1);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
        Player player = PlayerService.createPlayer(new CreatePlayerRequest(), null);

        try {
            PlayerService.canPlayerSearchGame(player.getId());
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        final int PORT = 8081;
        final String IP = "127.0.0.1";
        ServerSocket socket = new ServerSocket(PORT);
        Socket client = new Socket(IP, PORT);
        player.setConnection(new ClientConnection(client));
        player.setState(PlayerState.SEARCH_GAME);
        try {
            PlayerService.canPlayerSearchGame(player.getId());
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_CANNOT_FIND_GAME);
        }
        player.setState(PlayerState.PLAYING);
        try {
            PlayerService.canPlayerSearchGame(player.getId());
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_CANNOT_FIND_GAME);
        }
        player.closeConnect();
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
    void setNoneStatePlayerException() throws GameException {
        try {
            PlayerService.setPlayerStateNone(null);
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
    }

}
