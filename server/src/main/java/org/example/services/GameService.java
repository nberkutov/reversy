package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.player.GetReplayGameRequest;
import org.example.dto.request.player.MovePlayerRequest;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.logic.BoardFactory;
import org.example.logic.BoardLogic;
import org.example.models.base.Cell;
import org.example.models.base.GameState;
import org.example.models.base.PlayerColor;
import org.example.models.base.PlayerState;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.game.Game;
import org.example.models.game.GameResult;
import org.example.models.game.Room;
import org.example.models.player.User;
import org.example.models.player.UserConnection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Service
@Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
public class GameService extends DataBaseService {

    public Game createGameBySearch(final UserConnection firstC,
                                   final UserConnection secondC) throws ServerException {
        connectionIsNotNullAndConnected(firstC);
        connectionIsNotNullAndConnected(secondC);
        final User first = dbd.getUserById(firstC.getUserId());
        final User second = dbd.getUserById(secondC.getUserId());
        userIsNotNull(first);
        userIsNotNull(second);
        if (new Random().nextBoolean()) {
            return createGame(first, second);
        }
        return createGame(second, first);
    }

    public Game getReplayGame(final GetReplayGameRequest request,
                              final UserConnection connection) throws ServerException {
        checkRequestAndConnection(request, connection);
        final Game game = dbd.getGameById(request.getGameId());
        gameIsNotNull(game);
        gameIsEnd(game);
        return game;
    }

    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Game createGame(final User black, final User white) throws ServerException {
        userIsNotNull(black);
        userIsNotNull(white);
        final GameBoard board = BoardFactory.generateStartedBoard();
        final Game game = new Game(board, black, white);
        black.setState(PlayerState.PLAYING);
        black.setNowPlaying(game);
        white.setState(PlayerState.PLAYING);
        white.setNowPlaying(game);
        dbd.saveGame(game);
        return game;
    }

    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Game createGameByRoom(final Room room) throws ServerException {
        roomIsNotNull(room);
        final User black = room.getBlackUser();
        final User white = room.getWhiteUser();
        black.setNowRoom(null);
        white.setNowRoom(null);
        return createGame(black, white);
    }

    public Game makePlayerMove(final MovePlayerRequest movePlayer, final UserConnection connection) throws ServerException {
        checkRequestAndConnection(movePlayer, connection);
        final User user = dbd.getUserById(connection.getUserId());
        final Game game = dbd.getGameById(movePlayer.getGameId());
        return makePlayerMove(game, movePlayer.getPoint(), user);
    }

    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public Game makePlayerMove(final Game game, final Point point, final User user) throws ServerException {
        gameIsNotNull(game);
        gameIsNotEnd(game);
        userIsNotNull(user);
        playerValidMove(game, user);
        BoardLogic.makeMove(game.getBoard(), point, Cell.valueOf(user.getColor()));
        game.addMove(user.getColor(), point);
        choosingPlayerMove(game);

        if (gameIsFinished(game)) {
            final GameResult result = getGameResult(game);
            finishGame(result, game);
        }
        dbd.saveGame(game);
        return game;
    }

    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public void finishGame(final GameResult result, final Game game) throws ServerException {
        gameIsNotNull(game);
        gameResultIsNotNull(result);
        game.setResult(result);
        calculateStatistic(result);
        ps.setPlayerStateNone(game.getBlackUser());
        ps.setPlayerStateNone(game.getWhiteUser());
    }

    private void calculateStatistic(final GameResult gameResult) {
        final User winner = gameResult.getWinner();
        final User loser = gameResult.getLoser();

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

    private void choosingPlayerMove(final Game game) throws ServerException {
        final boolean blackCanMove = BoardLogic.canMove(game.getBoard(), game.getBlackUser().getColor());
        final boolean whiteCanMove = BoardLogic.canMove(game.getBoard(), game.getWhiteUser().getColor());

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
    @Transactional(rollbackFor = ServerException.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public GameResult getGameResult(final Game game) {
        final GameBoard board = game.getBoard();
        final long blackCells = BoardLogic.getCountBlack(board);
        final long whiteCells = BoardLogic.getCountWhite(board);
        if (blackCells <= whiteCells) {
            return GameResult.winner(game.getWhiteUser(), game.getBlackUser());
        } else {
            return GameResult.winner(game.getBlackUser(), game.getWhiteUser());
        }
    }

    private void gameIsNotEnd(final Game game) throws ServerException {
        if (gameIsFinished(game)) {
            throw new ServerException(GameErrorCode.GAME_ENDED);
        }
    }

    private void gameIsEnd(final Game game) throws ServerException {
        if (!gameIsFinished(game)) {
            throw new ServerException(GameErrorCode.GAME_NOT_FINISHED);
        }
    }

    private void playerValidMove(final Game game, final User user) throws ServerException {
        if (!whatPlayerMoveNow(game).equals(user)) {
            throw new ServerException(GameErrorCode.ILLEGAL_REQUEST);
        }
    }

    private boolean gameIsFinished(final Game game) {
        return game.getState() == GameState.END;
    }

    private User whatPlayerMoveNow(final Game game) throws ServerException {
        if (game.getState() == GameState.BLACK_MOVE) {
            return game.getBlackUser();
        }
        if (game.getState() == GameState.WHITE_MOVE) {
            return game.getWhiteUser();
        }
        throw new ServerException(GameErrorCode.GAME_ENDED);
    }

}
