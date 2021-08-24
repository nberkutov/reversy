package org.example.controllers.handlers;

import lombok.extern.slf4j.Slf4j;
import org.example.controllers.GameController;
import org.example.controllers.PlayerController;
import org.example.exception.ServerException;
import org.example.models.player.UserConnection;
import org.example.services.GameService;
import org.example.services.PlayerService;
import org.example.services.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingDeque;

@Component
@Slf4j
public class AutoLogoutHandler extends Thread {
    @Autowired
    @Qualifier("logouts")
    private LinkedBlockingDeque<UserConnection> logouts;

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
        log.info("GameLogout started");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final UserConnection first = logouts.takeFirst();
                    pc.actionAutoLogoutPlayer(first);
                } catch (final ServerException e) {
                    log.warn("action AutoLogout player {}", e.getMessage());
                }
            }
        } catch (final InterruptedException e) {
            log.info("GameSearcher closed");
        }
    }
}
