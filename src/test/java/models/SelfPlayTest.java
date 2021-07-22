package models;

import exception.GameException;
import models.player.RandomBotPlayer;
import org.junit.jupiter.api.Test;
import services.SelfPlay;

class SelfPlayTest {

    @Test
    public void testGame() throws GameException {
        RandomBotPlayer bot1 = new RandomBotPlayer(0, "Bot0");
        RandomBotPlayer bot2 = new RandomBotPlayer(1, "Bot1");
        SelfPlay selfPlay = new SelfPlay(bot1, bot2);
        selfPlay.play();
    }

    @Test
    void test1kGames() throws GameException {
        for (int i = 3000; i < 4000; i++) {
            RandomBotPlayer bot1 = new RandomBotPlayer(0, "Bot0");
            RandomBotPlayer bot2 = new RandomBotPlayer(1, "Bot1");
            SelfPlay selfPlay = new SelfPlay(bot1, bot2);
            selfPlay.play();
            System.out.println(i);
        }
    }
}