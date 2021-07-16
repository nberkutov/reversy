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
import models.board.Board;
import models.board.Point;
import models.game.Game;
import models.game.GameResult;
import models.player.Player;

@Slf4j
public class GameService extends BaseService {

    public static Game createGame(final CreateGameRequest createGame, final ClientConnection connection) throws GameException {
        requestIsNotNull(createGame);
        connectionIsNotNullAndConnected(connection);
        ClientConnection firstCon = PlayerService.getConnectionById(createGame.getFirstPlayerId());
        connectionIsNotNullAndConnected(firstCon);
        ClientConnection secondCon = PlayerService.getConnectionById(createGame.getSecondPlayerId());
        connectionIsNotNullAndConnected(secondCon);
        Player first = firstCon.getPlayer();
        Player second = secondCon.getPlayer();
        return createGame(first, second);
    }

    public static Game getGameInfo(GetGameInfoRequest getGame, ClientConnection connection) throws GameException {
        requestIsNotNull(getGame);
        connectionIsNotNullAndConnected(connection);
        Game game = GameService.getGameById(getGame.getGameId());
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
        games.putIfAbsent(gameId, game);
        first.setState(PlayerState.PLAYING);
        second.setState(PlayerState.PLAYING);
        return game;
    }

    public static Game makePlayerMove(final MovePlayerRequest movePlayer, final ClientConnection connection) throws GameException {
        requestIsNotNull(movePlayer);
        connectionIsNotNullAndConnected(connection);
        Player player = connection.getPlayer();
        Game game = GameService.getGameById(movePlayer.getGameId());
        return makePlayerMove(game, movePlayer.getPoint(), player);
    }

    public static Game makePlayerMove(final Game game, final Point point, final Player player) throws GameException {
        gameIsNotNull(game);
        gameIsNotEnd(game);
        playerIsNotNull(player);
        playerValidMove(game, player);

        BoardService.makeMove(game, point, player.getColor());
        choosingPlayerMove(game);

        if (game.isFinished()) {
            log.info("GameEnd {} \n{}", game, game.getBoard());
            game.setState(GameState.END);
            PlayerService.setPlayerStateNone(game.getBlackPlayer());
            PlayerService.setPlayerStateNone(game.getWhitePlayer());
        }

        return game;
    }

    private static void choosingPlayerMove(Game game) throws GameException {
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

    public static Game getGameById(final int gameId) {
        return games.get(gameId);
    }

    /**
     * Функция делает ход игры
     *
     * @param game - Игра
     */
    public static void playNext(final Game game) throws GameException {
        switch (game.getState()) {
            case BLACK_MOVE:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getBlackPlayer())) {
                    game.getBlackPlayer().nextMove(game);
                }
                game.setState(GameState.WHITE_MOVE);
                break;
            case WHITE_MOVE:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getWhitePlayer())) {
                    game.getWhitePlayer().nextMove(game);
                }
                game.setState(GameState.BLACK_MOVE);
                break;
        }
        if (GameService.isGameEnd(game)) {
            game.setState(GameState.END);
        }
    }

    /**
     * Функция просчитывает и вовзвращает закончена ли игра
     *
     * @param game - Игра
     * @return boolean
     */
    public static boolean isGameEnd(final Game game) throws GameException {
        if (BoardService.getCountEmpty(game.getBoard()) == 0) {
            return true;
        }
        return !BoardService.hasPossibleMove(game.getBoard(), game.getBlackPlayer())
                && !BoardService.hasPossibleMove(game.getBoard(), game.getWhitePlayer());
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
        Board board = game.getBoard();
        long blackCells = BoardService.getCountBlack(board);
        long whiteCells = BoardService.getCountWhite(board);
        if (blackCells <= whiteCells) {
            return GameResult.winner(board, game.getWhitePlayer());
        } else {
            return GameResult.winner(board, game.getBlackPlayer());
        }
    }

    private static void gameIsNotEnd(final Game game) throws GameException {
        if (game.isFinished() || game.getBoard().getCountEmpty() == 0) {
            throw new GameException(GameErrorCode.GAME_ENDED);
        }
    }

    private static void playerValidMove(final Game game, final Player player) throws GameException {
        if (!whatPlayerMoveNow(game).equals(player)) {
            throw new GameException(GameErrorCode.ILLEGAL_REQUEST);
        }
    }

    public static Player whatPlayerMoveNow(Game game) throws GameException {
        if (game.getState() == GameState.BLACK_MOVE) {
            return game.getBlackPlayer();
        }
        if (game.getState() == GameState.WHITE_MOVE) {
            return game.getWhitePlayer();
        }
        throw new GameException(GameErrorCode.GAME_ENDED);
    }

}