package controllers.handlers;

import exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.game.Game;
import services.GameService;
import services.PlayerService;

import java.util.concurrent.LinkedBlockingDeque;


@Slf4j
@AllArgsConstructor
public class GameSearcher extends Thread {
    private final LinkedBlockingDeque<ClientConnection> waiting;

    public static void actionCreateGame(final ClientConnection firstConnection, final ClientConnection secondConnection) throws ServerException {
        final Game game = GameService.createGameBySearch(firstConnection, secondConnection);
        log.debug("Game created by search, {}", game);
    }

    @Override
    public void run() {
        log.info("GameSearcher started");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final ClientConnection first = waiting.takeFirst();
                    final ClientConnection second = waiting.takeFirst();
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
                    actionCreateGame(first, second);
                } catch (final ServerException e) {
                    log.error("GameSearcher {}", e.getMessage());
                }
            }
        } catch (final InterruptedException e) {
            log.info("GameSearcher closed");
        }
    }

}
