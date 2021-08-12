package services;

import dto.request.GameRequest;
import dto.request.player.CreatePlayerRequest;
import exception.ServerException;
import logic.BoardFactory;
import models.base.interfaces.GameBoard;
import models.game.Game;
import models.player.RandomBotPlayer;
import org.junit.jupiter.api.Test;
import utils.JsonService;

import static org.junit.jupiter.api.Assertions.*;

class JsonServiceTest {

    @Test
    void testJsonSerialization() {
        final GameRequest request = new CreatePlayerRequest("TestNickName");
        final String json = JsonService.toJson(request);
        assertFalse(json.trim().isEmpty());
        final CreatePlayerRequest after = JsonService.fromJson(json, CreatePlayerRequest.class);
        assertEquals(request, after);
        assertNotEquals(after, new CreatePlayerRequest("NotTestNickName"));
    }

    @Test
    void testMapSerialization() throws ServerException {
        final GameBoard board = BoardFactory.generateStartedBoard();
        final String json = JsonService.toJson(board);
        assertFalse(json.trim().isEmpty());
        final GameBoard after = JsonService.fromJson(json, board.getClass());
        assertEquals(board, after);

        final Game game = new Game(board, new RandomBotPlayer(0, "Test"), new RandomBotPlayer(1, "Test1"));
        final String gameString = JsonService.toJson(game);
        final Game afterJson = JsonService.fromJson(gameString, Game.class);
        assertEquals(game, afterJson);
    }

}