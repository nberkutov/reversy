package services;

import java.util.Optional;

public class ServerProperties {
    private int port;
    private int numberOfGames;
    private String logPath;
    private String statsPath;
    private String servicesLogFileName;
    private String controllersLogFileName;

    public ServerProperties() {
    }

    public ServerProperties(
            final int port,
            final int numberOfGames,
            final String logPath,
            final String statsPath
    ) {
        this.port = port;
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

    public Optional<String> getServicesLogFileName() {
        return Optional.of(servicesLogFileName);
    }

    public void setServicesLogFileName(final String servicesLogFileName) {
        this.servicesLogFileName = servicesLogFileName;
    }

    public Optional<String> getControllersLogFileName() {
        return Optional.of(controllersLogFileName);
    }

    public void setControllersLogFileName(final String controllersLogFileName) {
        this.controllersLogFileName = controllersLogFileName;
    }

    @Override
    public String toString() {
        return "ServerProperties{" +
                "port=" + port +
                ", numberOfGames=" + numberOfGames +
                ", logPath='" + logPath + '\'' +
                ", statsPath='" + statsPath + '\'' +
                ", servicesLogFileName='" + servicesLogFileName + '\'' +
                ", controllersLogFileName='" + controllersLogFileName + '\'' +
                '}';
    }
}
