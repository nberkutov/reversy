package models.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.game.GameResult;

import java.util.concurrent.ConcurrentSkipListSet;

@Data
@AllArgsConstructor
public class Statistics {
    private final ConcurrentSkipListSet<GameResult> games;
    private int win;
    private int lose;
    private int playBlackColor;
    private int playWhiteColor;

    public Statistics() {
        win = 0;
        lose = 0;
        playBlackColor = 0;
        playWhiteColor = 0;
        games = new ConcurrentSkipListSet<>();
    }

    public void incrementWin() {
        win++;
    }

    public void addGameResult(GameResult gameResult) {
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
                "win=" + win +
                ", lose=" + lose +
                ", playBlackColor=" + playBlackColor +
                ", playWhiteColor=" + playWhiteColor +
                '}';
    }
}
