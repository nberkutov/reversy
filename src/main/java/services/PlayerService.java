package services;

import dto.request.player.CreatePlayerRequest;
import models.Player;

public class PlayerService extends BaseService {

    public static Player createPlayer(CreatePlayerRequest createPlayerRequest) {
        int id = getPlayerId();
        Player player = new Player(id);
        players.putIfAbsent(id, player);
        return player;
    }

}
