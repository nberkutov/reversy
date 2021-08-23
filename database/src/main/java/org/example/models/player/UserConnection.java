package org.example.models.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@AllArgsConstructor
@Slf4j
public class UserConnection implements AutoCloseable, Serializable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Lock lock = new ReentrantLock();
    private long userId;

    public UserConnection(final Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        userId = -1;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public String readMsg() throws IOException {
        return in.readUTF();
    }

    public void send(final String msg) {
        try {
            lock.lock();
            out.writeUTF(msg);
            out.flush();
        } catch (final IOException e) {
            log.warn("Can't send {}, {}, {}", msg, e.getMessage(), socket);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        if (!socket.isClosed()) {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (final IOException e) {
                log.warn("Can't close connection {} {}", e.getMessage(), socket);
            }
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UserConnection that = (UserConnection) o;
        return Objects.equals(socket, that.socket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket);
    }

    @Override
    public String toString() {
        return "ClientConnection{" +
                "socket=" + socket +
                ", user=" + userId +
                '}';
    }
}
