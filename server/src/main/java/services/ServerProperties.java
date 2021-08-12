package services;

import java.util.Optional;

public class ServerProperties {
    private int port;
    private int threads;
    private String logPath;
    private String logbackPath;
    private int numberOfGames;

    public ServerProperties() {
    }

    public ServerProperties(final int port, final int threads, final String logPath, final String logbackPath) {
        this.port = port;
        this.threads = threads;
        this.logPath = logPath;
        this.logbackPath = logbackPath;
    }

    public Optional<Integer> getPort() {
        return Optional.of(port);
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public Optional<Integer> getThreads() {
        return Optional.of(threads);
    }

    public void setThreads(final int threads) {
        this.threads = threads;
    }

    public Optional<String> getLogPath() {
        return Optional.of(logPath);
    }

    public void setLogPath(final String logPath) {
        this.logPath = logPath;
    }

    public Optional<String> getLogbackPath() {
        return Optional.of(logbackPath);
    }

    public void setLogbackPath(final String logbackPath) {
        this.logbackPath = logbackPath;
    }

    public Optional<Integer> getNumberOfGames() {
        return Optional.of(numberOfGames);
    }

    public void setNumberOfGames(final int numberOfGames) {
        this.numberOfGames = numberOfGames;
    }

    @Override
    public String toString() {
        return "ServerProperties{" +
                "port=" + port +
                ", threads=" + threads +
                ", logPath='" + logPath + '\'' +
                ", logbackPath='" + logbackPath + '\'' +
                '}';
    }
}
