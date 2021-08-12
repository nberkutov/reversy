package services.command;

import base.Command;
import models.GameProperties;
import services.Server;

public class SavePlayersCommand implements Command {
    private final Server server;
    private final String path;

    public SavePlayersCommand(Server server, String path) {
        this.server = server;
        this.path = path;
    }

    @Override
    public void execute() {
        server.saveStatistic(path);
    }
}
