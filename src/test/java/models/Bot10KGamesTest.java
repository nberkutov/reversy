package models;

import exception.GameException;
import models.player.RandomBotPlayer;
import org.junit.jupiter.api.Test;
import services.SelfPlay;

class Bot10KGamesTest {

    @Test
    public void testGame() throws GameException {
        RandomBotPlayer bot1 = new RandomBotPlayer(0);
        RandomBotPlayer bot2 = new RandomBotPlayer(1);
        SelfPlay selfPlay = new SelfPlay(bot1,bot2);
        selfPlay.play();
    }

    @Test
    void test10kGames() throws GameException {
        for (int i = 3000; i < 6000; i++) {
            RandomBotPlayer bot1 = new RandomBotPlayer(0);
            RandomBotPlayer bot2 = new RandomBotPlayer(1);
            SelfPlay selfPlay = new SelfPlay(bot1,bot2);
            selfPlay.play();
            System.out.println(i);
        }
    }
}