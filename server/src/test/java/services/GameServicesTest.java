package services;

import dto.request.player.CreatePlayerRequest;
import dto.request.player.GetReplayGameRequest;
import dto.request.player.MovePlayerRequest;
import exception.GameErrorCode;
import exception.ServerException;
import logic.BoardUtils;
import models.ClientConnection;
import models.base.GameResultState;
import models.base.GameState;
import models.base.PlayerState;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.game.Game;
import models.game.GameResult;
import models.player.RandomBotPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class GameServicesTest {
    private static final Server server = new Server();

    @BeforeEach
    private void clearDateBase() {
        DataBaseService.clearAll();
    }

    @Test
    void testDraw() throws ServerException {
        final String s = ""
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "0wbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb";
        final GameBoard board = BoardUtils.fromString(s);
        final Game game = new Game(board, new RandomBotPlayer(0, "Test"), new RandomBotPlayer(1, "Test1"));
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());
        final GameResult result = GameService.getGameResult(game);
        assertEquals(result.getResultState(), GameResultState.ORDINARY_VICTORY);
        assertEquals(result.getWinner(), game.getWhiteUser());
    }

    @Test
    void testWinBlack() throws ServerException {
        final String s = ""
                + "wwwwwwwb"
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "0wbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb";
        final GameBoard board = BoardUtils.fromString(s);
        final Game game = new Game(board, new RandomBotPlayer(0, "Test"), new RandomBotPlayer(1, "Test1"));
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());
        final GameResult result = GameService.getGameResult(game);
        assertEquals(result.getResultState(), GameResultState.ORDINARY_VICTORY);
        assertEquals(result.getWinner(), game.getBlackUser());
    }

    @Test
    void testWinWhite() throws ServerException {
        final String s = ""
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "0wbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbw";
        final GameBoard board = BoardUtils.fromString(s);
        final Game game = new Game(board, new RandomBotPlayer(0, "Test"), new RandomBotPlayer(1, "Test1"));
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());
        final GameResult result = GameService.getGameResult(game);
        assertEquals(result.getResultState(), GameResultState.ORDINARY_VICTORY);
        assertEquals(result.getWinner(), game.getWhiteUser());
    }

    @Test
    void testExceptionAfterEnd() throws ServerException {
        final String s = ""
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "wwwwwwww"
                + "0wbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbb"
                + "bbbbbbbw";
        final GameBoard board = BoardUtils.fromString(s);
        final Game game = new Game(board, new RandomBotPlayer(0, "Test"), new RandomBotPlayer(1, "Test1"));
        GameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());

        try {
            GameService.makePlayerMove(game, new Point(0, 4), game.getWhiteUser());
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.GAME_ENDED);
        }

    }

    @Test
    void testMoveException() throws IOException, ServerException {
        final int PORT = 8083;
        final String IP = "127.0.0.1";

        try {
            GameService.makePlayerMove(null, null);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            GameService.makePlayerMove(new MovePlayerRequest(-1, new Point()), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }

        final Game game = GameService.createGame(new RandomBotPlayer(0, "Bot1"), new RandomBotPlayer(1, "bot2"));

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final ClientConnection connection = new ClientConnection(client);
        PlayerService.createPlayer(new CreatePlayerRequest("Booot"), connection);
        try {
            GameService.makePlayerMove(new MovePlayerRequest(game.getId(), new Point()), connection);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.ILLEGAL_REQUEST);
        }

        try {
            GameService.makePlayerMove(new MovePlayerRequest(-1, new Point()), connection);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.GAME_NOT_FOUND);
        }

        connection.close();

        try {
            GameService.makePlayerMove(
                    new MovePlayerRequest(game.getId(), new Point()),
                    new ClientConnection(new Socket(IP, PORT))
            );
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }

        socket.close();
    }

    @Test
    void testCreateGame() throws ServerException {
        final RandomBotPlayer p1 = new RandomBotPlayer(1, "bot1");
        p1.setState(PlayerState.SEARCH_GAME);
        final RandomBotPlayer p2 = new RandomBotPlayer(2, "bot2");
        p2.setState(PlayerState.SEARCH_GAME);
        GameService.createGame(p1, p2);
        assertEquals(p1.getState(), PlayerState.PLAYING);
        assertEquals(p2.getState(), PlayerState.PLAYING);
    }

    @Test
    void testGetReplayGameException() throws ServerException, IOException {
        final int PORT = 8083;
        final String IP = "127.0.0.1";
        try {
            GameService.getReplayGame(null, null);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            GameService.getReplayGame(new GetReplayGameRequest(0), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.CONNECTION_LOST);
        }
        final RandomBotPlayer p1 = new RandomBotPlayer(1, "bot1");
        final RandomBotPlayer p2 = new RandomBotPlayer(2, "bot2");
        final Game game = GameService.createGame(p1, p2);

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final ClientConnection connection = new ClientConnection(client);
        PlayerService.createPlayer(new CreatePlayerRequest("Booot"), connection);

        try {
            GameService.getReplayGame(new GetReplayGameRequest(game.getId()), connection);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.GAME_NOT_FINISHED);
        }
        socket.close();
    }

    @Test
    void testGetReplayGame() throws ServerException, IOException {
        final int PORT = 8083;
        final String IP = "127.0.0.1";

        final RandomBotPlayer p1 = new RandomBotPlayer(1, "bot1");
        final RandomBotPlayer p2 = new RandomBotPlayer(2, "bot2");
        final Game game = GameService.createGame(p1, p2);

        game.addMove(p1.getColor(), new Point(0, 0));
        game.addMove(p2.getColor(), new Point(1, 1));
        game.addMove(p1.getColor(), new Point(2, 2));
        game.setResult(GameResult.winner(game.getBoard(), p1, p2));
        game.setState(GameState.END);
        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final ClientConnection connection = new ClientConnection(client);
        PlayerService.createPlayer(new CreatePlayerRequest("Booot"), connection);

        final Game responseGame = GameService.getReplayGame(new GetReplayGameRequest(game.getId()), connection);
        assertNotNull(responseGame);
        socket.close();
    }

    @Test
    void testCreateGameException() {
        try {
            GameService.createGame(new RandomBotPlayer(1, "bot2"), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
        try {
            GameService.createGame(null, new RandomBotPlayer(1, "bot2"));
            fail();
        } catch (final ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.PLAYER_NOT_FOUND);
        }
    }

}
