package services;

import dto.request.player.MovePlayerRequest;
import dto.request.server.CreateGameRequest;
import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.base.GameState;
import models.base.PlayerState;
import models.board.Board;
import models.board.Point;
import models.game.Game;
import models.game.GameResult;
import models.player.Player;

@Slf4j
public class GameService extends BaseService {

    public static Game createGame(CreateGameRequest createGame) throws GameException {
        checkRequestIsNull(createGame);
        Player first = PlayerService.getPlayerById(createGame.getFirstPlayerId());
        Player second = PlayerService.getPlayerById(createGame.getSecondPlayerId());
        return createGame(first, second);
    }

    public static Game createGame(final Player first, final Player second) throws GameException {
        playerIsNotNull(first);
        playerIsNotNull(second);
        playerIsPlaying(first);
        playerIsPlaying(second);
        int gameId = getGameId();
        Game game = new Game(gameId, first, second);
        games.putIfAbsent(gameId, game);
        first.setState(PlayerState.PLAYING);
        second.setState(PlayerState.PLAYING);
        return game;
    }

    public static Game makePlayerMove(final MovePlayerRequest movePlayer) throws GameException {
        checkRequestIsNull(movePlayer);
        Player player = PlayerService.getPlayerById(movePlayer.getPlayerId());
        checkPlayerConnection(player);
        Game game = GameService.getGameById(movePlayer.getGameId());
        return makePlayerMove(game, movePlayer.getPoint(), player);
    }

    public static Game makePlayerMove(final Game game, final Point point, final Player player) throws GameException {
        checkGameEnd(game);
        checkValidCanPlayerMove(game, player);
        BoardService.makeMove(game, point, player.getColor());
        switch (game.getState()) {
            case BLACK_MOVE:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getWhitePlayer())) {
                    game.setState(GameState.WHITE_MOVE);
                }
                break;
            case WHITE_MOVE:
                if (BoardService.hasPossibleMove(game.getBoard(), game.getBlackPlayer())) {
                    game.setState(GameState.BLACK_MOVE);
                }
                break;
        }
        if (isGameEnd(game)) {
            log.info("GameEnd {} \n{}", game, game.getBoard().getVisualString());
            game.setState(GameState.END);
            PlayerService.setPlayerStateNone(game.getBlackPlayer());
            PlayerService.setPlayerStateNone(game.getWhitePlayer());
        }
        return game;
    }

    public static Game getGameById(final int gameId) throws GameException {
        Game game = games.get(gameId);
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
            case END:
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

    private static void checkGameEnd(final Game game) throws GameException {
        if (game.isFinished()) {
            throw new GameException(GameErrorCode.GAME_ENDED);
        }
    }

    private static void checkValidCanPlayerMove(final Game game, final Player player) throws GameException {
        if (
                (game.getState() == GameState.BLACK_MOVE && game.getBlackPlayer().equals(player))
                        || (game.getState() == GameState.WHITE_MOVE && game.getWhitePlayer().equals(player))
        ) {
            return;
        }
        throw new GameException(GameErrorCode.INVALID_REQUEST);
    }
}