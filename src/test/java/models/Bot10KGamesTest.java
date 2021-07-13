package models;

import exception.GameException;
import models.player.RandomBot;
import org.junit.jupiter.api.Test;
import services.SelfPlay;

class Bot10KGamesTest {

    @Test
    public void testGame() throws GameException {
        RandomBot bot1 = new RandomBot(0);
        RandomBot bot2 = new RandomBot(1);
        SelfPlay selfPlay = new SelfPlay(bot1,bot2);
        selfPlay.play();
    }

    @Test
    void test10kGames() throws GameException {
        for (int i = 3000; i < 6000; i++) {
            RandomBot bot1 = new RandomBot(0);
            RandomBot bot2 = new RandomBot(1);
            SelfPlay selfPlay = new SelfPlay(bot1,bot2);
            selfPlay.play();
            System.out.println(i);
        }
    }
}