package services;

import com.google.gson.Gson;
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
    public static final Gson GSON = new Gson();





    protected static synchronized int getPlayerId() {
        return playerIncrement++;
    }

    protected static synchronized int getGameId() {
        return gameIncrement++;
    }
}
