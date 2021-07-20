package client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StartClient {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8081;

    public static void main(String[] args) {
        try {
            List<Thread> list = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                Client client = new Client(new Socket(IP, PORT));
                Thread thread = new Thread(client);
                thread.start();
                list.add(thread);
            }

            for (Thread thread : list) {
                thread.join();
            }
        } catch (InterruptedException | IOException e) {
            log.error("ERROR", e);
        }
    }

}
