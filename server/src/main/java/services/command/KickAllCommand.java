package services.command;

import base.Command;
import services.Server;

public class KickAllCommand implements Command {
    private final Server server;

    public KickAllCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute() {
        server.closeAllConnects();
    }
}
