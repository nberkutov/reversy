package org.example.services;

import org.example.dto.request.GameRequest;
import org.example.dto.request.player.CreateUserRequest;
import org.example.exception.ServerException;
import org.example.logic.BoardFactory;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.game.Game;
import org.example.models.player.User;
import org.example.utils.JsonService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonServiceTest {

    @Test
    void testJsonSerialization() {
        final GameRequest request = new CreateUserRequest("TestNickName");
        final String json = JsonService.toJson(request);
        assertFalse(json.trim().isEmpty());
        final CreateUserRequest after = JsonService.fromJson(json, CreateUserRequest.class);
        assertEquals(request, after);
        assertNotEquals(after, new CreateUserRequest("NotTestNickName"));
    }

    @Test
    void testMapSerialization() throws ServerException {
        final GameBoard board = BoardFactory.generateStartedBoard();
        final String json = JsonService.toJson(board);
        assertFalse(json.trim().isEmpty());
        final GameBoard after = JsonService.fromJson(json, board.getClass());
        assertEquals(board, after);

        final Game game = new Game(board, new User( "Test"), new User("Test1"));
        final String gameString = JsonService.toJson(game);
        final Game afterJson = JsonService.fromJson(gameString, Game.class);
        assertEquals(game, afterJson);
    }

}