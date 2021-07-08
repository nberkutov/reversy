package services;

import dto.request.player.MovePlayerRequest;
import dto.response.GameResponse;
import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.Game;
import models.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class GameService {
    //BD coming +++
    private int gameIncrement = 0;
    private final Map<Integer, Game> games = new ConcurrentHashMap<>();

    private int playerIncrement = 0;
    private final Map<Integer, Player> players = new ConcurrentHashMap<>();
    //BD ---

   // private final BoardService service = new BoardService();

    public Game createGame() {
        throw new NotImplementedException();
    }

    public GameResponse moveFromPlayer(MovePlayerRequest movePlayerRequest) {
        try {
            Game game = getGameById(movePlayerRequest.getIdGame());
            Player player = getPlayerById(movePlayerRequest.getIdPlayer());

            throw new GameException(GameErrorCode.GAME_NOT_FINISHED);
        } catch (GameException exception) {
            log.error("Error", exception);
            return new GameResponse();
        }
    }

    private Game getGameById(int idGame) throws GameException {
        Game game = games.get(idGame);
        if (game == null) {
            throw new GameException(GameErrorCode.GAME_NOT_FOUND);
        }
        return game;
    }

    public Player getPlayerById(int id) throws GameException {
        Player player = players.get(id);
        if(player== null){
            throw new GameException(GameErrorCode.GAME_NOT_FOUND);
        }
        return player;
    }
}