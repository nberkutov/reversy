package controllers;

import dto.request.GameRequest;
import dto.request.TaskRequest;
import dto.request.player.*;
import dto.request.server.CreateGameRequest;
import dto.response.ErrorResponse;
import dto.response.GameResponse;
import dto.response.TaskResponse;
import dto.response.player.CreatePlayerResponse;
import dto.response.player.GameBoardResponse;
import dto.response.player.MessageResponse;
import dto.response.player.SearchGameResponse;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.game.Game;
import models.player.Player;
import services.GameService;
import services.JsonService;
import services.PlayerService;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

@AllArgsConstructor
@Slf4j
public class TasksHandler extends Thread {
    private final LinkedBlockingDeque<TaskRequest> requests;
    private final LinkedBlockingDeque<ClientConnection> waiting;

    @Override
    public void run() {
        log.info("HandlerTasks started");
        while (true) {
            try {
                TaskRequest task = requests.takeFirst();
                try {
                    GameRequest request = task.getRequest();
                    switch (JsonService.getCommandByRequest(request)) {
                        case CREATE_PLAYER:
                            CreatePlayerRequest createPlayer = (CreatePlayerRequest) request;
                            actionCreatePlayer(createPlayer, task.getClient());
                            break;
                        case PLAYER_AUTH:
                            AuthPlayerRequest authPlayer = (AuthPlayerRequest) request;
                            actionAuthPlayer(authPlayer, task.getClient());
                            break;
                        case PLAYER_LOGOUT:
                            LogoutPlayerRequest logoutPlayer = (LogoutPlayerRequest) request;
                            actionLogoutPlayer(logoutPlayer, task.getClient());
                        case WANT_PLAY:
                            WantPlayRequest wantPlay = (WantPlayRequest) request;
                            actionWantPlay(wantPlay, task.getClient());
                            break;
                        case PLAYING_MOVE:
                            MovePlayerRequest movePlayer = (MovePlayerRequest) request;
                            actionMovePlayer(movePlayer, task.getClient());
                            break;
                        case GET_GAME_INFO:
                            GetGameInfoRequest getGame = (GetGameInfoRequest) request;
                            actionGetGameInfo(getGame, task.getClient());
                            break;
                        case PRIVATE_CREATE_GAME:
                            CreateGameRequest createGame = (CreateGameRequest) request;
                            actionCreateGame(createGame, task.getClient());
                            break;
                    }
                } catch (GameException e) {
                    log.warn("HandlerTasks ", e);
                    sendResponse(task.getClient(), ErrorResponse.toDto(e));
                }
            } catch (InterruptedException | IOException | GameException e) {
                log.error("Thread error ", e);
            }
        }
    }

    private void actionCreatePlayer(final CreatePlayerRequest createPlayer, final ClientConnection connection) throws IOException, GameException {
        try {
            Player player = PlayerService.createPlayer(createPlayer, connection);
            log.debug("action createPlayer {} {}", connection.getSocket().getPort(), createPlayer);
            sendResponse(connection, CreatePlayerResponse.toDto(player));
        } catch (GameException e) {
            log.warn("action CreatePlayer error {}", createPlayer, e);
            sendResponse(connection, ErrorResponse.toDto(e));
        }
    }

    private void actionAuthPlayer(AuthPlayerRequest authPlayer, ClientConnection connection) throws IOException, GameException {
        try {
            Player player = PlayerService.authPlayer(authPlayer, connection);
            log.debug("action authPlayer {} {}", connection.getSocket().getPort(), authPlayer);
            sendResponse(connection, CreatePlayerResponse.toDto(player));
        } catch (GameException e) {
            log.warn("action authPlayer error {}", authPlayer, e);
            sendResponse(connection, ErrorResponse.toDto(e));
        }
    }

    private void actionLogoutPlayer(LogoutPlayerRequest logoutPlayer, ClientConnection connection) throws IOException, GameException {
        try {
            PlayerService.logoutPlayer(logoutPlayer, connection);
            log.debug("action logoutPlayer {} {}", connection.getSocket().getPort(), logoutPlayer);
            sendResponse(connection, new MessageResponse("Logout player successfully"));
        } catch (GameException e) {
            log.warn("action authPlayer error {}", logoutPlayer, e);
            sendResponse(connection, ErrorResponse.toDto(e));
        }
    }

    private void actionGetGameInfo(GetGameInfoRequest getGame, ClientConnection connection) throws IOException, GameException {
        try {
            Game game = GameService.getGameInfo(getGame, connection);
            sendResponse(connection, GameBoardResponse.toDto(game));
            log.info("getGameInfo, {}", game);
        } catch (GameException e) {
            sendResponse(connection, ErrorResponse.toDto(e));
        }
    }

    private void actionCreateGame(CreateGameRequest createGame, ClientConnection connection) throws InterruptedException, IOException, GameException {
        try {
            Game game = GameService.createGame(createGame, connection);
            sendInfoAboutGame(game, game.getBlackPlayer());
            sendInfoAboutGame(game, game.getWhitePlayer());
            log.info("Game created, {}", game);
        } catch (GameException e) {
            log.warn("action CreateGame error {}", createGame, e);
            sendResponse(connection, ErrorResponse.toDto(e));
        }
    }

    private void sendInfoAboutGame(final Game game, Player player) throws GameException, IOException {
        ClientConnection connection = PlayerService.getConnectionByPlayer(player);
        sendResponse(connection, SearchGameResponse.toDto(game, player));
        sendResponse(connection, GameBoardResponse.toDto(game));
    }


    public void actionWantPlay(final WantPlayRequest wantPlay, final ClientConnection connection) throws InterruptedException, IOException, GameException {
        try {
            PlayerService.canPlayerSearchGame(connection);
            waiting.putLast(connection);
            sendResponse(connection, new MessageResponse("Search game"));
            log.debug("player put in waiting {}", connection.getPlayer());
        } catch (GameException e) {
            log.warn("action wantPlay error {}", wantPlay);
            sendResponse(connection, ErrorResponse.toDto(e));
        }
    }

    public void actionMovePlayer(final MovePlayerRequest movePlayer, final ClientConnection connection) throws IOException, GameException {
        Game game = null;
        try {
            log.debug("action movePlayer {}", movePlayer);
            game = GameService.makePlayerMove(movePlayer, connection);

            sendResponse(game.getBlackPlayer(), GameBoardResponse.toDto(game));
            sendResponse(game.getWhitePlayer(), GameBoardResponse.toDto(game));
        } catch (GameException e) {
            log.warn("action movePlayer {} {}", connection.getSocket().getPort(), movePlayer);
            sendResponse(connection, ErrorResponse.toDto(e));
        } finally {
            if (game != null) {
                game.unlock();
            }
        }
    }

    private void sendResponse(final Player player, final GameResponse response) throws GameException, IOException {
        sendResponse(PlayerService.getConnectionByPlayer(player), response);
    }

    private void sendResponse(final ClientConnection connection, final GameResponse response) throws IOException, GameException {
        TaskResponse.createAndSend(connection, response);
    }
}
