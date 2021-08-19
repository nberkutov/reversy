package services.command;

import base.Command;
import services.Server;

public class StopServerCommand implements Command {
    private final Server server;

    public StopServerCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute() {
        server.close();
    }
}
