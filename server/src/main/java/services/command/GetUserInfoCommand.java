package services.command;

import base.Command;
import services.Server;

public class GetUserInfoCommand implements Command {
    private final Server server;

    public GetUserInfoCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute() {
        server.getInfoUser();
    }
}
