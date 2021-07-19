package controllers;

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
import services.JsonService;
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
                try {
                    GameRequest request = task.getRequest();
                    switch (JsonService.getCommandByRequest(request)) {
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
                } catch (GameException e) {
                    log.warn("HandlerTasks ", e);
                    addTaskResponse(task.getClient(), ErrorResponse.toDto(e));
                }
            } catch (InterruptedException e) {
                log.error("Thread error ", e);
            }
        }
    }

    private void actionGetGameInfo(GetGameInfoRequest getGame, ClientConnection connection) throws InterruptedException {
        try {
            Game game = GameService.getGameInfo(getGame, connection);
            addTaskResponse(connection, GameBoardResponse.toDto(game));
            log.info("getGameInfo, {}", game);
        } catch (GameException e) {
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    private void actionCreateGame(CreateGameRequest createGame, ClientConnection connection) throws InterruptedException {
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

    private void sendInfoAboutGame(final Game game, Player player) throws GameException, InterruptedException {
        ClientConnection connection = PlayerService.getConnectionByPlayer(player);
        addTaskResponse(connection, SearchGameResponse.toDto(game, player));
        addTaskResponse(connection, GameBoardResponse.toDto(game));
    }

    public void actionCreatePlayer(final CreatePlayerRequest createPlayer, final ClientConnection connection) throws InterruptedException {
        try {
            PlayerService.createPlayer(createPlayer, connection);
            log.debug("action createPlayer {} {}", connection.getSocket().getPort(), createPlayer);
            addTaskResponse(connection, new CreatePlayerResponse());
        } catch (GameException e) {
            log.warn("action CreatePlayer error {}", createPlayer, e);
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    public void actionWantPlay(final WantPlayRequest wantPlay, final ClientConnection connection) throws InterruptedException {
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

    public void actionMovePlayer(final MovePlayerRequest movePlayer, final ClientConnection connection) throws InterruptedException {
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

    private void addTaskResponse(final Player player, final GameResponse response) throws GameException, InterruptedException {
        addTaskResponse(PlayerService.getConnectionByPlayer(player), response);
    }

    private void addTaskResponse(final ClientConnection connection, final GameResponse response) throws InterruptedException {
        responses.putLast(TaskResponse.create(connection, response));
    }
}
