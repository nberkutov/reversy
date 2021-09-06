package services.command;

import base.Command;
import services.Server;

public class ClearDatabaseCommand implements Command {
    private final Server server;

    public ClearDatabaseCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute() {
        server.clearDataBase();
    }
}
