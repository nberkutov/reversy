package org.example.commands;

import lombok.Getter;
import org.example.dto.request.GameRequest;
import org.example.dto.request.player.*;
import org.example.dto.request.room.CloseRoomRequest;
import org.example.dto.request.room.CreateRoomRequest;
import org.example.dto.request.room.GetRoomsRequest;
import org.example.dto.request.room.JoinRoomRequest;

@Getter
public enum CommandRequest {
    CREATE_PLAYER("create_player", CreateUserRequest.class),
    WANT_PLAY("want_play", WantPlayRequest.class),
    PLAYING_MOVE("playing_move", MovePlayerRequest.class),
    GET_INFO_USER("get_info", GetInfoAboutUserRequest.class),
    PLAYER_AUTH("player_auth", AuthUserRequest.class),
    PLAYER_LOGOUT("player_logout", LogoutPlayerRequest.class),
    CREATE_ROOM("create_room", CreateRoomRequest.class),
    JOIN_ROOM("join_room", JoinRoomRequest.class),
    CLOSE_ROOM_REQUEST("close_room_request", CloseRoomRequest.class),
    GET_ROOMS("get_rooms", GetRoomsRequest.class),
    GET_REPLAY_GAME("get_replay_game", GetReplayGameRequest.class);

    private final String commandName;
    private final Class<? extends GameRequest> request;

    CommandRequest(final String commandName, final Class<? extends GameRequest> request) {
        this.commandName = commandName;
        this.request = request;
    }

    public boolean equalCommand(final String message) {
        return commandName.equals(message);
    }
}
