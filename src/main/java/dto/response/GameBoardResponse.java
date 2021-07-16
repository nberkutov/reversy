package dto.response;

import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.base.GameState;
import models.board.Board;
import models.game.Game;
import services.BoardEncoder;
import services.BoardService;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GameBoardResponse extends GameResponse {
    private int gameId;
    private GameState state;
    private GameBoardDto boardDto;

    public static GameBoardResponse toDto(final Game game) throws GameException {
        GameBoardDto dto = new GameBoardDto(BoardEncoder.toString(game.getBoard()));
        return new GameBoardResponse(game.getId(), game.getState(), dto);
    }
}
