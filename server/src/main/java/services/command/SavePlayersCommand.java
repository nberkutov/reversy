package services.command;

import base.Command;
import lombok.extern.slf4j.Slf4j;
import models.GameProperties;
import services.Server;

import java.io.IOException;

@Slf4j
public class SavePlayersCommand implements Command {
    private final Server server;
    private final String path;

    public SavePlayersCommand(final Server server, final String path) {
        this.server = server;
        this.path = path;
    }

    @Override
    public void execute() {
        try {
            server.saveStatistic();
        } catch (final IOException e) {
            log.error("Statistics saving failed.");
        }
    }
}
