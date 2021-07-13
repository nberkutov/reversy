package controllers;

import controllers.commands.CommandRequest;
import dto.request.player.*;
import dto.response.TaskResponse;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

@Data
@AllArgsConstructor
@Slf4j
public class HandlerTasks extends Thread {
    private final BlockingQueue<TaskRequest> requests;
    private final BlockingQueue<TaskResponse> responses;

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
                        actionCreatePlayer(createPlayer);
                        break;
                    case WANT_PLAY:
                        WantPlayRequest wantPlay = (WantPlayRequest) request;
                        actionWantPlay(wantPlay);
                        break;
                    case PLAYING_MOVE:
                        MovePlayerRequest movePlayer = (MovePlayerRequest) request;
                        actionMovePlayer(movePlayer);
                        break;
                }

            } catch (InterruptedException | GameException e) {
                e.printStackTrace();
            }
        }
    }

    private void actionCreatePlayer(CreatePlayerRequest createPlayer) {
        log.debug("action createPlayer {}", createPlayer);
    }

    private void actionWantPlay(WantPlayRequest wantPlay) {
        log.debug("action wantPlay {}", wantPlay);
    }

    private void actionMovePlayer(MovePlayerRequest movePlayer) {
        log.debug("action movePlayer {}", movePlayer);
    }
}
