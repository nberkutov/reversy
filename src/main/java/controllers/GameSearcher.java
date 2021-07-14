package controllers;

import dto.request.player.TaskRequest;
import dto.request.server.CreateGameRequest;
import dto.response.TaskResponse;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.player.Player;
import services.PlayerService;

import java.util.concurrent.BlockingQueue;

@Slf4j
@AllArgsConstructor
public class GameSearcher extends Thread {
    private final BlockingQueue<TaskRequest> requests;
    private final BlockingQueue<TaskResponse> responses;
    private final BlockingQueue<Player> waiting;

    @Override
    public void run() {
        log.debug("GameController started");
        while (true) {
            try {
                Player first = waiting.take();
                Player second = waiting.take();
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
                linkPlayers(first, second);
            } catch (InterruptedException | GameException e) {
                log.error("GameSearcher", e);
            }
        }
    }

    private void linkPlayers(final Player first, final Player second) throws InterruptedException {
        requests.put(new TaskRequest(first.getConnection(), CreateGameRequest.toDto(first, second)));
//        try {
//            Game game = GameService.createGame(first, second);
//            sendInfoAboutGame(game, game.getBlackPlayer());
//            sendInfoAboutGame(game, game.getWhitePlayer());
//            log.info("Game created, {}", game);
//        } catch (GameException e) {
//            addTaskResponse(first, ErrorResponse.toDto(e));
//            addTaskResponse(second, ErrorResponse.toDto(e));
//        }
    }

//    private void sendInfoAboutGame(final Game game, final Player player) throws InterruptedException {
//        addTaskResponse(player, SearchGameResponse.toDto(game, player));
//        addTaskResponse(player, GameBoardResponse.toDto(game));
//    }
//
//    public void addTaskResponse(final Player player, final GameResponse response) throws InterruptedException {
//        addTaskResponse(player.getConnection(), response);
//    }
//
//    private void addTaskResponse(final ClientConnection connection, final GameResponse response)
//            throws InterruptedException {
//        responses.put(new TaskResponse(connection, response));
//    }
}
