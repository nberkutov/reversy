package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.player.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientConnection implements AutoCloseable, Serializable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Player player;

    private final Lock lock = new ReentrantLock();

    public ClientConnection(final Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public String readMsg() throws IOException {
        return in.readUTF();
    }

    public void send(final String msg) throws IOException {
        lock.lock();
        out.writeUTF(msg);
        out.flush();
        lock.unlock();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientConnection that = (ClientConnection) o;
        return Objects.equals(socket, that.socket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket);
    }

}
