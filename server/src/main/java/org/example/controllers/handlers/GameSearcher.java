package org.example.controllers.handlers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controllers.GameController;
import org.example.controllers.PlayerController;
import org.example.exception.ServerException;
import org.example.models.player.UserConnection;
import org.example.services.GameService;
import org.example.services.PlayerService;
import org.example.services.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingDeque;


@Slf4j
@Component
@NoArgsConstructor

public class GameSearcher extends Thread {
    @Autowired
    private LinkedBlockingDeque<UserConnection> waiting;
    @Autowired
    private GameService gs;
    @Autowired
    private PlayerService ps;

    @Autowired
    private GameController gc;

    @Autowired
    private PlayerController pc;

    @Autowired
    private SenderService ss;

    @Override
    public void run() {
        log.info("GameSearcher started");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final UserConnection first = waiting.takeFirst();
                    final UserConnection second = waiting.takeFirst();
                    log.debug("GameSearcher {}, {}", first, second);
                    if (!pc.canPlay(first)) {
                        log.info("Player cant play {}", first);
                        waiting.putFirst(second);
                        continue;
                    }
                    if (!pc.canPlay((second))) {
                        log.info("Player cant play {}", second);
                        waiting.putFirst(first);
                        continue;
                    }
                    gc.actionCreateGame(first, second);
                } catch (final ServerException e) {
                    log.error("GameSearcher {}", e.getMessage());
                }
            }
        } catch (final InterruptedException e) {
            log.info("GameSearcher closed");
        }
    }
}
