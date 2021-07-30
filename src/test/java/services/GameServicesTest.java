package services;

import dto.request.player.CreatePlayerRequest;
import dto.request.player.MovePlayerRequest;
import exception.GameErrorCode;
import exception.GameException;
import models.ClientConnection;
import models.base.GameResultState;
import models.base.PlayerState;
import models.board.Board;
import models.board.Point;
import models.game.Game;
import models.game.GameResult;
import models.player.RandomBotPlayer;
import models.player.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.utils.BoardUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameServicesTest {
    private static Server server = new Server();

    @BeforeEach
    private void clearDateBase() {
        DataBaseService.clearAll();
    }

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
        Board board = BoardUtils.fromString(s);
        Game game = new Game(board, new RandomBotPlayer(0, "Test"), new RandomBotPlayer(1, "Test1"));
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());
        GameResult result = GameService.getGameResult(game);
        assertEquals(result.getResultState(), GameResultState.ORDINARY_VICTORY);
        assertEquals(result.getWinner(), game.getWhiteUser());
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
        Board board = BoardUtils.fromString(s);
        Game game = new Game(board, new RandomBotPlayer(0, "Test"), new RandomBotPlayer(1, "Test1"));
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());
        GameResult result = GameService.getGameResult(game);
        assertEquals(result.getResultState(), GameResultState.ORDINARY_VICTORY);
        assertEquals(result.getWinner(), game.getBlackUser());
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
        Board board = BoardUtils.fromString(s);
        Game game = new Game(board, new RandomBotPlayer(0, "Test"), new RandomBotPlayer(1, "Test1"));
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());
        GameResult result = GameService.getGameResult(game);
        assertEquals(result.getResultState(), GameResultState.ORDINARY_VICTORY);
        assertEquals(result.getWinner(), game.getWhiteUser());
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
        Board board = BoardUtils.fromString(s);
        Game game = new Game(board, new RandomBotPlayer(0, "Test"), new RandomBotPlayer(1, "Test1"));
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());

        try {
            GameService.makePlayerMove(game, new Point(0, 4), game.getWhiteUser());
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.GAME_ENDED);
        }

    }

    @Test
    void testMoveException() throws GameException, IOException {
        final int PORT = 8083;
        final String IP = "127.0.0.1";

        try {
            GameService.makePlayerMove(null, null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            GameService.makePlayerMove(new MovePlayerRequest(-1, new Point()), null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        Game game = GameService.createGame(new RandomBotPlayer(0, "Bot1"), new RandomBotPlayer(1, "bot2"));

        ServerSocket socket = new ServerSocket(PORT);
        Socket client = new Socket(IP, PORT);
        ClientConnection connection = new ClientConnection(client);
        User user = PlayerService.createPlayer(new CreatePlayerRequest("Booot"), connection);
        try {
            GameService.makePlayerMove(new MovePlayerRequest(game.getId(), new Point()), connection);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.ILLEGAL_REQUEST);
        }

        try {
            GameService.makePlayerMove(new MovePlayerRequest(-1, new Point()), connection);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.GAME_NOT_FOUND);
        }

        connection.close();

        try {
            GameService.makePlayerMove(
                    new MovePlayerRequest(game.getId(), new Point()),
                    new ClientConnection(new Socket(IP, PORT))
            );
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }

        socket.close();
    }

    @Test
    void testCreateGame() throws GameException {
        RandomBotPlayer p1 = new RandomBotPlayer(1, "bot1");
        p1.setState(PlayerState.SEARCH_GAME);
        RandomBotPlayer p2 = new RandomBotPlayer(2, "bot2");
        p2.setState(PlayerState.SEARCH_GAME);
        Game game = GameService.createGame(p1, p2);
        assertEquals(p1.getState(), PlayerState.PLAYING);
        assertEquals(p2.getState(), PlayerState.PLAYING);
    }

    @Test
    void testCreateGameException() {
        try {
            GameService.createGame(new RandomBotPlayer(1, "bot2"), null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
        try {
            GameService.createGame(null, new RandomBotPlayer(1, "bot2"));
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
    }

}
