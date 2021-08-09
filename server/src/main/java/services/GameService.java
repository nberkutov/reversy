package services;

import dto.request.player.GetReplayGameRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.server.CreateGameRequest;
import exception.GameErrorCode;
import exception.ServerException;
import logic.BoardLogic;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.base.Cell;
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

    public static Game createGameBySearch(final CreateGameRequest createGame,
                                          final ClientConnection connection) throws ServerException {
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

    public static Game getReplayGame(final GetReplayGameRequest request,
                                     final ClientConnection connection) throws ServerException {
        checkRequestAndConnection(request, connection);
        Game game = getGameById(request.getGameId());
        gameIsNotNull(game);
        gameIsEnd(game);
        return game;
    }

    public static Game createGame(final User black, final User white) throws ServerException {
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

    public static Game createGameByRoom(Room room) throws ServerException {
        roomIsNotNull(room);
        User black = room.getBlackUser();
        User white = room.getWhiteUser();
        return createGame(black, white);
    }

    public static Game makePlayerMove(final MovePlayerRequest movePlayer, final ClientConnection connection) throws ServerException {
        checkRequestAndConnection(movePlayer, connection);
        User user = connection.getUser();
        Game game = getGameById(movePlayer.getGameId());
        return makePlayerMove(game, movePlayer.getPoint(), user);
    }

    public static Game makePlayerMove(final Game game, final Point point, final User user) throws ServerException {
        gameIsNotNull(game);
        game.lock();
        gameIsNotEnd(game);
        playerIsNotNull(user);
        playerValidMove(game, user);

        BoardLogic.makeMove(game.getBoard(), point, Cell.valueOf(user.getColor()));
        game.addMove(user.getColor(), point);
        choosingPlayerMove(game);

        if (gameIsFinished(game)) {
            GameResult result = getGameResult(game);
            finishGame(result, game);
        }

        return game;
    }

    public static void finishGame(final GameResult result, final Game game) throws ServerException {
        gameIsNotNull(game);
        gameResultIsNotNull(result);
        log.info("GameEnd {} {}", game.getId(), result);
        game.setResult(result);
        calculateStatistic(result);
        PlayerService.setPlayerStateNone(game.getBlackUser());
        PlayerService.setPlayerStateNone(game.getWhiteUser());
    }

    private static void calculateStatistic(final GameResult gameResult) {
        User winner = gameResult.getWinner();
        User loser = gameResult.getLoser();

        winner.getStatistics().incrementWin();
        winner.getStatistics().incrementCountGames();
        winner.getStatistics().incrementPlayerAgainst(loser);

        loser.getStatistics().incrementLose();
        loser.getStatistics().incrementCountGames();

        if (winner.getColor() == PlayerColor.BLACK) {
            winner.getStatistics().incrementPlayBlack();
            loser.getStatistics().incrementPlayWhite();
        } else {
            winner.getStatistics().incrementPlayWhite();
            loser.getStatistics().incrementPlayBlack();
        }
    }

    private static void choosingPlayerMove(final Game game) throws ServerException {
        boolean blackCanMove = BoardLogic.canMove(game.getBoard(), game.getBlackUser().getColor());
        boolean whiteCanMove = BoardLogic.canMove(game.getBoard(), game.getWhiteUser().getColor());

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
        long blackCells = BoardLogic.getCountBlack(board);
        long whiteCells = BoardLogic.getCountWhite(board);
        if (blackCells <= whiteCells) {
            return GameResult.winner(board, game.getWhiteUser(), game.getBlackUser());
        } else {
            return GameResult.winner(board, game.getBlackUser(), game.getWhiteUser());
        }
    }

    private static void gameIsNotEnd(final Game game) throws ServerException {
        if (gameIsFinished(game)) {
            throw new ServerException(GameErrorCode.GAME_ENDED);
        }
    }

    private static void gameIsEnd(final Game game) throws ServerException {
        if (!gameIsFinished(game)) {
            throw new ServerException(GameErrorCode.GAME_NOT_FINISHED);
        }
    }

    private static void playerValidMove(final Game game, final User user) throws ServerException {
        if (!whatPlayerMoveNow(game).equals(user)) {
            throw new ServerException(GameErrorCode.ILLEGAL_REQUEST);
        }
    }

    private static boolean gameIsFinished(final Game game) {
        return game.getState() == GameState.END;
    }

    public static User whatPlayerMoveNow(final Game game) throws ServerException {
        if (game.getState() == GameState.BLACK_MOVE) {
            return game.getBlackUser();
        }
        if (game.getState() == GameState.WHITE_MOVE) {
            return game.getWhiteUser();
        }
        throw new ServerException(GameErrorCode.GAME_ENDED);
    }

}