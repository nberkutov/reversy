package services;

import dto.request.player.CreatePlayerRequest;
import dto.request.player.WantPlayRequest;
import exception.GameErrorCode;
import exception.GameException;
import models.ClientConnection;
import models.player.Player;
import models.base.PlayerState;

public class PlayerService extends BaseService {

    public static Player createPlayer(final CreatePlayerRequest createPlayerRequest, ClientConnection connection) throws GameException {
        checkRequestIsNull(createPlayerRequest);
        int id = getPlayerId();
        Player player = new Player(id);
        players.putIfAbsent(id, player);
        player.initConnect(connection);
        return player;
    }

    public static Player getPlayerById(final int id) throws GameException {
        Player player = players.get(id);
        if (player == null) {
            throw new GameException(GameErrorCode.PLAYER_NOT_FOUND);
        }
        return player;
    }

    public static boolean isCanPlay(final Player player) {
        return player.getConnection() != null
                && player.getConnection().isConnected();
    }

    public static void setPlayerStateNone(final Player player) throws GameException {
        playerIsNotNull(player);
        player.setState(PlayerState.NONE);
    }


    public static Player canPlayerSearchGame(final WantPlayRequest wantPlay) throws GameException {
        checkRequestIsNull(wantPlay);
        return canPlayerSearchGame(wantPlay.getId());
    }

    public static Player canPlayerSearchGame(final int id) throws GameException {
        Player player = PlayerService.getPlayerById(id);
        checkPlayerConnection(player);
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
