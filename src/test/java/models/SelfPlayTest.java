package models;

import exception.GameException;
import models.player.RandomBot;
import org.junit.jupiter.api.Test;
import services.SelfPlay;

class SelfPlayTest {

    @Test
    public void testGame() throws GameException {
        RandomBot bot1 = new RandomBot(0, "Bot0");
        RandomBot bot2 = new RandomBot(1, "Bot1");
        SelfPlay selfPlay = new SelfPlay(bot1, bot2);
        selfPlay.play();
    }

    @Test
    void test1kGames() throws GameException {
        for (int i = 3000; i < 4000; i++) {
            RandomBot bot1 = new RandomBot(0, "Bot0");
            RandomBot bot2 = new RandomBot(1, "Bot1");
            SelfPlay selfPlay = new SelfPlay(bot1, bot2);
            selfPlay.play();
            System.out.println(i);
        }
    }
}