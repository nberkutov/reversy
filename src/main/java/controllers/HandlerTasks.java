package controllers;

import controllers.commands.CommandRequest;
import dto.request.player.*;
import dto.response.*;
import dto.response.player.CreatePlayerResponse;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.Game;
import models.Player;
import services.GameService;
import services.PlayerService;

import java.util.concurrent.BlockingQueue;

@AllArgsConstructor
@Slf4j
public class HandlerTasks extends Thread {
    private final BlockingQueue<TaskRequest> requests;
    private final BlockingQueue<TaskResponse> responses;
    private final BlockingQueue<Player> waiting;

    @Override
    public void run() {
        log.debug("HandlerTasks started");
        while (true) {
            try {
                TaskRequest task = requests.take();
                GameRequest request = task.getRequest();
                log.info("Request {}", request);
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
                }
            } catch (InterruptedException | GameException e) {
                e.printStackTrace();
            }
        }
    }

    public void actionCreatePlayer(CreatePlayerRequest createPlayer, ClientConnection connection) throws InterruptedException {
        log.debug("action createPlayer {}", createPlayer);
        Player player = PlayerService.createPlayer(createPlayer, connection);
        addTaskResponse(player, CreatePlayerResponse.toDto(player));
    }

    public void actionWantPlay(WantPlayRequest wantPlay, ClientConnection connection) throws InterruptedException {
        log.debug("action wantPlay {}", wantPlay);
        try {
            Player player = PlayerService.isPlayerCanSearchGame(wantPlay);
            waiting.put(player);
            addTaskResponse(player, new MessageResponse("Search game"));
        } catch (GameException e) {
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }

    public void actionMovePlayer(MovePlayerRequest movePlayer, ClientConnection connection) throws InterruptedException {
        log.debug("action movePlayer {}", movePlayer);
        try {
            Game game = GameService.moveFromPlayer(movePlayer);
            addTaskResponse(game.getWhitePlayer(), GameBoardResponse.toDto(game));
            addTaskResponse(game.getBlackPlayer(), GameBoardResponse.toDto(game));
        } catch (GameException e) {
            addTaskResponse(connection, ErrorResponse.toDto(e));
        }
    }


    public void addTaskResponse(Player player, GameResponse response) throws InterruptedException {
        addTaskResponse(player.getConnection(), response);
    }

    private void addTaskResponse(ClientConnection connection, GameResponse response) throws InterruptedException {
        responses.put(new TaskResponse(connection, response));
    }
}
