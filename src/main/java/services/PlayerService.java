package services;

import dto.request.player.CreatePlayerRequest;
import exception.GameErrorCode;
import exception.GameException;
import models.Player;

public class PlayerService extends BaseService {

    public static Player createPlayer(CreatePlayerRequest createPlayerRequest) {
        int id = getPlayerId();
        Player player = new Player(id);
        players.putIfAbsent(id, player);
        return player;
    }

    public static Player getPlayerById(int id) throws GameException {
        Player player = players.get(id);
        if (player == null) {
            throw new GameException(GameErrorCode.PLAYER_NOT_FOUND);
        }
        return player;
    }

}
