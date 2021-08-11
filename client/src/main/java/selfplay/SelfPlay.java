package selfplay;

import exception.GameErrorCode;
import exception.ServerException;
import logic.BoardLogic;
import lombok.extern.slf4j.Slf4j;
import models.Player;
import models.base.Cell;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Board;
import models.board.Point;


@Slf4j
public class SelfPlay {
    private final Player first;
    private final Player second;
    private final SelfGame selfGame;

    public SelfPlay(final Player first, final Player second) {
        this.first = first;
        this.second = second;
        final GameBoard board = new Board();

        first.setColor(PlayerColor.BLACK);
        second.setColor(PlayerColor.WHITE);
        selfGame = new SelfGame(board, first, second);
    }

    /**
     * Функция делает ход игры
     *
     * @param selfGame - Игра
     */
    private static void playNext(final SelfGame selfGame, Player first, Player second) throws ServerException {
        switch (selfGame.getState()) {
            case BLACK_MOVE:
                if (!BoardLogic.getAvailableMoves(selfGame.getBoard(), selfGame.getBlackPlayer().getColor()).isEmpty()) {
                    final Point move = first.move(selfGame.getBoard());
                    BoardLogic.makeMove(selfGame.getBoard(), move, Cell.BLACK);
                }
                selfGame.setState(GameState.WHITE_MOVE);
                break;
            case WHITE_MOVE:
                if (!BoardLogic.getAvailableMoves(selfGame.getBoard(), selfGame.getWhitePlayer().getColor()).isEmpty()) {
                    final Point move = second.move(selfGame.getBoard());
                    BoardLogic.makeMove(selfGame.getBoard(), move, Cell.WHITE);
                }
                selfGame.setState(GameState.BLACK_MOVE);
                break;
        }
        if (isGameEnd(selfGame)) {
            selfGame.setState(GameState.END);
        }
    }

    /**
     * Функция просчитывает и вовзвращает закончена ли игра
     *
     * @param selfGame - Игра
     * @return boolean
     */
    private static boolean isGameEnd(final SelfGame selfGame) throws ServerException {
        if (BoardLogic.getCountEmpty(selfGame.getBoard()) == 0) {
            return true;
        }
        return !BoardLogic.canMove(selfGame.getBoard(), selfGame.getBlackPlayer().getColor())
                && !BoardLogic.canMove(selfGame.getBoard(), selfGame.getWhitePlayer().getColor());
    }

    private static PlayerColor getGameResult(final SelfGame selfGame) throws ServerException {
        if (selfGame.getState() != GameState.END) {
            throw new ServerException(GameErrorCode.GAME_NOT_FINISHED);
        }
        GameBoard board = selfGame.getBoard();
        final int blackCells = BoardLogic.getCountBlack(board);
        final int whiteCells = BoardLogic.getCountWhite(board);
        if (blackCells <= whiteCells) {
            return PlayerColor.WHITE;
        } else {
            return PlayerColor.BLACK;
        }
    }

    public PlayerColor play() throws ServerException {
        while (selfGame.getState() != GameState.END) {
            playNext(selfGame, first, second);
        }
        return getGameResult(selfGame);
    }
}