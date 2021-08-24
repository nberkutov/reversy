package client;

import java.util.Optional;

public class ClientProperties {
    private String host;
    private int port;
    private String botType;
    private String guiType;
    private String playerColor;
    private String nickname;
    private String logPath;
    private String logFile;
    private int numberOfGames;
    private int depth;
    private long windowDelay;

    public ClientProperties() {
    }

    public Optional<String> getBotType() {
        return Optional.of(botType);
    }

    public void setBotType(final String botType) {
        this.botType = botType;
    }

    public Optional<String> getGuiType() {
        return Optional.of(guiType);
    }

    public void setGuiType(final String guiType) {
        this.guiType = guiType;
    }

    public Optional<String> getPlayerColor() {
        return Optional.of(playerColor);
    }

    public void setPlayerColor(final String playerColor) {
        this.playerColor = playerColor;
    }

    public Optional<String> getHost() {
        return Optional.of(host);
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public Optional<Integer> getPort() {
        return Optional.of(port);
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public Optional<Integer> getNumberOfGames() {
        return Optional.of(numberOfGames);
    }

    public void setNumberOfGames(final int numberOfGames) {
        this.numberOfGames = numberOfGames;
    }

    public Optional<String> getLogPath() {
        return Optional.of(logPath);
    }

    public void setLogPath(final String logPath) {
        this.logPath = logPath;
    }

    public Optional<String> getLogFile() {
        return Optional.of(logFile);
    }

    public void setLogFile(final String logFile) {
        this.logFile = logFile;
    }

    public Optional<Integer> getDepth() {
        return Optional.of(depth);
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Optional<Long> getWindowDelay() {
        return Optional.of(windowDelay);
    }

    public void setWindowDelay(long windowDelay) {
        this.windowDelay = windowDelay;
    }
}
