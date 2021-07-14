package controllers;

import controllers.commands.CommandRequest;
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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@AllArgsConstructor
@Slf4j
public class HandlerTasks extends Thread {
    private final BlockingQueue<TaskRequest> requests;
    private final BlockingQueue<TaskResponse> responses;
    private final BlockingQueue<ClientConnection> waiting;

    @Override
    public void run() {
        log.debug("HandlerTasks started");
        while (true) {
            try {
                TaskRequest task = requests.take();
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

    private void actionCreateGame(CreateGameRequest createGame, ClientConnection connection) throws InterruptedException {
        log.info("action createGame {}", createGame);
        try {
            Game game = GameService.createGame(createGame, connection);
            sendInfoAboutGame(game, game.getBlackPlayer());
            sendInfoAboutGame(game, game.getWhitePlayer());
            log.info("Game created, {}", game);
        } catch (GameException e) {
            log.info("action CreateGame error {}", createGame, e);
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    private void sendInfoAboutGame(final Game game, final Player player) throws InterruptedException, GameException {
        addTaskResponse(player, Arrays.asList(SearchGameResponse.toDto(game, player), GameBoardResponse.toDto(game)));
    }

    public void actionCreatePlayer(final CreatePlayerRequest createPlayer, final ClientConnection connection) throws InterruptedException {
        log.info("action createPlayer {}", createPlayer);
        try {
            Player player = PlayerService.createPlayer(createPlayer, connection);
            addTaskResponse(connection, CreatePlayerResponse.toDto(player));
        } catch (GameException e) {
            log.info("action CreatePlayer error {}", createPlayer, e);
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    public void actionWantPlay(final WantPlayRequest wantPlay, final ClientConnection connection) throws InterruptedException {
        try {
            log.info("action wantPlay {}", wantPlay);
            PlayerService.canPlayerSearchGame(connection);
            waiting.put(connection);
            addTaskResponse(connection, new MessageResponse("Search game"));
            log.info("player put in waiting {}", connection.getPlayer());
        } catch (GameException e) {
            log.info("action wantPlay error {}", wantPlay, e);
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    public void actionMovePlayer(final MovePlayerRequest movePlayer, final ClientConnection connection) throws InterruptedException {
        try {
            log.debug("action movePlayer {}", movePlayer);
            Game game = GameService.makePlayerMove(movePlayer, connection);
            addTaskResponse(game.getWhitePlayer(), GameBoardResponse.toDto(game));
            addTaskResponse(game.getBlackPlayer(), GameBoardResponse.toDto(game));
        } catch (GameException e) {
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    private void addTaskResponse(final Player player, final GameResponse response) throws InterruptedException, GameException {
        addTaskResponse(PlayerService.getConnectionByPlayer(player), response);
    }

    private void addTaskResponse(final Player player, final List<GameResponse> response) throws InterruptedException, GameException {
        addTaskResponse(PlayerService.getConnectionByPlayer(player), response);
    }

    private void addTaskResponse(final ClientConnection connection, final List<GameResponse> response) throws InterruptedException {
        responses.put(new TaskResponse(connection, response));
    }

    private void addTaskResponse(final ClientConnection connection, final GameResponse response) throws InterruptedException {
        responses.put(new TaskResponse(connection, response));
    }
}
