package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.player.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ClientConnection implements AutoCloseable, Delayed {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Player player;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void initPlayer(Player player) {
        this.player = player;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void send(String msg) throws IOException {
        out.writeUTF(msg);
        out.flush();
    }

    @Override
    public void close() {
        if (!socket.isClosed()) {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return 0;
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }
}
