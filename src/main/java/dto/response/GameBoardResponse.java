package dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import models.board.Board;
import models.game.Game;
import models.base.GameState;

@NoArgsConstructor
@AllArgsConstructor
public class GameBoardResponse extends GameResponse {
    private GameState state;
    private Board board;

    public static GameBoardResponse toDto(final Game game) {
        return new GameBoardResponse(game.getState(), game.getBoard());
    }
}
