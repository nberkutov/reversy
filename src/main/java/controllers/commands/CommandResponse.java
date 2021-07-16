package controllers.commands;

import dto.response.ErrorResponse;
import dto.response.GameBoardResponse;
import dto.response.MessageResponse;
import dto.response.SearchGameResponse;
import dto.response.player.CreatePlayerResponse;
import lombok.Getter;

@Getter
public enum CommandResponse {
    ERROR("error", ErrorResponse.class),
    MESSAGE("message", MessageResponse.class),
    GAME_PLAYING("game_playing", GameBoardResponse.class),
    GAME_START("game_start", SearchGameResponse.class),
    CREATE_PLAYER("new_player", CreatePlayerResponse.class);

    private final String commandName;
    private final Class response;

    CommandResponse(final String commandName, Class response) {
        this.commandName = commandName;
        this.response = response;
    }

    public boolean equalCommand(final String message) {
        return commandName.equals(message);
    }

}
