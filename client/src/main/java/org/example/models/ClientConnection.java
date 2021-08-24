package org.example.models;


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
public class ClientConnection implements AutoCloseable, Serializable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private final Lock write = new ReentrantLock();
    private final Lock read = new ReentrantLock();

    public ClientConnection(final Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public String readMsg() throws IOException {
        try {
            read.lock();
            return in.readUTF();
        } finally {
            read.unlock();
        }
    }

    public void send(final String msg) {
        try {
            write.lock();
            out.writeUTF(msg);
            out.flush();
        } catch (final IOException e) {
            log.warn("Can't send {}, {}, {}", msg, e.getMessage(), socket);
        } finally {
            write.unlock();
        }
    }


    @Override
    public void close() {
        try {
            socket.close();
            in.close();
            out.close();
        } catch (final IOException e) {
            log.warn("Can't close connection {}", e.getMessage());
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ClientConnection that = (ClientConnection) o;
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
                '}';
    }
}
