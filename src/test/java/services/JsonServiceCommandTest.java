package services;

import controllers.commands.CommandRequest;
import controllers.commands.CommandResponse;
import dto.request.player.*;
import dto.request.server.CreateGameRequest;
import dto.response.*;
import dto.response.player.CreatePlayerResponse;
import exception.GameErrorCode;
import exception.GameException;
import models.base.PlayerColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static controllers.commands.CommandRequest.*;
import static controllers.commands.CommandResponse.CREATE_PLAYER;
import static controllers.commands.CommandResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


class JsonServiceCommandTest {

    private static Stream<Arguments> getCommandByRequestStream() {
        return Stream.of(
                Arguments.of(CommandRequest.CREATE_PLAYER, new CreatePlayerRequest()),
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

    private static Stream<Arguments> getCommandByResponseStream() {
        return Stream.of(
                Arguments.of(ERROR, new ErrorResponse()),
                Arguments.of(MESSAGE, new MessageResponse()),
                Arguments.of(GAME_PLAYING, new GameBoardResponse()),
                Arguments.of(GAME_START, new SearchGameResponse()),
                Arguments.of(CREATE_PLAYER, new CreatePlayerResponse())
        );
    }

    @ParameterizedTest
    @MethodSource("getCommandByResponseStream")
    void getCommandByResponse(CommandResponse command, GameResponse response) throws GameException {
        assertEquals(command, JsonService.getCommandByResponse(response));
    }

    @Test
    void getCommandByResponseException() {
        try {
            JsonService.getCommandByResponse(null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_RESPONSE);
        }

        try {
            JsonService.getCommandByResponse(new GameResponse() {
            });
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_RESPONSE);
        }
    }


    @Test
    void fromMsgParserRequest() throws GameException {
        String json = JsonService.toJson(new CreatePlayerRequest("Player_Test"));
        CreatePlayerRequest request = (CreatePlayerRequest) JsonService.getRequestFromMsg(CommandRequest.CREATE_PLAYER.getCommandName() + " " + json);
        assertEquals(request.getNickname(), "Player_Test");
    }

    @Test
    void fromMsgParserRequestException() {
        try {
            JsonService.getRequestFromMsg(null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MESSAGE_DTO);
        }

        try {
            JsonService.getRequestFromMsg("         ");
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MESSAGE_DTO);
        }

        try {
            JsonService.getRequestFromMsg("abrakadabra {0,0}");
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_REQUEST);
        }
    }


    @Test
    void fromMsgParserResponse() throws GameException {
        String json = JsonService.toJson(new SearchGameResponse(1001, PlayerColor.BLACK));
        SearchGameResponse response = (SearchGameResponse) JsonService.getResponseFromMsg(CommandResponse.GAME_START.getCommandName() + " " + json);
        assertEquals(response.getGameId(), 1001);
        assertEquals(response.getColor(), PlayerColor.BLACK);
    }

    @Test
    void fromMsgParserResponseException() {
        try {
            JsonService.getResponseFromMsg(null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MESSAGE_DTO);
        }

        try {
            JsonService.getResponseFromMsg("         ");
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MESSAGE_DTO);
        }

        try {
            JsonService.getResponseFromMsg("abrakadabra {0,0}");
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_RESPONSE);
        }
    }

    @Test
    void toMsgParseRequest() throws GameException {
        CreatePlayerRequest request = new CreatePlayerRequest("Player_Test");
        String msg = JsonService.toMsgParser(request);
        String json = JsonService.toJson(request);
        String msg_will = CommandRequest.CREATE_PLAYER.getCommandName() + " " + json;

        assertEquals(msg, msg_will);
    }

    @Test
    void toMsgParseRequestException() {
        try {
            JsonService.toMsgParser((GameRequest) null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            JsonService.toMsgParser(new GameRequest() {
            });
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_REQUEST);
        }
    }

    @Test
    void toMsgParseResponse() throws GameException {
        SearchGameResponse response = new SearchGameResponse(1001, PlayerColor.BLACK);
        String msg = JsonService.toMsgParser(response);
        String json = JsonService.toJson(response);
        String msg_will = CommandResponse.GAME_START.getCommandName() + " " + json;

        assertEquals(msg, msg_will);
    }

    @Test
    void toMsgParseResponseException() {
        try {
            JsonService.toMsgParser((GameResponse) null);
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_RESPONSE);
        }

        try {
            JsonService.toMsgParser(new GameResponse() {
            });
            fail();
        } catch (GameException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_RESPONSE);
        }
    }
}