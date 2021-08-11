package controllers;

import controllers.mapper.Mapper;
import dto.request.player.GetReplayGameRequest;
import dto.request.player.MovePlayerRequest;
import dto.response.GameResponse;
import exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.game.Game;
import models.player.User;
import services.GameService;
import services.PlayerService;

import java.io.IOException;

@Slf4j
public class GameController {

    private GameController() {
    }

    public static void actionMovePlayer(final MovePlayerRequest movePlayer, final ClientConnection connection) throws IOException, ServerException {
        Game game = null;
        try {
            log.debug("action movePlayer {}", movePlayer);
            game = GameService.makePlayerMove(movePlayer, connection);

            sendResponse(game.getBlackUser(), Mapper.toDtoGame(game, game.getBlackUser()));
            sendResponse(game.getWhiteUser(), Mapper.toDtoGame(game, game.getWhiteUser()));
        } finally {
            if (game != null) {
                game.unlock();
            }
        }
    }

    public static void actionGetReplayGame(final GetReplayGameRequest getGame, final ClientConnection connection) throws IOException, ServerException {
        Game game = GameService.getReplayGame(getGame, connection);
        sendResponse(connection, Mapper.toDto(game));
        log.debug("action GetReplayGame, {}", game);
    }

    public static void actionCreateGame(final ClientConnection firstConnection, final ClientConnection secondConnection) throws IOException, ServerException {
        Game game = GameService.createGameBySearch(firstConnection, secondConnection);
        sendInfoAboutGame(game, game.getBlackUser());
        sendInfoAboutGame(game, game.getWhiteUser());
        log.debug("Game created by search, {}", game);
    }

    public static void sendInfoAboutGame(final Game game, final User user) throws IOException {
        try {
            ClientConnection connection = PlayerService.getConnectionByPlayer(user);
            sendResponse(connection, Mapper.toDtoSearch(game, user));
            sendResponse(connection, Mapper.toDtoGame(game, user));
        } catch (ServerException e) {
            log.warn("Cant sendInfoAboutGame {}", user, e);
        }
    }

    public static void sendResponse(final User user, final GameResponse response) throws ServerException, IOException {
        sendResponse(PlayerService.getConnectionByPlayer(user), response);
    }

    private static void sendResponse(final ClientConnection connection, final GameResponse response) throws IOException, ServerException {
        TaskResponse.createAndSend(connection, response);
    }

}
