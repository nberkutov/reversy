package services;

import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.*;
import models.base.GameState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Slf4j
public class GameService extends BaseService {

    public static Game createGame(Player first, Player second) {
        int idGame = getGameId();
        Game game = new Game(idGame, first, second);
        games.putIfAbsent(idGame, game);
        return game;
    }

    public static void moveFromPlayer(Game game, Point point, Player player) throws GameException {
        checkGameEnd(game);
        checkValidCanPlayerMove(game, player);
        BoardService.makeMove(game, point, player.getColor());
        switch (game.getState()) {
            case BLACK:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getWhitePlayer())) {
                    game.setState(GameState.WHITE);
                }
                break;
            case WHITE:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getBlackPlayer())) {
                    game.setState(GameState.BLACK);
                }
                break;
        }
        if (GameService.isEndGame(game)) {
            game.setState(GameState.END);
        }
    }

    public static Game getGameById(int idGame) throws GameException {
        Game game = games.get(idGame);
        if (game == null) {
            throw new GameException(GameErrorCode.GAME_NOT_FOUND);
        }
        return game;
    }

    /**
     * Функция делает ход игры
     *
     * @param game - Игра
     */
    public static void playNext(final Game game) throws GameException {
        switch (game.getState()) {
            case BLACK:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getBlackPlayer())) {
                    game.getBlackPlayer().nextMove(game);
                }
                game.setState(GameState.WHITE);
                break;
            case WHITE:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getWhitePlayer())) {
                    game.getWhitePlayer().nextMove(game);
                }
                game.setState(GameState.BLACK);
                break;
            case END:
                break;
        }
        if (GameService.isEndGame(game)) {
            game.setState(GameState.END);
        }
    }

    /**
     * Функция просчитывает и вовзвращает закончена ли игра
     *
     * @param game - Игра
     * @return boolean
     */
    public static boolean isEndGame(final Game game) throws GameException {
        return BoardService.getCountEmpty(game.getBoard()) == 0 ||
                (!BoardService.hasPossibleMove(game.getBoard(), game.getBlackPlayer())
                        && !BoardService.hasPossibleMove(game.getBoard(), game.getWhitePlayer()));
    }

    /**
     * Функция вовзвращает результат об окончании игры
     *
     * @param game - Игра
     * @return boolean
     */
    public static GameResult getResultGame(final Game game) throws GameException {
        if (game.getState() != GameState.END) {
            throw new GameException(GameErrorCode.GAME_NOT_FINISHED);
        }
        Board board = game.getBoard();
        long blackCells = BoardService.getCountBlack(board);
        long whiteCells = BoardService.getCountWhite(board);
        if (blackCells == whiteCells) {
            return GameResult.draw(board);
        } else if (blackCells > whiteCells) {
            return GameResult.winner(board, game.getBlackPlayer());
        } else {
            return GameResult.winner(board, game.getWhitePlayer());
        }
    }

    private static void checkGameEnd(Game game) throws GameException {
        if (game.isFinished()) {
            throw new GameException(GameErrorCode.INVALID_REQUEST);
        }
    }

    private static void checkValidCanPlayerMove(Game game, Player player) throws GameException {
        if ((game.getState() == GameState.BLACK
                && game.getBlackPlayer().equals(player))
                ||
                (game.getState() == GameState.WHITE
                        && game.getWhitePlayer().equals(player))) {
            return;
        }
        throw new GameException(GameErrorCode.INVALID_REQUEST);
    }

}