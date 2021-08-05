package models;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ServerCommand {
    HELP("help", "Get help about the available commands"),
    SAVE_SERVER("save server", "save database in file"),
    UPLOAD_SERVER("upload server", "upload database from file"),
    SAVE_PLAYERS("save players", "save players in CSV file"),
    CLEAR("clear", "clear all in database"),
    INFO_USER("user", "get info about any user"),
    INFO_GAME("game", "get info about any game"),
    INFO_ROOM("room", "get info about any room"),
    KICK_ALL("kick all", "kick all from server"),
    STOP("stop", "stop server working"),
    NONE("none", "none");

    private final String commandName;
    private final String description;

    ServerCommand(String commandName, String description) {
        this.commandName = commandName;
        this.description = description;
    }

    public static ServerCommand getCommandMessage(final String message) {
        for (final ServerCommand command : values()) {
            if (command.equalCommand(message)) {
                return command;
            }
        }

        System.out.println(String.format("Command [%s] not found, write '%s'", message, HELP.commandName));
        return NONE;
    }

    public static void printHelpInfo() {
        StringBuilder help = new StringBuilder();
        for (final ServerCommand command : values()) {
            if (command == NONE) {
                continue;
            }
            help.append(command.commandName);
            help.append(" - ");
            help.append(command.description);
            help.append("\n");
        }
        System.out.println(help.toString().trim());
    }

    boolean equalCommand(final String message) {
        return commandName.equals(message);
    }
}
