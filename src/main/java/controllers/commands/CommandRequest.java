package controllers.commands;

import dto.request.player.*;
import dto.request.room.CreateRoomRequest;
import dto.request.room.GetRoomsRequest;
import dto.request.room.JoinRoomRequest;
import dto.request.server.CreateGameRequest;
import lombok.Getter;

@Getter
public enum CommandRequest {
    CREATE_PLAYER("create_player", CreatePlayerRequest.class),
    WANT_PLAY("want_play", WantPlayRequest.class),
    SEARCH_CREATE_GAME("create_private_game", CreateGameRequest.class),
    PLAYING_MOVE("playing_move", MovePlayerRequest.class),
    PLAYER_AUTH("player_auth", AuthPlayerRequest.class),
    PLAYER_LOGOUT("player_logout", LogoutPlayerRequest.class),
    CREATE_ROOM("create_room", CreateRoomRequest.class),
    JOIN_ROOM("join_room", JoinRoomRequest.class),
    GET_ROOMS("get_rooms", GetRoomsRequest.class),
    GET_GAME_INFO("get_game_info", GetGameInfoRequest.class);

    private final String commandName;
    private final Class request;

    CommandRequest(final String commandName, final Class request) {
        this.commandName = commandName;
        this.request = request;
    }

    public boolean equalCommand(final String message) {
        return commandName.equals(message);
    }
}
