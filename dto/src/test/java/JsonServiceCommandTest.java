import org.example.commands.CommandRequest;
import org.example.commands.CommandResponse;
import org.example.dto.request.GameRequest;
import org.example.dto.request.player.*;
import org.example.dto.request.room.CreateRoomRequest;
import org.example.dto.request.room.GetRoomsRequest;
import org.example.dto.request.room.JoinRoomRequest;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.GameResponse;
import org.example.dto.response.game.CreateGameResponse;
import org.example.dto.response.game.GameBoardResponse;
import org.example.dto.response.game.ReplayResponse;
import org.example.dto.response.player.CreatePlayerResponse;
import org.example.dto.response.player.LogoutResponse;
import org.example.dto.response.player.MessageResponse;
import org.example.dto.response.player.PlayerResponse;
import org.example.dto.response.room.ListRoomResponse;
import org.example.dto.response.room.RoomResponse;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.base.PlayerColor;
import org.example.utils.JsonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.example.commands.CommandRequest.*;
import static org.example.commands.CommandResponse.CREATE_PLAYER;
import static org.example.commands.CommandResponse.*;
import static org.junit.jupiter.api.Assertions.*;


class JsonServiceCommandTest {

    private static Stream<Arguments> getCommandByRequestStream() {
        return Stream.of(
                Arguments.of(CommandRequest.CREATE_PLAYER, new CreateUserRequest(null)),
                Arguments.of(WANT_PLAY, new WantPlayRequest()),
                Arguments.of(GET_INFO_USER, new GetInfoAboutUserRequest(null)),
                Arguments.of(PLAYING_MOVE, new MovePlayerRequest(0, null)),
                Arguments.of(PLAYER_AUTH, new AuthUserRequest(null)),
                Arguments.of(PLAYER_LOGOUT, new LogoutPlayerRequest()),
                Arguments.of(CREATE_ROOM, new CreateRoomRequest()),
                Arguments.of(JOIN_ROOM, new JoinRoomRequest(0)),
                Arguments.of(GET_ROOMS, new GetRoomsRequest()),
                Arguments.of(GET_REPLAY_GAME, new GetReplayGameRequest(0))
        );
    }

    private static Stream<Arguments> getCommandByResponseStream() {
        return Stream.of(
                Arguments.of(ERROR, new ErrorResponse(null, null)),
                Arguments.of(MESSAGE, new MessageResponse(null)),
                Arguments.of(GAME_PLAYING, new GameBoardResponse(0, null, null)),
                Arguments.of(LOGOUT, new LogoutResponse()),
                Arguments.of(GET_INFO_USER_RESPONSE, new PlayerResponse(null, 0, 0, 0)),
                Arguments.of(GAME_START, new CreateGameResponse(0, null, null)),
                Arguments.of(GAME_REPLAY, new ReplayResponse(null, null, null, null, null)),
                Arguments.of(CREATE_PLAYER, new CreatePlayerResponse(0, null)),
                Arguments.of(ROOM, new RoomResponse(0, null, null)),
                Arguments.of(ROOMS, new ListRoomResponse(null))
        );
    }

    @ParameterizedTest
    @MethodSource("getCommandByRequestStream")
    void getCommandByRequest(final CommandRequest command, final GameRequest request) throws ServerException {
        assertEquals(command, JsonService.getCommandByRequest(request));
    }

    @Test
    void getCommandByRequestException() {
        try {
            JsonService.getCommandByRequest(null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_REQUEST, e.getErrorCode());
        }

        try {
            JsonService.getCommandByRequest(new GameRequest() {
            });
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.UNKNOWN_REQUEST, e.getErrorCode());
        }
    }

    @Test
    void testCountCommandAndRequest() {
        assertEquals(getCommandByRequestStream().count(), CommandRequest.values().length);
    }

    @ParameterizedTest
    @MethodSource("getCommandByResponseStream")
    void getCommandByResponse(final CommandResponse command, final GameResponse response) throws ServerException {
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
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_RESPONSE, e.getErrorCode());
        }

        try {
            JsonService.getCommandByResponse(new GameResponse() {
            });
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.UNKNOWN_RESPONSE, e.getErrorCode());
        }
    }


    @Test
    void fromMsgParserRequest() throws ServerException {
        final String json = JsonService.toJson(new CreateUserRequest("Player_Test"));
        final CreateUserRequest request = (CreateUserRequest) JsonService.getRequestFromMsg(CommandRequest.CREATE_PLAYER.getCommandName() + " " + json);
        assertEquals("Player_Test", request.getNickname());
    }

    @Test
    void fromMsgParserRequestException() {
        try {
            JsonService.getRequestFromMsg(null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_MESSAGE_DTO, e.getErrorCode());
        }

        try {
            JsonService.getRequestFromMsg("         ");
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_MESSAGE_DTO, e.getErrorCode());
        }

        try {
            JsonService.getRequestFromMsg("abrakadabra {0,0}");
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.UNKNOWN_REQUEST, e.getErrorCode());
        }
    }


    @Test
    void fromMsgParserResponse() throws ServerException {
        final String json = JsonService.toJson(new CreateGameResponse(1001, PlayerColor.BLACK, new PlayerResponse("test", 0, 0, 0)));
        final CreateGameResponse response = (CreateGameResponse) JsonService.getResponseFromMsg(CommandResponse.GAME_START.getCommandName() + " " + json);
        assertEquals(1001, response.getGameId());
        assertEquals(PlayerColor.BLACK, response.getColor());
        assertNotNull(response.getOpponent());
        assertEquals("test", response.getOpponent().getNickname());
    }

    @Test
    void fromMsgParserResponseException() {
        try {
            JsonService.getResponseFromMsg(null);
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_MESSAGE_DTO, e.getErrorCode());
        }

        try {
            JsonService.getResponseFromMsg("         ");
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_MESSAGE_DTO, e.getErrorCode());
        }

        try {
            JsonService.getResponseFromMsg("abrakadabra {0,0}");
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.UNKNOWN_RESPONSE, e.getErrorCode());
        }
    }

    @Test
    void toMsgParseRequest() throws ServerException {
        final CreateUserRequest request = new CreateUserRequest("Player_Test");
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
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_REQUEST, e.getErrorCode());
        }

        try {
            JsonService.toMsgParser(new GameRequest() {
            });
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.UNKNOWN_REQUEST, e.getErrorCode());
        }
    }

    @Test
    void toMsgParseResponse() throws ServerException {
        final CreateGameResponse response = new CreateGameResponse(1001, PlayerColor.BLACK, new PlayerResponse("test", 0, 0, 0));
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
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.INVALID_RESPONSE, e.getErrorCode());
        }

        try {
            JsonService.toMsgParser(new GameResponse() {
            });
            fail();
        } catch (final ServerException e) {
            assertEquals(GameErrorCode.UNKNOWN_RESPONSE, e.getErrorCode());
        }
    }
}