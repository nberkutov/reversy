package org.example.services;

import org.example.dto.request.player.CreateUserRequest;
import org.example.dto.request.player.GetReplayGameRequest;
import org.example.dto.request.player.MovePlayerRequest;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.logic.BoardUtils;
import org.example.models.base.GameResultState;
import org.example.models.base.GameState;
import org.example.models.base.PlayerState;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.game.Game;
import org.example.models.game.GameResult;
import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class GameServicesTest extends BaseServiceTest {

    @BeforeEach
    private void clearDateBase() {
        dataBaseDao.clearAll();
        cacheDataBaseDao.clearAll();
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
        final Game game = new Game(board, new User("Test"), new User("Test1"));

        gameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());
        final GameResult result = gameService.getGameResult(game);
        assertEquals(GameResultState.ORDINARY_VICTORY, result.getResultState());
        assertEquals(result.getWinner(), game.getWhiteUser());
        assertEquals(dataBaseDao.getGameById(game.getId()), game);
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
        final Game game = new Game(board, new User("Test"), new User("Test1"));
        gameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());
        final GameResult result = gameService.getGameResult(game);
        assertEquals(GameResultState.ORDINARY_VICTORY, result.getResultState());
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
        final Game game = new Game(board, new User("Test"), new User("Test1"));
        gameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());
        final GameResult result = gameService.getGameResult(game);
        assertEquals(GameResultState.ORDINARY_VICTORY, result.getResultState());
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
        final Game game = new Game(board, new User("Test"), new User("Test1"));
        gameService.makePlayerMove(game, new Point(0, 4), game.getBlackUser());

        try {
            gameService.makePlayerMove(game, new Point(0, 4), game.getWhiteUser());
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.GAME_ENDED, e.getErrorCode());
        }

    }

    @Test
    void testMoveException() throws IOException, ServerException {
        final int PORT = 8083;
        final String IP = "127.0.0.1";

        try {
            gameService.makePlayerMove(null, null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_REQUEST, e.getErrorCode());
        }

        try {
            gameService.makePlayerMove(new MovePlayerRequest(-1, new Point()), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.CONNECTION_LOST, e.getErrorCode());
        }

        final Game game = gameService.createGame(new User("Bot1"), new User("bot2"));

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final UserConnection connection = new UserConnection(client);
        playerService.createUser(new CreateUserRequest("Booot"), connection);
        try {
            gameService.makePlayerMove(new MovePlayerRequest(game.getId(), new Point()), connection);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.ILLEGAL_REQUEST, e.getErrorCode());
        }

        try {
            gameService.makePlayerMove(new MovePlayerRequest(-1, new Point()), connection);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.GAME_NOT_FOUND, e.getErrorCode());
        }

        connection.close();

        try {
            gameService.makePlayerMove(
                    new MovePlayerRequest(game.getId(), new Point()),
                    new UserConnection(new Socket(IP, PORT))
            );
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_NOT_FOUND, e.getErrorCode());
        }

        socket.close();
    }

    @Test
    void testCreateGame() throws ServerException {
        final User p1 = new User("bot1");
        p1.setState(PlayerState.SEARCH_GAME);
        final User p2 = new User("bot2");
        p2.setState(PlayerState.SEARCH_GAME);
        gameService.createGame(p1, p2);
        assertEquals(PlayerState.PLAYING, p1.getState());
        assertEquals(PlayerState.PLAYING, p2.getState());
    }

    @Test
    void testGetReplayGameException() throws ServerException, IOException {
        final int PORT = 8083;
        final String IP = "127.0.0.1";
        try {
            gameService.getReplayGame(null, null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_REQUEST, e.getErrorCode());
        }

        try {
            gameService.getReplayGame(new GetReplayGameRequest(0), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.CONNECTION_LOST, e.getErrorCode());
        }
        final User p1 = new User("bot1");
        final User p2 = new User("bot2");
        final Game game = gameService.createGame(p1, p2);

        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final UserConnection connection = new UserConnection(client);
        playerService.createUser(new CreateUserRequest("Booot"), connection);

        try {
            gameService.getReplayGame(new GetReplayGameRequest(game.getId()), connection);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.GAME_NOT_FINISHED, e.getErrorCode());
        }
        socket.close();
    }

    @Test
    void testGetReplayGame() throws ServerException, IOException {
        final int PORT = 8083;
        final String IP = "127.0.0.1";

        final User p1 = new User("bot1");
        final User p2 = new User("bot2");
        final Game game = gameService.createGame(p1, p2);

        game.addMove(p1.getColor(), new Point(0, 0));
        game.addMove(p2.getColor(), new Point(1, 1));
        game.addMove(p1.getColor(), new Point(2, 2));
        game.setResult(GameResult.winner(p1, p2));
        game.setState(GameState.END);
        dataBaseDao.saveGame(game);
        final ServerSocket socket = new ServerSocket(PORT);
        final Socket client = new Socket(IP, PORT);
        final UserConnection connection = new UserConnection(client);
        playerService.createUser(new CreateUserRequest("Booot"), connection);

        final Game responseGame = gameService.getReplayGame(new GetReplayGameRequest(game.getId()), connection);
        assertNotNull(responseGame);
        socket.close();
    }

    @Test
    void testCreateGameException() {
        try {
            gameService.createGame(new User("bot2"), null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_NOT_FOUND, e.getErrorCode());
        }
        try {
            gameService.createGame(null, new User("bot2"));
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.PLAYER_NOT_FOUND, e.getErrorCode());
        }
    }

}
