package services.command;

import base.Command;
import services.Server;

public class UploadServerCommand implements Command {
    private final String path;

    public UploadServerCommand(String path) {
        this.path = path;
    }


    @Override
    public void execute() {
        //Server.uploadServerFile(path);
    }
}
