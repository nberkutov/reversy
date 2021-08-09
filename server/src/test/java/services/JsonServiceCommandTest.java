package services;

import controllers.commands.CommandRequest;
import controllers.commands.CommandResponse;
import dto.request.GameRequest;
import dto.request.player.*;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import dto.request.server.CreateGameRequest;
import dto.response.ErrorResponse;
import dto.response.GameResponse;
import dto.response.game.GameBoardResponse;
import dto.response.game.ReplayResponse;
import dto.response.player.CreatePlayerResponse;
import dto.response.player.MessageResponse;
import dto.response.player.SearchGameResponse;
import dto.response.room.ListRoomResponse;
import dto.response.room.RoomResponse;
import exception.GameErrorCode;
import exception.ServerException;
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
                Arguments.of(SEARCH_CREATE_GAME, new CreateGameRequest()),
                Arguments.of(PLAYING_MOVE, new MovePlayerRequest()),
                Arguments.of(PLAYER_AUTH, new AuthPlayerRequest()),
                Arguments.of(PLAYER_LOGOUT, new LogoutPlayerRequest()),
                Arguments.of(CREATE_ROOM, new CreateRoomRequest()),
                Arguments.of(JOIN_ROOM, new JoinRoomRequest()),
                Arguments.of(GET_ROOMS, new GetRoomsRequest()),
                Arguments.of(GET_REPLAY_GAME, new GetReplayGameRequest())
        );
    }

    @ParameterizedTest
    @MethodSource("getCommandByRequestStream")
    void getCommandByRequest(CommandRequest command, GameRequest request) throws ServerException, ServerException {
        assertEquals(command, JsonService.getCommandByRequest(request));
    }

    private static Stream<Arguments> getCommandByResponseStream() {
        return Stream.of(
                Arguments.of(ERROR, new ErrorResponse()),
                Arguments.of(MESSAGE, new MessageResponse()),
                Arguments.of(GAME_PLAYING, new GameBoardResponse()),
                Arguments.of(GAME_START, new SearchGameResponse()),
                Arguments.of(GAME_REPLAY, new ReplayResponse()),
                Arguments.of(CREATE_PLAYER, new CreatePlayerResponse()),
                Arguments.of(ROOM, new RoomResponse()),
                Arguments.of(ROOMS, new ListRoomResponse())
        );
    }

    @Test
    void getCommandByRequestException() {
        try {
            JsonService.getCommandByRequest(null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            JsonService.getCommandByRequest(new GameRequest() {
            });
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_REQUEST);
        }
    }

    @Test
    void testCountCommandAndRequest() {
        assertEquals(getCommandByRequestStream().count(), CommandRequest.values().length);
    }

    @ParameterizedTest
    @MethodSource("getCommandByResponseStream")
    void getCommandByResponse(CommandResponse command, GameResponse response) throws ServerException {
        assertEquals(command, JsonService.getCommandByResponse(response));
    }

    @Test
    void testCountCommandAndResponse() {
        assertEquals(getCommandByResponseStream().count(), CommandResponse.values().length);
    }

    @Test
    void getCommandByResponseException() {
        try {
            JsonService.getCommandByResponse(null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_RESPONSE);
        }

        try {
            JsonService.getCommandByResponse(new GameResponse() {
            });
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_RESPONSE);
        }
    }


    @Test
    void fromMsgParserRequest() throws ServerException {
        String json = JsonService.toJson(new CreatePlayerRequest("Player_Test"));
        CreatePlayerRequest request = (CreatePlayerRequest) JsonService.getRequestFromMsg(CommandRequest.CREATE_PLAYER.getCommandName() + " " + json);
        assertEquals(request.getNickname(), "Player_Test");
    }

    @Test
    void fromMsgParserRequestException() {
        try {
            JsonService.getRequestFromMsg(null);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MESSAGE_DTO);
        }

        try {
            JsonService.getRequestFromMsg("         ");
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MESSAGE_DTO);
        }

        try {
            JsonService.getRequestFromMsg("abrakadabra {0,0}");
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_REQUEST);
        }
    }


    @Test
    void fromMsgParserResponse() throws ServerException {
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
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MESSAGE_DTO);
        }

        try {
            JsonService.getResponseFromMsg("         ");
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_MESSAGE_DTO);
        }

        try {
            JsonService.getResponseFromMsg("abrakadabra {0,0}");
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_RESPONSE);
        }
    }

    @Test
    void toMsgParseRequest() throws ServerException {
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
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_REQUEST);
        }

        try {
            JsonService.toMsgParser(new GameRequest() {
            });
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_REQUEST);
        }
    }

    @Test
    void toMsgParseResponse() throws ServerException {
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
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.INVALID_RESPONSE);
        }

        try {
            JsonService.toMsgParser(new GameResponse() {
            });
            fail();
        } catch (ServerException e) {
            assertEquals(e.getErrorCode(), GameErrorCode.UNKNOWN_RESPONSE);
        }
    }
}