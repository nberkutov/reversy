package services.command;

import base.Command;
import services.Server;

public class GetRoomInfoCommand implements Command {
    private final Server server;

    public GetRoomInfoCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute() {
        server.getInfoRoom();
    }
}
