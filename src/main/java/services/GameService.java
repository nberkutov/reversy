package services;

import dto.request.player.GetGameInfoRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.server.CreateGameRequest;
import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.base.GameState;
import models.base.PlayerState;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.game.Game;
import models.game.GameResult;
import models.player.Player;

@Slf4j
public class GameService extends DataBaseService {

    public static Game createGame(final CreateGameRequest createGame, final ClientConnection connection) throws GameException {
        requestIsNotNull(createGame);
        connectionIsNotNullAndConnected(connection);
        ClientConnection firstCon = getConnectionById(createGame.getFirstPlayerId());
        connectionIsNotNullAndConnected(firstCon);
        ClientConnection secondCon = getConnectionById(createGame.getSecondPlayerId());
        connectionIsNotNullAndConnected(secondCon);
        Player first = firstCon.getPlayer();
        Player second = secondCon.getPlayer();
        return createGame(first, second);
    }

    public static Game getGameInfo(final GetGameInfoRequest getGame, final ClientConnection connection) throws GameException {
        requestIsNotNull(getGame);
        connectionIsNotNullAndConnected(connection);
        Game game = getGameById(getGame.getGameId());
        gameIsNotNull(game);
        return game;
    }

    public static Game createGame(final Player first, final Player second) throws GameException {
        playerIsNotNull(first);
        playerIsNotNull(second);
        playerIsNotPlaying(first);
        playerIsNotPlaying(second);
        int gameId = getGameId();
        Game game = new Game(gameId, first, second);
        putGame(gameId, game);
        first.setState(PlayerState.PLAYING);
        second.setState(PlayerState.PLAYING);
        return game;
    }

    public static Game makePlayerMove(final MovePlayerRequest movePlayer, final ClientConnection connection) throws GameException {
        requestIsNotNull(movePlayer);
        connectionIsNotNullAndConnected(connection);
        Player player = connection.getPlayer();
        Game game = getGameById(movePlayer.getGameId());
        return makePlayerMove(game, movePlayer.getPoint(), player);
    }

    public static Game makePlayerMove(final Game game, final Point point, final Player player) throws GameException {
        gameIsNotNull(game);
        try {
            game.lock();
            gameIsNotEnd(game);
            playerIsNotNull(player);
            playerValidMove(game, player);

            BoardService.makeMove(game, point, player.getColor());
            choosingPlayerMove(game);

            if (gameIsFinished(game)) {
                log.info("GameEnd {} \n{}", game, game.getBoard());
                game.setState(GameState.END);
                game.getBlackPlayer().setState(PlayerState.NONE);
                game.getWhitePlayer().setState(PlayerState.NONE);
                calculateStatistic(game);
            }
        } finally {
            game.unlock();
        }
        return game;
    }

    private static void calculateStatistic(final Game game) throws GameException {
        GameResult gameResult = getGameResult(game);
        gameResult.getWinner().getStatistics().incrementWin();
        gameResult.getLoser().getStatistics().incrementLose();

        game.getWhitePlayer().getStatistics().incrementPlayWhite();
        game.getBlackPlayer().getStatistics().incrementPlayBlack();
        game.getWhitePlayer().getStatistics().addGameResult(gameResult);
        game.getBlackPlayer().getStatistics().addGameResult(gameResult);
        log.info("CalculateStatistic {} {}", game.getBlackPlayer().getNickname(), game.getBlackPlayer().getStatistics());
        log.info("CalculateStatistic {} {}", game.getWhitePlayer().getNickname(), game.getWhitePlayer().getStatistics());
    }

    private static void choosingPlayerMove(final Game game) throws GameException {
        boolean blackCanMove = BoardService.hasPossibleMove(game.getBoard(), game.getBlackPlayer());
        boolean whiteCanMove = BoardService.hasPossibleMove(game.getBoard(), game.getWhitePlayer());

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
     * @return boolean
     */
    public static GameResult getGameResult(final Game game) throws GameException {
        if (game.getState() != GameState.END) {
            throw new GameException(GameErrorCode.GAME_NOT_FINISHED);
        }
        GameBoard board = game.getBoard();
        long blackCells = BoardService.getCountBlack(board);
        long whiteCells = BoardService.getCountWhite(board);
        if (blackCells <= whiteCells) {
            return GameResult.winner(board, game.getWhitePlayer(), game.getBlackPlayer());
        } else {
            return GameResult.winner(board, game.getBlackPlayer(), game.getWhitePlayer());
        }
    }

    private static void gameIsNotEnd(final Game game) throws GameException {
        if (gameIsFinished(game) || game.getBoard().getCountEmpty() == 0) {
            throw new GameException(GameErrorCode.GAME_ENDED);
        }
    }

    private static void playerValidMove(final Game game, final Player player) throws GameException {
        if (!whatPlayerMoveNow(game).equals(player)) {
            throw new GameException(GameErrorCode.ILLEGAL_REQUEST);
        }
    }

    private static boolean gameIsFinished(final Game game) {
        return game.getState() == GameState.END;
    }

    public static Player whatPlayerMoveNow(final Game game) throws GameException {
        if (game.getState() == GameState.BLACK_MOVE) {
            return game.getBlackPlayer();
        }
        if (game.getState() == GameState.WHITE_MOVE) {
            return game.getWhitePlayer();
        }
        throw new GameException(GameErrorCode.GAME_ENDED);
    }

}