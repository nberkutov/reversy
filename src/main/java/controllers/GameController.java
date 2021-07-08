package controllers;

import com.google.gson.Gson;
import dto.request.player.CreatePlayerRequest;
import dto.request.player.GameRequest;
import dto.request.player.MovePlayerRequest;
import services.GameService;
import services.PlayerService;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

public class GameController extends Thread {
    private final Gson gson = new Gson();
    private final BlockingQueue<GameRequest> requests = new DelayQueue<GameRequest>();
    private final PlayerService playerService = new PlayerService();
    private final GameService gameService = new GameService();

    @Override
    public void run() {
        while (true) {
            try {
                MovePlayerRequest movePlayerRequest = (MovePlayerRequest) requests.take();
                gameService.moveFromPlayer(movePlayerRequest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addConnect(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        CreatePlayerRequest createPlayerRequest = gson.fromJson(in, CreatePlayerRequest.class);


    }
}
