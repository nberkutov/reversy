import models.GameProperties;
import models.ServerCommand;
import services.Server;

import java.util.Scanner;

import static models.GameProperties.SERVER_FILE;

public class StartServer {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String path = SERVER_FILE;
        int port = GameProperties.PORT;
        Server server = Server.initServerFromFile(port, path);
        server.start();

        runCommands(server, path);
    }

    private static void runCommands(final Server server, final String path) {
        boolean work = server.isAlive();

        while (work) {
            String string = scanner.nextLine();
            ServerCommand command = ServerCommand.getCommandMessage(string);
            switch (command) {
                case HELP:
                    ServerCommand.printHelpInfo();
                    break;
                case SAVE_SERVER:
                    server.saveServerFile(path);
                    break;
                case UPLOAD_SERVER:
                    Server.uploadServerFile(path);
                    break;
                case SAVE_PLAYERS:
                    server.saveStatistic(GameProperties.PLAYERS_FILE);
                    break;
                case INFO_GAME:
                    server.getInfoGame();
                    break;
                case INFO_ROOM:
                    server.getInfoRoom();
                    break;
                case INFO_USER:
                    server.getInfoUser();
                    break;
                case KICK_ALL:
                    server.closeAllConnects();
                    break;
                case CLEAR:
                    server.clearDataBase();
                    break;
                case STOP:
                    server.close();
                    work = false;
                    break;
                default:
                    break;
            }
        }
    }
}
