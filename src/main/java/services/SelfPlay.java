package services;

import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.base.Cell;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Board;
import models.board.Point;
import models.game.Game;
import models.game.GameResult;
import models.player.Player;

@Slf4j
public class SelfPlay {
    private final Player first;
    private final Player second;
    private final Game game;

    public SelfPlay(final Player first, final Player second) {
        this.first = first;
        this.second = second;
        GameBoard board = new Board();
        game = new Game(board, first, second);
    }

    /**
     * Функция делает ход игры
     *
     * @param game - Игра
     */
    private static void playNext(final Game game) throws GameException {
        switch (game.getState()) {
            case BLACK_MOVE:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getBlackPlayer())) {
                    Point move = game.getBlackPlayer().move(game.getBoard());
                    BoardService.makeMove(game.getBoard(), move, Cell.BLACK);
                }
                game.setState(GameState.WHITE_MOVE);
                break;
            case WHITE_MOVE:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getWhitePlayer())) {
                    Point move = game.getWhitePlayer().move(game.getBoard());
                    BoardService.makeMove(game.getBoard(), move, Cell.WHITE);
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
    private static boolean isGameEnd(final Game game) throws GameException {
        if (BoardService.getCountEmpty(game.getBoard()) == 0) {
            return true;
        }
        return !BoardService.hasPossibleMove(game.getBoard(), game.getBlackPlayer())
                && !BoardService.hasPossibleMove(game.getBoard(), game.getWhitePlayer());
    }

    public GameResult play() throws GameException {
        while (game.getState() != GameState.END) {
            playNext(game);
        }

        log.debug("DEBUG finish \n{}", game.getResult().getBoard());
        return getGameResult(game);
    }

    private GameResult getGameResult(final Game game) throws GameException {
        if (game.getState() != GameState.END) {
            throw new GameException(GameErrorCode.GAME_NOT_FINISHED);
        }
        GameBoard board = game.getBoard();
        long blackCells = BoardService.getCountBlack(board);
        long whiteCells = BoardService.getCountWhite(board);
        if (blackCells <= whiteCells) {
            return GameResult.winner(board, game.getWhitePlayer());
        } else {
            return GameResult.winner(board, game.getBlackPlayer());
        }
    }
}
