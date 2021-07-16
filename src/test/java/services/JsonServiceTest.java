package services;

import dto.request.player.CreatePlayerRequest;
import dto.request.player.GameRequest;
import models.board.Board;
import models.game.Game;
import models.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonServiceTest {

    @Test
    void testJsonSerialization() {
        GameRequest request = new CreatePlayerRequest("TestNickName");
        String json = JsonService.toJson(request);
        assertFalse(json.trim().isEmpty());
        CreatePlayerRequest after = JsonService.fromJson(json, CreatePlayerRequest.class);
        assertEquals(request, after);
        assertNotEquals(after, new CreatePlayerRequest("NotTestNickName"));
    }

    @Test
    void testMapSerialization() {
        Board board = new Board();
        String json = JsonService.toJson(board);
        assertFalse(json.trim().isEmpty());
        Board after = JsonService.fromJson(json, Board.class);
        assertEquals(board, after);

        Game game = new Game(board, new Player(), new Player());
        String gameString = JsonService.toJson(game);
        Game afterJson = JsonService.fromJson(gameString, Game.class);
        assertEquals(game, afterJson);
    }

}