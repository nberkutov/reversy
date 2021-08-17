package client;

import java.util.Optional;

public class ClientProperties {
    private String host;
    private int port;
    private String botType;
    private String guiType;
    private String playerColor;
    private String nickname;

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
}
