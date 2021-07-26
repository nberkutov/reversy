package controllers.handlers;

import dto.request.TaskRequest;
import dto.request.server.CreateGameRequest;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import services.PlayerService;

import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@AllArgsConstructor
public class GameSearcher extends Thread {
    private final LinkedBlockingDeque<TaskRequest> requests;
    private final LinkedBlockingDeque<ClientConnection> waiting;


    @Override
    public void run() {
        log.info("GameSearcher started");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ClientConnection first = waiting.takeFirst();
                    ClientConnection second = waiting.takeFirst();
                    log.debug("GameSearcher {}, {}", first, second);
                    if (!PlayerService.canSearchGame(first)) {
                        log.info("Player cant play {}", first);
                        waiting.putFirst(second);
                        PlayerService.setPlayerStateNone(first);
                        continue;
                    }
                    if (!PlayerService.canSearchGame(second)) {
                        log.info("Player cant play {}", second);
                        PlayerService.setPlayerStateNone(second);
                        waiting.putFirst(first);
                        continue;
                    }
                    linkPlayersForGame(first, second);
                } catch (GameException e) {
                    log.error("GameSearcher", e);
                }
            }
        } catch (InterruptedException e) {
            log.info("GameSearcher closed");
        }
    }

    private void linkPlayersForGame(final ClientConnection first, final ClientConnection second) throws InterruptedException {
        requests.putLast(new TaskRequest(PlayerService.getConnectionById(first.getUser().getId()), CreateGameRequest.toDto(first.getUser(), second.getUser())));
    }

}
