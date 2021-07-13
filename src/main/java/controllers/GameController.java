package controllers;

import dto.response.CreateGameResponse;
import dto.response.GameBoardResponse;
import dto.response.GameResponse;
import dto.response.TaskResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ClientConnection;
import models.Game;
import models.Player;
import models.base.PlayerState;
import services.GameService;

import java.util.concurrent.BlockingQueue;

@Slf4j
@AllArgsConstructor
public class GameController extends Thread {
    private final BlockingQueue<TaskResponse> responses;
    private final BlockingQueue<Player> waiting;

    @Override
    public void run() {
        log.debug("GameController started");
        while (true) {
            try {
                Player first = waiting.take();
                Player second = waiting.take();
                if (!isCanPlay(first)) {
                    waiting.put(second);
                    first.setState(PlayerState.NONE);
                    continue;
                }
                if (!isCanPlay(second)) {
                    second.setState(PlayerState.NONE);
                    waiting.put(first);
                    continue;
                }
                linkPlayers(first, second);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void linkPlayers(Player first, Player second) throws InterruptedException {
        Game game = GameService.createGame(first, second);

        setStatePlaying(game.getBlackPlayer());
        sendInfoAboutGame(game, game.getBlackPlayer());

        setStatePlaying(game.getWhitePlayer());
        sendInfoAboutGame(game, game.getWhitePlayer());
    }

    private void setStatePlaying(Player player) {
        player.setState(PlayerState.PLAYING);
    }

    private void sendInfoAboutGame(Game game, Player player) throws InterruptedException {
        addTaskResponse(player, CreateGameResponse.toDto(game, player));
        addTaskResponse(player, GameBoardResponse.toDto(game));
    }

    public void addTaskResponse(Player player, GameResponse response) throws InterruptedException {
        addTaskResponse(player.getConnection(), response);
    }

    private void addTaskResponse(ClientConnection connection, GameResponse response) throws InterruptedException {
        responses.put(new TaskResponse(connection, response));
    }

    private boolean isCanPlay(Player player) {
        return player.getConnection() != null
                && player.getConnection().isConnected();
    }
}
