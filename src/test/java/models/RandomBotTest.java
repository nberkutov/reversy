package models;

import exception.GameException;
import models.base.PlayerColor;
import org.junit.jupiter.api.Test;
import services.SelfPlay;

import static org.junit.jupiter.api.Assertions.*;

class RandomBotTest {

    @Test
    public void testGame() throws GameException {
        RandomBot bot1 = new RandomBot(0);
        RandomBot bot2 = new RandomBot(1);
        SelfPlay selfPlay = new SelfPlay(bot1,bot2);
    }
}