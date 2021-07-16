package services;

import dto.request.player.CreatePlayerRequest;
import exception.GameErrorCode;
import exception.GameException;
import models.ClientConnection;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.player.Player;

public class PlayerService extends BaseService {

    public static Player createPlayer(final CreatePlayerRequest createPlayerRequest, ClientConnection connection) throws GameException {
        requestIsNotNull(createPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        int id = getPlayerId();
        Player player = new Player(id);
        players.putIfAbsent(id, player);
        connects.putIfAbsent(id, connection);
        connection.initPlayer(player);
        return player;
    }


    public static ClientConnection getConnectionById(final int id) {
        return connects.get(id);
    }

    public static ClientConnection getConnectionByPlayer(final Player player) throws GameException {
        playerIsNotNull(player);
        ClientConnection connection = connects.get(player.getId());
        connectionIsNotNullAndConnected(connection);
        return connection;
    }

    public static boolean isCanPlay(final ClientConnection connection) {
        if (connection == null) {
            return false;
        }
        return connection.isConnected();
    }

    public static void setPlayerStateNone(final ClientConnection connection) throws GameException {
        connectionIsNotNullAndConnected(connection);
        Player player = connection.getPlayer();
        setPlayerStateNone(player);
    }

    public static void setPlayerStateNone(final Player player) throws GameException {
        playerIsNotNull(player);
        player.setState(PlayerState.NONE);
        player.setColor(PlayerColor.NONE);
    }


    public static Player canPlayerSearchGame(ClientConnection clientConnection) throws GameException {
        connectionIsNotNullAndConnected(clientConnection);
        Player player = clientConnection.getPlayer();
        return canPlayerSearchGame(player);
    }

    public static Player canPlayerSearchGame(final Player player) throws GameException {
        playerIsNotNull(player);
        checkPlayerCanFindGame(player);
        player.setState(PlayerState.SEARCH_GAME);
        return player;
    }

    private static void checkPlayerCanFindGame(final Player player) throws GameException {
        if (player.getState() == PlayerState.PLAYING || player.getState() == PlayerState.SEARCH_GAME) {
            throw new GameException(GameErrorCode.PLAYER_CANNOT_FIND_GAME);
        }
    }

}
