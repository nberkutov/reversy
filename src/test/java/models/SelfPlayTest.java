package models;

import client.models.Player;
import client.models.RandomBotPlayer;
import client.models.SmartBot;
import client.models.strategies.HardStrategy;
import exception.GameException;
import models.game.GameResult;
import org.junit.jupiter.api.Test;
import services.SelfPlay;

class SelfPlayTest {

    @Test
    public void testGame() throws GameException {
        RandomBotPlayer bot1 = new RandomBotPlayer("Bot0");
        RandomBotPlayer bot2 = new RandomBotPlayer("Bot1");
        SelfPlay selfPlay = new SelfPlay(bot1, bot2);
        selfPlay.play();
    }

    @Test
    void test1kGamesWithStats() throws GameException {
        int games = 100;
        Player bot1 = new SmartBot("minimax", 3, new HardStrategy());
        Player bot2 = new RandomBotPlayer("Random");
        int win1 = 0;
        int win2 = 0;
        float maxTime = 0;
        for (int i = 0; i < games; i++) {
            long timeBefore = System.currentTimeMillis();
            SelfPlay selfPlay = new SelfPlay(bot2, bot1);
            GameResult result = selfPlay.play();
            if (result.getWinner().getNickname().equals(bot1.getNickname())) {
                win1++;
            } else {
                win2++;
            }
            long timeAfter = System.currentTimeMillis();
            float timeGame = timeAfter - timeBefore;
            maxTime = Math.max(maxTime, timeGame);
            System.out.println(i + String.format(" Time on game %2.3f sec, win %s", timeGame / 1000, result.getWinner().getNickname()));
        }
        System.out.println(String.format("%s win %d; %s win %d", bot1.getNickname(), win1, bot2.getNickname(), win2));
        System.out.println(String.format("Percent win: %2.0f%s", getPercent(win1, games), "%"));
        System.out.println(String.format("Max time on game: %f sec", maxTime / 1000));
    }

    private float getPercent(int win, int games) {
        float percent = (float) win / games;
        return percent * 100;
    }

    @Test
    void test1kGames() throws GameException {
        for (int i = 3000; i < 4000; i++) {
            RandomBotPlayer bot1 = new RandomBotPlayer("Bot0");
            RandomBotPlayer bot2 = new RandomBotPlayer("Bot1");
            SelfPlay selfPlay = new SelfPlay(bot1, bot2);
            selfPlay.play();
            System.out.println(i);
        }
    }
}