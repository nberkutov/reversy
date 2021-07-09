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
public class GameService extends BaseService {


   // private final BoardService service = new BoardService();

    public static Game createGame() {
        throw new NotImplementedException();
    }

    public static GameResponse moveFromPlayer(MovePlayerRequest movePlayerRequest) {
        try {
            Game game = getGameById(movePlayerRequest.getIdGame());
            Player player = getPlayerById(movePlayerRequest.getIdPlayer());

            throw new GameException(GameErrorCode.GAME_NOT_FINISHED);
        } catch (GameException exception) {
            log.error("Error", exception);
            return new GameResponse();
        }
    }


}