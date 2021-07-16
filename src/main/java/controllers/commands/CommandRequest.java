package controllers.commands;

import dto.request.player.CreatePlayerRequest;
import dto.request.player.GetGameInfoRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.player.WantPlayRequest;
import dto.request.server.CreateGameRequest;
import lombok.Getter;

@Getter
public enum CommandRequest {
    CREATE_PLAYER("create_player", CreatePlayerRequest.class),
    WANT_PLAY("want_play", WantPlayRequest.class),
    PRIVATE_CREATE_GAME("create_private_game", CreateGameRequest.class),
    PLAYING_MOVE("playing_move", MovePlayerRequest.class),
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
