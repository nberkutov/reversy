package services;

import dto.request.player.GameRequest;
import exception.GameErrorCode;
import exception.GameException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.base.PlayerState;
import models.board.Point;
import models.game.Game;
import models.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static models.board.Board.BOARD_SIZE;

@Slf4j
@NoArgsConstructor
public class BaseService {
    //MiniBD
    protected static int gameIncrement = 0;
    protected static final Map<Integer, Game> games = new ConcurrentHashMap<>();
    protected static int playerIncrement = 0;
    protected static final Map<Integer, Player> players = new ConcurrentHashMap<>();
    protected static final Map<Integer, ClientConnection> connects = new ConcurrentHashMap<>();


    protected static synchronized int getPlayerId() {
        return playerIncrement++;
    }

    protected static synchronized int getGameId() {
        return gameIncrement++;
    }

    /**
     * Функция провероки
     * Если game равен null, то выбрасывает GameException.
     *
     * @param game - класс игры
     */
    protected static void gameIsNotNull(final Game game) throws GameException {
        if (game == null) {
            throw new GameException(GameErrorCode.GAME_NOT_FOUND);
        }
    }

    /**
     * Функция провероки
     * Если player равен null, то выбрасывает GameException.
     *
     * @param player - класс игрока
     */
    protected static void playerIsNotNull(final Player player) throws GameException {
        if (player == null) {
            throw new GameException(GameErrorCode.PLAYER_NOT_FOUND);
        }
    }

    protected static void playerIsNotPlaying(final Player player) throws GameException {
        if (player.getState() == PlayerState.PLAYING) {
            throw new GameException(GameErrorCode.PLAYER_ALREADY_PLAYING);
        }
    }

    /**
     * Функция провероки
     * Если point равен null, то выбрасывает GameException.
     *
     * @param point - игровое поле
     */
    protected static void checkPointIsInside(final Point point) throws GameException {
        if (point != null
                && point.getX() >= 0
                && point.getY() >= 0
                && point.getX() < BOARD_SIZE
                && point.getY() < BOARD_SIZE) {
            return;
        }
        log.error("Bad checkPoint", new GameException(GameErrorCode.BAD_POINT));
        throw new GameException(GameErrorCode.BAD_POINT);
    }

    protected static void checkPlayerConnection(final Player player) throws GameException {
        ClientConnection connection = PlayerService.getConnectionById(player.getId());
        connectionIsNotNullAndConnected(connection);
    }

    protected static void connectionIsNotNullAndConnected(ClientConnection connection) throws GameException {
        if (connection == null || !connection.isConnected()) {
            throw new GameException(GameErrorCode.CONNECTION_LOST);
        }
    }

    protected static void requestIsNotNull(GameRequest request) throws GameException {
        if (request == null) {
            throw new GameException(GameErrorCode.INVALID_REQUEST);
        }
    }
}
