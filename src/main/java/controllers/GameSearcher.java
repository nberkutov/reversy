package controllers;

import dto.request.player.TaskRequest;
import dto.request.server.CreateGameRequest;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import services.PlayerService;

import java.util.concurrent.BlockingQueue;

@Slf4j
@AllArgsConstructor
public class GameSearcher extends Thread {
    private final BlockingQueue<TaskRequest> requests;
    private final BlockingQueue<ClientConnection> waiting;

    @Override
    public void run() {
        log.debug("GameController started");
        while (true) {
            try {
                ClientConnection first = waiting.take();
                ClientConnection second = waiting.take();
                log.debug("GameSearcher {}, {}", first, second);
                if (!PlayerService.isCanPlay(first)) {
                    log.info("Player cant play {}", first);
                    waiting.put(second);
                    PlayerService.setPlayerStateNone(first);
                    continue;
                }
                if (!PlayerService.isCanPlay(second)) {
                    log.info("Player cant play {}", second);
                    PlayerService.setPlayerStateNone(second);
                    waiting.put(first);
                    continue;
                }
                linkPlayersForGame(first, second);
            } catch (InterruptedException | GameException e) {
                log.error("GameSearcher", e);
            }
        }
    }

    private void linkPlayersForGame(final ClientConnection first, final ClientConnection second) throws InterruptedException, GameException {
        requests.put(new TaskRequest(PlayerService.getConnectionById(first.getPlayer().getId()), CreateGameRequest.toDto(first.getPlayer(), second.getPlayer())));
    }

}
