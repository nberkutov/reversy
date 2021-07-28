package services;

import client.models.Player;
import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.base.Cell;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Board;
import models.board.Point;
import models.game.Game;
import models.game.GameResult;
import models.player.User;

@Slf4j
public class SelfPlay {
    private final Player first;
    private final Player second;
    private final Game game;

    public SelfPlay(final Player first, final Player second) {
        this.first = first;
        this.second = second;
        GameBoard board = new Board();
        User black = new User(0, first.getNickname());
        first.setColor(PlayerColor.BLACK);
        User white = new User(1, second.getNickname());
        second.setColor(PlayerColor.WHITE);
        game = new Game(board, black, white);
    }

    /**
     * Функция делает ход игры
     *
     * @param game - Игра
     */
    private static void playNext(final Game game, Player first, Player second) throws GameException {
        switch (game.getState()) {
            case BLACK_MOVE:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getBlackUser())) {
                    Point move = first.move(game.getBoard());
                    BoardService.makeMove(game.getBoard(), move, Cell.BLACK);
                    game.addMove(first.getColor(), move);
                }
                game.setState(GameState.WHITE_MOVE);
                break;
            case WHITE_MOVE:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getWhiteUser())) {
                    Point move = second.move(game.getBoard());
                    BoardService.makeMove(game.getBoard(), move, Cell.WHITE);
                    game.addMove(second.getColor(), move);
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
        return !BoardService.hasPossibleMove(game.getBoard(), game.getBlackUser())
                && !BoardService.hasPossibleMove(game.getBoard(), game.getWhiteUser());
    }

    public GameResult play() throws GameException {
        while (game.getState() != GameState.END) {
            playNext(game, first, second);
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
            return GameResult.winner(board, game.getWhiteUser(), game.getBlackUser());
        } else {
            return GameResult.winner(board, game.getBlackUser(), game.getWhiteUser());
        }
    }
}
