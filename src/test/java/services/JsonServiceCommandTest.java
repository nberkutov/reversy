package services;

import controllers.commands.CommandRequest;
import dto.request.player.*;
import dto.request.server.CreateGameRequest;
import exception.GameErrorCode;
import exception.GameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static controllers.commands.CommandRequest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


class JsonServiceCommandTest {

    private static Stream<Arguments> getCommandByRequestStream() {
        return Stream.of(
                Arguments.of(CREATE_PLAYER, new CreatePlayerRequest()),
                Arguments.of(WANT_PLAY, new WantPlayRequest()),
                Arguments.of(PRIVATE_CREATE_GAME, new CreateGameRequest()),
                Arguments.of(PLAYING_MOVE, new MovePlayerRequest()),
                Arguments.of(GET_GAME_INFO, new GetGameInfoRequest())
        );
    }

    @ParameterizedTest
    @MethodSource("getCommandByRequestStream")
    void getCommandByRequest(CommandRequest command, GameRequest request) throws GameException {
        assertEquals(command, JsonService.getCommandByRequest(request));
    }

    @Test
    void getCommandByRequestException() {
        try {
            JsonService.getCommandByRequest(null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            JsonService.getCommandByRequest(new GameRequest() {
            });
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_REQUEST);
        }
    }


//    @Test
//    public void toJsonParser() {
//
//    }
//
//    @Test
//    public void getRequestFromJson() {
//    }
}