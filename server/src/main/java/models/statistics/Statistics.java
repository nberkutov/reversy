package models.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.game.GameResult;
import models.player.User;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Data
@AllArgsConstructor
public class Statistics implements Serializable {
    private final ConcurrentSkipListSet<GameResult> games;
    private final ConcurrentHashMap<String, Integer> winsAgainst;
    private int totalGames;
    private int win;
    private int lose;
    private int playBlackColor;
    private int playWhiteColor;

    public Statistics() {
        totalGames = 0;
        win = 0;
        lose = 0;
        playBlackColor = 0;
        playWhiteColor = 0;
        games = new ConcurrentSkipListSet<>();
        winsAgainst = new ConcurrentHashMap<>();
    }

    public void incrementPlayerAgainst(final User user) {
        final String nickname = user.getNickname();
        Integer wins = winsAgainst.get(nickname);
        if (wins == null) {
            wins = 0;
        }
        winsAgainst.put(nickname, ++wins);
    }

    public void incrementCountGames() {
        totalGames++;
    }

    public void incrementWin() {
        win++;
    }

    public void addGameResult(final GameResult gameResult) {
        games.add(gameResult);
    }

    public void incrementLose() {
        lose++;
    }

    public void incrementPlayBlack() {
        playBlackColor++;
    }

    public void incrementPlayWhite() {
        playWhiteColor++;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "gamesCount=" + totalGames +
                ", win=" + win +
                ", lose=" + lose +
                ", playBlackColor=" + playBlackColor +
                ", playWhiteColor=" + playWhiteColor +
                '}';
    }
}
