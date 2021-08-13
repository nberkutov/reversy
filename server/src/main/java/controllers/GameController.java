package controllers;

import controllers.mapper.Mapper;
import dto.request.player.GetReplayGameRequest;
import dto.request.player.MovePlayerRequest;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.game.Game;
import services.GameService;
import services.SenderService;

@Slf4j
public class GameController {

    private GameController() {
    }

    public static void actionMovePlayer(final MovePlayerRequest movePlayer, final ClientConnection connection) throws ServerException {
        GameService.makePlayerMove(movePlayer, connection);
        log.debug("action movePlayer {}", movePlayer);
    }

    public static void actionGetReplayGame(final GetReplayGameRequest getGame, final ClientConnection connection) throws ServerException {
        final Game game = GameService.getReplayGame(getGame, connection);
        SenderService.sendResponse(connection, Mapper.toDtoReplay(game));
        log.debug("action GetReplayGame, {}", game);
    }

}
