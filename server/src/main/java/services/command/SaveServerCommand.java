package services.command;

import base.Command;
import services.Server;

public class SaveServerCommand implements Command {
    private final Server server;
    private final String path;

    public SaveServerCommand(Server server, String path) {
        this.server = server;
        this.path = path;
    }

    @Override
    public void execute() {
        server.saveServerFile(path);
    }
}
