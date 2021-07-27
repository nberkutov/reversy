package controllers;

import dto.request.player.GetGameInfoRequest;
import dto.request.player.MovePlayerRequest;
import dto.request.server.CreateGameRequest;
import dto.response.GameResponse;
import dto.response.TaskResponse;
import dto.response.player.GameBoardResponse;
import dto.response.player.SearchGameResponse;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.game.Game;
import models.player.User;
import services.GameService;
import services.PlayerService;

import java.io.IOException;

@Slf4j
public class GameController {

    public static void actionMovePlayer(final MovePlayerRequest movePlayer, final ClientConnection connection) throws IOException, GameException {
        Game game = null;
        try {
            log.debug("action movePlayer {}", movePlayer);
            game = GameService.makePlayerMove(movePlayer, connection);

            sendResponse(game.getBlackUser(), GameBoardResponse.toDto(game, game.getBlackUser()));
            sendResponse(game.getWhiteUser(), GameBoardResponse.toDto(game, game.getWhiteUser()));
        } finally {
            if (game != null) {
                game.unlock();
            }
        }
    }

    public static void actionGetGameInfo(final GetGameInfoRequest getGame, final ClientConnection connection) throws IOException, GameException {
        Game game = GameService.getGameInfo(getGame, connection);
        //TODO maybe Change getGameInfo -> GameResult, when game finish
        sendResponse(connection, GameBoardResponse.toDto(game, connection.getUser()));
        log.debug("getGameInfo, {}", game);
    }

    public static void actionCreateGame(final CreateGameRequest createGame, final ClientConnection connection) throws IOException, GameException {
        Game game = GameService.createGameBySearch(createGame, connection);
        sendInfoAboutGame(game, game.getBlackUser());
        sendInfoAboutGame(game, game.getWhiteUser());
        log.debug("Game created by search, {}", game);
    }

    public static void sendInfoAboutGame(final Game game, final User user) throws IOException {
        try {
            ClientConnection connection = PlayerService.getConnectionByPlayer(user);
            sendResponse(connection, SearchGameResponse.toDto(game, user));
            sendResponse(connection, GameBoardResponse.toDto(game, user));
        } catch (GameException e) {
            log.warn("Cant sendInfoAboutGame {}", user, e);
        }
    }

    public static void sendResponse(final User user, final GameResponse response) throws GameException, IOException {
        sendResponse(PlayerService.getConnectionByPlayer(user), response);
    }

    private static void sendResponse(final ClientConnection connection, final GameResponse response) throws IOException, GameException {
        TaskResponse.createAndSend(connection, response);
    }

}
