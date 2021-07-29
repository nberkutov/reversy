package services;

import dto.request.player.GetGameInfoRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.server.CreateGameRequest;
import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.base.GameState;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.game.Game;
import models.game.GameResult;
import models.game.Room;
import models.player.User;

import java.util.Random;

@Slf4j
public class GameService extends DataBaseService {

    public static Game createGameBySearch(final CreateGameRequest createGame, final ClientConnection connection) throws GameException {
        checkRequestAndConnection(createGame, connection);
        ClientConnection firstCon = getConnectionById(createGame.getFirstPlayerId());
        connectionIsNotNullAndConnected(firstCon);
        ClientConnection secondCon = getConnectionById(createGame.getSecondPlayerId());
        connectionIsNotNullAndConnected(secondCon);
        User first = firstCon.getUser();
        User second = secondCon.getUser();
        if (new Random().nextBoolean()) {
            return createGame(first, second);
        }
        return createGame(second, first);
    }

    public static Game getGameInfo(final GetGameInfoRequest getGame, final ClientConnection connection) throws GameException {
        checkRequestAndConnection(getGame, connection);
        Game game = getGameById(getGame.getGameId());
        gameIsNotNull(game);
        return game;
    }

    public static Game createGame(final User black, final User white) throws GameException {
        playerIsNotNull(black);
        playerIsNotNull(white);
        black.lock();
        white.lock();
        Game game = putGame(black, white);
        black.setState(PlayerState.PLAYING);
        black.setNowPlaying(game);
        white.setState(PlayerState.PLAYING);
        white.setNowPlaying(game);
        white.unlock();
        black.unlock();
        return game;
    }

    public static Game createGameByRoom(Room room) throws GameException {
        roomIsNotNull(room);
        User black = room.getBlackUser();
        User white = room.getWhiteUser();
        return createGame(black, white);
    }

    public static Game makePlayerMove(final MovePlayerRequest movePlayer, final ClientConnection connection) throws GameException {
        checkRequestAndConnection(movePlayer, connection);
        User user = connection.getUser();
        Game game = getGameById(movePlayer.getGameId());
        return makePlayerMove(game, movePlayer.getPoint(), user);
    }

    public static Game makePlayerMove(final Game game, final Point point, final User user) throws GameException {
        gameIsNotNull(game);
        game.lock();
        gameIsNotEnd(game);
        playerIsNotNull(user);
        playerValidMove(game, user);

        BoardService.makeMove(game, point, user.getColor());
        choosingPlayerMove(game);

        if (gameIsFinished(game)) {
            GameResult result = getGameResult(game);
            finishGame(result, game);
        }

        return game;
    }

    public static void finishGame(final GameResult result, final Game game) throws GameException {
        gameIsNotNull(game);
        gameResultIsNotNull(result);
        log.info("GameEnd {} {}", game.getId(), result);
        game.setResult(result);
        calculateStatistic(result, game);
        PlayerService.setPlayerStateNone(game.getBlackUser());
        PlayerService.setPlayerStateNone(game.getWhiteUser());
    }

    private static void calculateStatistic(final GameResult gameResult, final Game game) {
        User winner = gameResult.getWinner();
        User loser = gameResult.getLoser();

        winner.getStatistics().incrementWin();
        winner.getStatistics().incrementCountGames();
        winner.getStatistics().incrementPlayerAgainst(loser);
        if (winner.getColor() == PlayerColor.BLACK) {
            winner.getStatistics().incrementPlayBlack();
        } else {
            winner.getStatistics().incrementPlayWhite();
        }
        loser.getStatistics().incrementLose();
        loser.getStatistics().incrementCountGames();

        if (loser.getColor() == PlayerColor.BLACK) {
            loser.getStatistics().incrementPlayBlack();
        } else {
            loser.getStatistics().incrementPlayWhite();
        }
    }

    private static void choosingPlayerMove(final Game game) throws GameException {
        boolean blackCanMove = BoardService.hasPossibleMove(game.getBoard(), game.getBlackUser());
        boolean whiteCanMove = BoardService.hasPossibleMove(game.getBoard(), game.getWhiteUser());

        if (game.getState() == GameState.BLACK_MOVE && whiteCanMove) {
            game.setState(GameState.WHITE_MOVE);
        } else if (game.getState() == GameState.WHITE_MOVE && blackCanMove) {
            game.setState(GameState.BLACK_MOVE);
        } else if (game.getState() == GameState.BLACK_MOVE && blackCanMove) {
            game.setState(GameState.BLACK_MOVE);
        } else if (game.getState() == GameState.WHITE_MOVE && whiteCanMove) {
            game.setState(GameState.WHITE_MOVE);
        } else {
            game.setState(GameState.END);
        }
    }

    /**
     * Функция вовзвращает результат об окончании игры
     *
     * @param game - Игра
     * @return GameResult
     */
    public static GameResult getGameResult(final Game game) {
        GameBoard board = game.getBoard();
        long blackCells = BoardService.getCountBlack(board);
        long whiteCells = BoardService.getCountWhite(board);
        if (blackCells <= whiteCells) {
            return GameResult.winner(board, game.getWhiteUser(), game.getBlackUser());
        } else {
            return GameResult.winner(board, game.getBlackUser(), game.getWhiteUser());
        }
    }

    private static void gameIsNotEnd(final Game game) throws GameException {
        if (gameIsFinished(game) || game.getBoard().getCountEmpty() == 0) {
            throw new GameException(GameErrorCode.GAME_ENDED);
        }
    }

    private static void playerValidMove(final Game game, final User user) throws GameException {
        if (!whatPlayerMoveNow(game).equals(user)) {
            throw new GameException(GameErrorCode.ILLEGAL_REQUEST);
        }
    }

    private static boolean gameIsFinished(final Game game) {
        return game.getState() == GameState.END;
    }

    public static User whatPlayerMoveNow(final Game game) throws GameException {
        if (game.getState() == GameState.BLACK_MOVE) {
            return game.getBlackUser();
        }
        if (game.getState() == GameState.WHITE_MOVE) {
            return game.getWhiteUser();
        }
        throw new GameException(GameErrorCode.GAME_ENDED);
    }

}