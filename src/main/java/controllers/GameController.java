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
import models.player.Player;
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

            sendResponse(game.getBlackPlayer(), GameBoardResponse.toDto(game));
            sendResponse(game.getWhitePlayer(), GameBoardResponse.toDto(game));
        } finally {
            if (game != null) {
                game.unlock();
            }
        }
    }

    public static void actionGetGameInfo(final GetGameInfoRequest getGame, final ClientConnection connection) throws IOException, GameException {
        Game game = GameService.getGameInfo(getGame, connection);
        sendResponse(connection, GameBoardResponse.toDto(game));
        log.info("getGameInfo, {}", game);
    }

    public static void actionCreateGame(final CreateGameRequest createGame, final ClientConnection connection) throws IOException, GameException {
        Game game = GameService.createGameBySearch(createGame, connection);
        sendInfoAboutGame(game, game.getBlackPlayer());
        sendInfoAboutGame(game, game.getWhitePlayer());
        log.info("Game created by search, {}", game);
    }

    public static void sendInfoAboutGame(final Game game, Player player) throws IOException {
        try {
            ClientConnection connection = PlayerService.getConnectionByPlayer(player);
            sendResponse(connection, SearchGameResponse.toDto(game, player));
            sendResponse(connection, GameBoardResponse.toDto(game));
        } catch (GameException e) {
            log.warn("Cant sendInfoAboutGame {}", player, e);
        }
    }

    private static void sendResponse(final Player player, final GameResponse response) throws GameException, IOException {
        sendResponse(PlayerService.getConnectionByPlayer(player), response);
    }

    private static void sendResponse(final ClientConnection connection, final GameResponse response) throws IOException, GameException {
        TaskResponse.createAndSend(connection, response);
    }

}
