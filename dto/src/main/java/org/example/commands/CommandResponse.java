package org.example.commands;

import lombok.Getter;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.GameResponse;
import org.example.dto.response.game.CreateGameResponse;
import org.example.dto.response.game.GameBoardResponse;
import org.example.dto.response.game.ReplayResponse;
import org.example.dto.response.player.CreatePlayerResponse;
import org.example.dto.response.player.LogoutResponse;
import org.example.dto.response.player.MessageResponse;
import org.example.dto.response.player.PlayerResponse;
import org.example.dto.response.room.CloseRoomResponse;
import org.example.dto.response.room.ListRoomResponse;
import org.example.dto.response.room.RoomResponse;

@Getter
public enum CommandResponse {
    ERROR("error", ErrorResponse.class),
    MESSAGE("message", MessageResponse.class),
    GAME_PLAYING("game_playing", GameBoardResponse.class),
    LOGOUT("logout", LogoutResponse.class),
    GET_INFO_USER_RESPONSE("get_info_response", PlayerResponse.class),
    GAME_REPLAY("game_replay", ReplayResponse.class),
    GAME_START("game_start", CreateGameResponse.class),
    CREATE_PLAYER("new_player", CreatePlayerResponse.class),
    ROOM("room", RoomResponse.class),
    CLOSE_ROOM_RESPONSE("close_room_response", CloseRoomResponse.class),
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
