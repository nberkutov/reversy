package commands;

import dto.response.ErrorResponse;
import dto.response.GameResponse;
import dto.response.game.GameBoardResponse;
import dto.response.game.ReplayResponse;
import dto.response.player.CreateGameResponse;
import dto.response.player.CreatePlayerResponse;
import dto.response.player.MessageResponse;
import dto.response.room.ListRoomResponse;
import dto.response.room.RoomResponse;
import lombok.Getter;

@Getter
public enum CommandResponse {
    ERROR("error", ErrorResponse.class),
    MESSAGE("message", MessageResponse.class),
    GAME_PLAYING("game_playing", GameBoardResponse.class),
    GAME_REPLAY("game_replay", ReplayResponse.class),
    GAME_START("game_start", CreateGameResponse.class),
    CREATE_PLAYER("new_player", CreatePlayerResponse.class),
    ROOM("room", RoomResponse.class),
    ROOMS("rooms", ListRoomResponse.class);

    private final String commandName;
    private final Class<? extends GameResponse> response;

    CommandResponse(final String commandName, final Class<? extends GameResponse> response) {
        this.commandName = commandName;
        this.response = response;
    }

    public boolean equalCommand(final String message) {
        return commandName.equals(message);
    }

}
