package dto.response.player;

import dto.response.GameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.game.Game;
import models.player.User;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GameBoardResponse extends GameResponse {
    private int gameId;
    private GameState state;
    private GameBoard board;
    private PlayerResponse opponent;

    public static GameBoardResponse toDto(final Game game, final User to) {
        if (to.equals(game.getWhiteUser())) {
            return new GameBoardResponse(game.getId(),
                    game.getState(),
                    game.getBoard(),
                    PlayerResponse.toDto(game.getBlackUser()));
        }

        return new GameBoardResponse(game.getId(),
                game.getState(),
                game.getBoard(),
                PlayerResponse.toDto(game.getWhiteUser()));
    }
}
