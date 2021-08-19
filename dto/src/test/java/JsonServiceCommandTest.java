import commands.CommandRequest;
import commands.CommandResponse;
import dto.request.GameRequest;
import dto.request.player.*;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
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
import utils.JsonService;

import java.util.stream.Stream;

import static commands.CommandRequest.*;
import static commands.CommandResponse.CREATE_PLAYER;
import static commands.CommandResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


class JsonServiceCommandTest {

    private static Stream<Arguments> getCommandByRequestStream() {
        return Stream.of(
                Arguments.of(CommandRequest.CREATE_PLAYER, new CreatePlayerRequest(null)),
                Arguments.of(WANT_PLAY, new WantPlayRequest(null)),
                Arguments.of(PLAYING_MOVE, new MovePlayerRequest(0, null)),
                Arguments.of(PLAYER_AUTH, new AuthPlayerRequest(null)),
                Arguments.of(PLAYER_LOGOUT, new LogoutPlayerRequest()),
                Arguments.of(CREATE_ROOM, new CreateRoomRequest(PlayerColor.NONE, 1)),
                Arguments.of(JOIN_ROOM, new JoinRoomRequest(0)),
                Arguments.of(GET_ROOMS, new GetRoomsRequest()),
                Arguments.of(GET_REPLAY_GAME, new GetReplayGameRequest(0))
        );
    }

    @ParameterizedTest
    @MethodSource("getCommandByRequestStream")
    void getCommandByRequest(CommandRequest command, GameRequest request) throws ServerException, ServerException {
        assertEquals(command, JsonService.getCommandByRequest(request));
    }

    private static Stream<Arguments> getCommandByResponseStream() {
        return Stream.of(
                Arguments.of(ERROR, new ErrorResponse(null, null)),
                Arguments.of(MESSAGE, new MessageResponse(null)),
                Arguments.of(GAME_PLAYING, new GameBoardResponse(0, null, null, null)),
                Arguments.of(GAME_START, new SearchGameResponse(0, null)),
                Arguments.of(GAME_REPLAY, new ReplayResponse(null, null, null, null, null)),
                Arguments.of(CREATE_PLAYER, new CreatePlayerResponse(0, null)),
                Arguments.of(ROOM, new RoomResponse(0, null, null)),
                Arguments.of(ROOMS, new ListRoomResponse(null))
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
        final String json = JsonService.toJson(new CreatePlayerRequest("Player_Test"));
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
        final String json = JsonService.toJson(new SearchGameResponse(1001, PlayerColor.BLACK));
        final SearchGameResponse response = (SearchGameResponse) JsonService.getResponseFromMsg(CommandResponse.GAME_START.getCommandName() + " " + json);
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
        final CreatePlayerRequest request = new CreatePlayerRequest("Player_Test");
        final String msg = JsonService.toMsgParser(request);
        final String json = JsonService.toJson(request);
        final String msg_will = CommandRequest.CREATE_PLAYER.getCommandName() + " " + json;

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
        final SearchGameResponse response = new SearchGameResponse(1001, PlayerColor.BLACK);
        final String msg = JsonService.toMsgParser(response);
        final String json = JsonService.toJson(response);
        final String msg_will = CommandResponse.GAME_START.getCommandName() + " " + json;

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