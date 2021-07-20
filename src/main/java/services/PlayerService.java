package services;

import dto.request.player.AuthPlayerRequest;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.LogoutPlayerRequest;
import exception.GameErrorCode;
import exception.GameException;
import models.ClientConnection;
import models.GameProperties;
import models.base.PlayerColor;
import models.base.PlayerState;
import models.player.Player;

public class PlayerService extends DataBaseService {

    public static synchronized Player createPlayer(final CreatePlayerRequest createPlayerRequest, final ClientConnection connection) throws GameException {
        requestIsNotNull(createPlayerRequest);
        nicknameIsNotNull(createPlayerRequest);
        validateNickname(createPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        connectionIsAuthed(connection);
        String nickname = createPlayerRequest.getNickname();
        nicknameIsUsedAlready(nickname);
        int id = getPlayerId();
        Player player = putPlayer(id, nickname);
        putConnection(id, nickname, connection);
        connection.initPlayer(player);
        return player;
    }

    public static Player authPlayer(final AuthPlayerRequest createPlayerRequest, final ClientConnection connection) throws GameException {
        requestIsNotNull(createPlayerRequest);
        nicknameIsNotNull(createPlayerRequest);
        validateNickname(createPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        connectionIsAuthed(connection);
        String nickname = createPlayerRequest.getNickname();
        Player player = getPlayerByNickname(nickname);
        playerIsNotNull(player);
        putConnection(nickname, connection);
        connection.initPlayer(player);
        return player;
    }

    public static Player logoutPlayer(final LogoutPlayerRequest logoutPlayerRequest, final ClientConnection connection) throws GameException {
        requestIsNotNull(logoutPlayerRequest);
        connectionIsNotNullAndConnected(connection);
        Player player = connection.getPlayer();
        playerIsNotNull(player);
        putConnection(player.getId(), player.getNickname(), null);
        connection.initPlayer(null);
        return player;
    }


    public static ClientConnection getConnectionByPlayer(final Player player) throws GameException {
        playerIsNotNull(player);
        ClientConnection connection = getConnectionById(player.getId());
        connectionIsNotNullAndConnected(connection);
        return connection;
    }

    public static boolean canPlay(final ClientConnection connection) {
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


    public static Player canPlayerSearchGame(final ClientConnection clientConnection) throws GameException {
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

    private static void nicknameIsNotNull(final CreatePlayerRequest createPlayerRequest) throws GameException {
        if (createPlayerRequest.getNickname() == null) {
            throw new GameException(GameErrorCode.INVALID_REQUEST);
        }
    }

    private static void validateNickname(CreatePlayerRequest createPlayerRequest) throws GameException {
        String nickname = createPlayerRequest.getNickname().trim().toLowerCase();
        if (nickname.isEmpty()
                || nickname.length() < GameProperties.MIN_SIZE_NICKNAME
                || nickname.length() > GameProperties.MAX_SIZE_NICKNAME) {
            throw new GameException(GameErrorCode.INVALID_NICKNAME);
        }
        createPlayerRequest.setNickname(nickname);
    }

}
