package dto.response;

import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import models.base.GameState;
import models.board.Board;
import models.game.Game;
import services.BoardEncoder;
import services.BoardService;

@Getter
@ToString
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
