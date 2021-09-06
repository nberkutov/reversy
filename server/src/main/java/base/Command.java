package base;

import models.GameProperties;
import models.ServerCommand;
import services.Server;
import services.command.*;

public interface Command {
    void execute();

    static Command parse(final String commandString, final Server server, final String path) {
        final ServerCommand commandType = ServerCommand.getCommandMessage(commandString);
        final Command command;
        switch (commandType) {
            case HELP:
                command = new HelpCommand();
                break;
            case SAVE_SERVER:
                command = new SaveServerCommand(server, path);
                break;
            case UPLOAD_SERVER:
                command = new UploadServerCommand(path);
                break;
            case SAVE_PLAYERS:
                command = new SavePlayersCommand(server, GameProperties.PLAYERS_FILE);
                break;
            case INFO_GAME:
                command = new GetGameInfoCommand(server);
                break;
            case INFO_ROOM:
                command = new GetRoomInfoCommand(server);
                break;
            case INFO_USER:
                command = new GetUserInfoCommand(server);
                break;
            case KICK_ALL:
                command = new KickAllCommand(server);
                break;
            case CLEAR:
                command = new ClearDatabaseCommand(server);
                break;
            case STOP:
                command = new StopServerCommand(server);
                break;
            default:
                command = () -> {};
        }
        return command;
    }
}
