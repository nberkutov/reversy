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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static services.BoardService.isPossibleMove;

@Slf4j
public class GameService extends BaseService {


    // private final BoardService service = new BoardService();

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

    public static void doGame(Game game) throws GameException {
        switch (game.getState()) {
            case BLACK:
                if (BoardService.isPossibleMove(game.getBlack())) {
                    game.getBlack().nextMove();
                }
                game.setState(GameState.WHITE);
                break;
            case WHITE:
                if (BoardService.isPossibleMove(game.getWhite())) {
                    game.getWhite().nextMove();
                }
                game.setState(GameState.BLACK);
                break;
            case END:
                break;
        }
        if (GameService.isEndGame(board, game)) {
            game.setState(GameState.END);
        }
    }

    public static boolean isEndGame(Game game) throws GameException {
        return BoardService.getCountEmpty(game.getBoard()) == 0 ||
                (!BoardService.isPossibleMove(game.getBlack())
                        && !BoardService.isPossibleMove(game.getWhite()));
    }

    public static GameResult getResultGame(Game game, Board board) throws GameException {
        if (game.getState() != GameState.END) {
            throw new GameException(GameErrorCode.GAME_NOT_FINISHED);
        }
        long blackCells = board.getCountBlack();
        long whiteCells = board.getCountWhite();
        if (blackCells == whiteCells) {
            return GameResult.draw(board);
        } else if (blackCells > whiteCells) {
            return GameResult.winner(board, game.getBlack());
        } else {
            return GameResult.winner(board, game.getWhite());
        }
    }


}