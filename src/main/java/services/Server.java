package services;

import controllers.GameController;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 8081;
    private final GameController controller;

    public Server() {
        controller = new GameController();
    }

    public void Start() throws IOException {
        try (final ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                final Socket socket = serverSocket.accept();
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            controller.addConnect(socket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
        } catch (final BindException e) {
            e.printStackTrace();
        }
    }

    public void Close() {

    }
}
