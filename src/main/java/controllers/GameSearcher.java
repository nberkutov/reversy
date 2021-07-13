package controllers;

import dto.response.*;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.game.Game;
import models.player.Player;
import services.GameService;
import services.PlayerService;

import java.util.concurrent.BlockingQueue;

@Slf4j
@AllArgsConstructor
public class GameSearcher extends Thread {
    private final BlockingQueue<TaskResponse> responses;
    private final BlockingQueue<Player> waiting;

    @Override
    public void run() {
        log.debug("GameController started");
        while (true) {
            try {
                Player first = waiting.take();
                Player second = waiting.take();
                if (!PlayerService.isCanPlay(first)) {
                    waiting.put(second);
                    PlayerService.setPlayerStateNone(first);
                    continue;
                }
                if (!PlayerService.isCanPlay(second)) {
                    PlayerService.setPlayerStateNone(second);
                    waiting.put(first);
                    continue;
                }
                linkPlayers(first, second);
            } catch (InterruptedException | GameException e) {
                e.printStackTrace();
            }
        }
    }

    private void linkPlayers(final Player first, final Player second) throws InterruptedException {
        try {
            Game game = GameService.createGame(first, second);
            sendInfoAboutGame(game, game.getBlackPlayer());
            sendInfoAboutGame(game, game.getWhitePlayer());
        } catch (GameException e) {
            addTaskResponse(first, ErrorResponse.toDto(e));
            addTaskResponse(second, ErrorResponse.toDto(e));
        }
    }

    private void sendInfoAboutGame(final Game game, final Player player) throws InterruptedException {
        addTaskResponse(player, CreateGameResponse.toDto(game, player));
        addTaskResponse(player, GameBoardResponse.toDto(game));
    }

    public void addTaskResponse(final Player player, final GameResponse response) throws InterruptedException {
        addTaskResponse(player.getConnection(), response);
    }

    private void addTaskResponse(final ClientConnection connection, final GameResponse response)
            throws InterruptedException {
        responses.put(new TaskResponse(connection, response));
    }
}
