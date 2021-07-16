package dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import models.base.GameState;
import models.board.Board;
import models.game.Game;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GameBoardResponse extends GameResponse {
    private int gameId;
    private GameState state;
    private Board board;

    public static GameBoardResponse toDto(final Game game) {
        return new GameBoardResponse(game.getId(), game.getState(), game.getBoard());
    }
}
