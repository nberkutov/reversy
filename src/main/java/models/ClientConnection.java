package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.net.Socket;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientConnection {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public void send(String msg) throws IOException {
        out.write(msg);
        out.flush();
    }

    public void close() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
            in.close();
            out.close();
        }
    }
}
