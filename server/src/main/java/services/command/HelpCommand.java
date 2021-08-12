package services.command;

import base.Command;
import models.ServerCommand;

public class HelpCommand implements Command {
    @Override
    public void execute() {
        ServerCommand.printHelpInfo();
    }
}
