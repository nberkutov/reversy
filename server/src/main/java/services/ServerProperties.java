package services;

import java.util.Optional;

public class ServerProperties {
    private int port;
    private int threads;
    private int numberOfGames;
    private String logPath;
    private String statsPath;


    private static ServerProperties instance;

    public ServerProperties() {
    }

    public ServerProperties(
            final int port,
            final int threads,
            final int numberOfGames,
            final String logPath,
            final String statsPath
    ) {
        this.port = port;
        this.threads = threads;
        this.logPath = logPath;
        this.statsPath = statsPath;
        this.numberOfGames = numberOfGames;
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

    public Optional<Integer> getNumberOfGames() {
        return Optional.of(numberOfGames);
    }

    public Optional<String> getStatsPath() {
        return Optional.of(statsPath);
    }

    public void setStatsPath(final String statsPath) {
        this.statsPath = statsPath;
    }

    @Override
    public String toString() {
        return "ServerProperties{" +
                "port=" + port +
                ", threads=" + threads +
                ", numberOfGames=" + numberOfGames +
                ", logPath='" + logPath + '\'' +
                ", statsPath='" + statsPath + '\'' +
                '}';
    }
}
