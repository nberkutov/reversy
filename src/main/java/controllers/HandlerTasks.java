package controllers;

import controllers.commands.CommandRequest;
import dto.request.TaskRequest;
import dto.request.player.*;
import dto.request.server.CreateGameRequest;
import dto.response.*;
import dto.response.player.CreatePlayerResponse;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.game.Game;
import models.player.Player;
import services.GameService;
import services.PlayerService;

import java.util.concurrent.LinkedBlockingDeque;

@AllArgsConstructor
@Slf4j
public class HandlerTasks extends Thread {
    private final LinkedBlockingDeque<TaskRequest> requests;
    private final LinkedBlockingDeque<TaskResponse> responses;
    private final LinkedBlockingDeque<ClientConnection> waiting;

    @Override
    public void run() {
        log.info("HandlerTasks started");
        while (true) {
            try {
                TaskRequest task = requests.takeFirst();
                GameRequest request = task.getRequest();
                switch (CommandRequest.getCommandByRequest(request)) {
                    case CREATE_PLAYER:
                        CreatePlayerRequest createPlayer = (CreatePlayerRequest) request;
                        actionCreatePlayer(createPlayer, task.getClient());
                        break;
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
            } catch (InterruptedException | GameException e) {
                log.error("HandlerTasks ", e);
            }
        }
    }

    private synchronized void actionGetGameInfo(final GetGameInfoRequest getGame, final ClientConnection connection) {
        try {
            Game game = GameService.getGameInfo(getGame, connection);
            addTaskResponse(connection, GameBoardResponse.toDto(game));
            log.info("getGameInfo, {}", game);
        } catch (GameException e) {
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    private synchronized void actionCreateGame(final CreateGameRequest createGame, final ClientConnection connection) {
        try {
            Game game = GameService.createGame(createGame, connection);
            sendInfoAboutGame(game, game.getBlackPlayer());
            sendInfoAboutGame(game, game.getWhitePlayer());
            log.info("Game created, {}", game);
        } catch (GameException e) {
            log.warn("action CreateGame error {}", createGame, e);
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    private synchronized void sendInfoAboutGame(final Game game, final Player player) throws GameException {
        ClientConnection connection = PlayerService.getConnectionByPlayer(player);
        addTaskResponse(connection, SearchGameResponse.toDto(game, player));
        addTaskResponse(connection, GameBoardResponse.toDto(game));
    }

    public synchronized void actionCreatePlayer(final CreatePlayerRequest createPlayer, final ClientConnection connection) {
        try {
            Player player = PlayerService.createPlayer(createPlayer, connection);
            log.debug("action createPlayer {} {}", connection.getSocket().getPort(), createPlayer);
            addTaskResponse(connection, CreatePlayerResponse.toDto(player));
        } catch (GameException e) {
            log.warn("action CreatePlayer error {}", createPlayer, e);
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    public synchronized void actionWantPlay(final WantPlayRequest wantPlay, final ClientConnection connection) throws InterruptedException {
        try {
            PlayerService.canPlayerSearchGame(connection);
            waiting.putLast(connection);
            addTaskResponse(connection, new MessageResponse("Search game"));
            log.debug("player put in waiting {}", connection.getPlayer());
        } catch (GameException e) {
            log.warn("action wantPlay error {}", wantPlay);
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    public synchronized void actionMovePlayer(final MovePlayerRequest movePlayer, final ClientConnection connection) throws InterruptedException {

        try {
            log.debug("action movePlayer {}", movePlayer);
            Game game = GameService.makePlayerMove(movePlayer, connection);

            addTaskResponse(game.getBlackPlayer(), GameBoardResponse.toDto(game));
            addTaskResponse(game.getWhitePlayer(), GameBoardResponse.toDto(game));
        } catch (GameException e) {
            log.warn("action movePlayer {} {}", connection.getSocket().getPort(), movePlayer);
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    private void addTaskResponse(final Player player, final GameResponse response) throws GameException {
        addTaskResponse(PlayerService.getConnectionByPlayer(player), response);
    }

    private void addTaskResponse(final ClientConnection connection, final GameResponse response) {
        responses.addLast(TaskResponse.create(connection, response));
    }
}
