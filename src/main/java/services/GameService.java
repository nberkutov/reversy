package services;

import dto.request.player.MovePlayerRequest;
import dto.response.GameResponse;
import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.Board;
import models.Game;
import models.GameResult;
import models.Player;
import models.base.GameState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Slf4j
public class GameService extends BaseService {

    public static Game createGame() {
        throw new NotImplementedException();
    }

    public static GameResponse moveFromPlayer(MovePlayerRequest movePlayerRequest) {
        try {
            Game game = getGameById(movePlayerRequest.getIdGame());
            Player player = getPlayerById(movePlayerRequest.getIdPlayer());

            throw new GameException(GameErrorCode.GAME_NOT_FINISHED);
        } catch (GameException exception) {
            log.error("Error", exception);
            return new GameResponse();
        }
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
     * Функция вовзвращает закончена ли игра
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


}