package services;

import exception.GameErrorCode;
import exception.GameException;
import models.Game;
import models.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseService {
    //BD coming +++
    protected static int gameIncrement = 0;
    protected static final Map<Integer, Game> games = new ConcurrentHashMap<>();

    protected static int playerIncrement = 0;
    protected static final Map<Integer, Player> players = new ConcurrentHashMap<>();
    //BD ---

    protected static Game getGameById(int idGame) throws GameException {
        Game game = games.get(idGame);
        if (game == null) {
            throw new GameException(GameErrorCode.GAME_NOT_FOUND);
        }
        return game;
    }

    protected static Player getPlayerById(int id) throws GameException {
        Player player = players.get(id);
        if(player== null){
            throw new GameException(GameErrorCode.GAME_NOT_FOUND);
        }
        return player;
    }

    protected static synchronized int getPlayerId(){
        return playerIncrement++;
    }
}
