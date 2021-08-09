import exception.GameErrorCode;
import exception.ServerException;
import logic.BoardLogic;
import lombok.extern.slf4j.Slf4j;
import models.Player;
import models.base.Cell;
import models.base.Game;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Board;
import models.board.Point;


@Slf4j
public class SelfPlay {
    private final Player first;
    private final Player second;
    private final Game game;

    public SelfPlay(final Player first, final Player second) {
        this.first = first;
        this.second = second;
        GameBoard board = new Board();

        first.setColor(PlayerColor.BLACK);
        second.setColor(PlayerColor.WHITE);
        game = new Game(board, first, second);
    }

    /**
     * Функция делает ход игры
     *
     * @param game - Игра
     */
    private static void playNext(final Game game, Player first, Player second) throws ServerException {
        switch (game.getState()) {
            case BLACK_MOVE:
                if (!BoardLogic.getAvailableMoves(game.getBoard(), game.getBlackUser().getColor()).isEmpty()) {
                    Point move = first.move(game.getBoard());
                    BoardLogic.makeMove(game.getBoard(), move, Cell.BLACK);
                }
                game.setState(GameState.WHITE_MOVE);
                break;
            case WHITE_MOVE:
                if (!BoardLogic.getAvailableMoves(game.getBoard(), game.getWhiteUser().getColor()).isEmpty()) {
                    Point move = second.move(game.getBoard());
                    BoardLogic.makeMove(game.getBoard(), move, Cell.WHITE);
                }
                game.setState(GameState.BLACK_MOVE);
                break;
        }
        if (isGameEnd(game)) {
            game.setState(GameState.END);
        }
    }

    /**
     * Функция просчитывает и вовзвращает закончена ли игра
     *
     * @param game - Игра
     * @return boolean
     */
    private static boolean isGameEnd(final Game game) throws ServerException {
        if (BoardLogic.getCountEmpty(game.getBoard()) == 0) {
            return true;
        }
        return !BoardLogic.canMove(game.getBoard(), game.getBlackUser().getColor())
                && !BoardLogic.canMove(game.getBoard(), game.getWhiteUser().getColor());
    }

    private static PlayerColor getGameResult(final Game game) throws ServerException {
        if (game.getState() != GameState.END) {
            throw new ServerException(GameErrorCode.GAME_NOT_FINISHED);
        }
        GameBoard board = game.getBoard();
        long blackCells = BoardLogic.getCountBlack(board);
        long whiteCells = BoardLogic.getCountWhite(board);
        if (blackCells <= whiteCells) {
            return PlayerColor.WHITE;
        } else {
            return PlayerColor.BLACK;
        }
    }

    public PlayerColor play() throws ServerException {
        while (game.getState() != GameState.END) {
            playNext(game, first, second);
        }
        return getGameResult(game);
    }
}
