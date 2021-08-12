package services.command;

import base.Command;
import services.Server;

public class GetGameInfoCommand implements Command {
    private final Server server;

    public GetGameInfoCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute() {
        server.getGameInfo();
    }
}
