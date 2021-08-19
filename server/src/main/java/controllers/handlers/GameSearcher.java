package controllers.handlers;

import controllers.TaskRequest;
import dto.response.ErrorResponse;
import exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.game.Game;
import services.GameService;
import services.PlayerService;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

import static controllers.GameController.sendInfoAboutGame;
import static controllers.handlers.TasksHandler.sendResponse;

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
                    linkPlayersForGame(first, second);
                } catch (final ServerException e) {
                    log.error("GameSearcher {}", e.getMessage());
                }
            }
        } catch (final InterruptedException e) {
            log.info("GameSearcher closed");
        }
    }

    private void linkPlayersForGame(final ClientConnection first, final ClientConnection second) throws InterruptedException, ServerException {
        try {
            final Game game = GameService.createGameBySearch(first, second);
            sendInfoAboutGame(game, game.getBlackUser());
            sendInfoAboutGame(game, game.getWhiteUser());
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final ServerException e) {
            try {
                sendResponse(first, ErrorResponse.toDto(e));
                sendResponse(second, ErrorResponse.toDto(e));
            } catch (final IOException io) {
                log.error("GameSearcher {}", io.getMessage());
            }
        }
    }

}
