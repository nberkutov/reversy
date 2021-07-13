package services;

import dto.request.player.CreatePlayerRequest;
import dto.request.player.MovePlayerRequest;
import exception.GameErrorCode;
import exception.GameException;
import models.*;
import models.base.GameResultState;
import models.base.PlayerState;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameServicesTest {
    private BaseService bs = new BaseService();

    @Test
    void testDraw() throws GameException {
        String s = ""
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "0wbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb";
        Board board = BoardUtilsTest.parse(s);
        Game game = new Game(board, new Player(), new Player());
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackPlayer());
        GameResult result = GameService.getGameResult(game);
        assertEquals(result.getResultState(), GameResultState.WINNER_FOUND);
        assertEquals(result.getWinner(), game.getWhitePlayer());
    }

    @Test
    void testWinBlack() throws GameException {
        String s = ""
                + "wwwwwwwb"
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "0wbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb";
        Board board = BoardUtilsTest.parse(s);
        Game game = new Game(board, new Player(), new Player());
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackPlayer());
        GameResult result = GameService.getGameResult(game);
        assertEquals(result.getResultState(), GameResultState.WINNER_FOUND);
        assertEquals(result.getWinner(), game.getBlackPlayer());
    }

    @Test
    void testWinWhite() throws GameException {
        String s = ""
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "0wbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbw";
        Board board = BoardUtilsTest.parse(s);
        Game game = new Game(board, new Player(), new Player());
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackPlayer());
        GameResult result = GameService.getGameResult(game);
        assertEquals(result.getResultState(), GameResultState.WINNER_FOUND);
        assertEquals(result.getWinner(), game.getWhitePlayer());
    }

    @Test
    void testExceptionAfterEnd() throws GameException {
        String s = ""
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "0wbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbw";
        Board board = BoardUtilsTest.parse(s);
        Game game = new Game(board, new Player(), new Player());
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackPlayer());

        try {
            GameService.makePlayerMove(game, new Point(0, 4), game.getWhitePlayer());
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.GAME_ENDED);
        }

    }

    @Test
    void testMoveException() throws GameException, IOException {
        try {
            GameService.makePlayerMove(null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }
        Game game = GameService.createGame(new Player(), new Player());
        try {
            GameService.makePlayerMove(new MovePlayerRequest(-1, game.getId(), new Point()));
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
        Player player = PlayerService.createPlayer(new CreatePlayerRequest(), null);
        try {
            GameService.makePlayerMove(new MovePlayerRequest(player.getId(), game.getId(), new Point()));
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }
        final int PORT = 8081;
        final String IP = "127.0.0.1";
        ServerSocket socket = new ServerSocket(PORT);

        Socket client = new Socket(IP, PORT);
        player.setConnection(new ClientConnection(client));

        try {
            GameService.makePlayerMove(new MovePlayerRequest(player.getId(), -1, new Point()));
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.GAME_NOT_FOUND);
        }
        player.closeConnect();
        socket.close();
    }

    @Test
    void testCreateGame() throws GameException {
        Player p1 = new Player();
        p1.setState(PlayerState.SEARCH_GAME);
        Player p2 = new Player();
        p2.setState(PlayerState.SEARCH_GAME);
        Game game = GameService.createGame(p1, p2);
        assertEquals(p1.getState(), PlayerState.PLAYING);
        assertEquals(p2.getState(), PlayerState.PLAYING);
    }

    @Test
    void testCreateGameException() {
        try {
            GameService.createGame(new Player(), null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
        try {
            GameService.createGame(null, new Player());
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
    }

}
