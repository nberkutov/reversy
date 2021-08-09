import exception.ServerException;
import models.Player;
import models.base.PlayerColor;
import models.base.SmartBot;
import models.strategies.MinimaxStrategy;
import models.strategies.RandomStrategy;
import models.strategies.algorithms.HardAlgorithm;
import org.junit.jupiter.api.Test;


class SelfPlayTest {

    @Test
    public void testGame() throws ServerException {
        Player bot1 = new SmartBot("Bot0", new RandomStrategy());
        Player bot2 = new SmartBot("Bot1", new RandomStrategy());
        SelfPlay selfPlay = new SelfPlay(bot1, bot2);
        selfPlay.play();
    }

    @Test
    void test1kGamesWithStats() throws ServerException {
        int games = 300;
        Player bot1 = new SmartBot("minimax", new MinimaxStrategy(3, new HardAlgorithm()));
        Player bot2 = new SmartBot("random", new RandomStrategy());
        int win1 = 0;
        int win2 = 0;
        float maxTime = 0;
        for (int i = 0; i < games; i++) {
            long timeBefore = System.currentTimeMillis();
            SelfPlay selfPlay = new SelfPlay(bot1, bot2);
            PlayerColor result = selfPlay.play();

            long timeAfter = System.currentTimeMillis();
            float timeGame = timeAfter - timeBefore;
            maxTime = Math.max(maxTime, timeGame);
            Player winner = null;
            if (bot1.getColor() == result) {
                win1++;
                winner = bot1;
            } else {
                win2++;
                winner = bot2;
            }
            System.out.println(i + String.format(" Time on game %2.3f sec, win %s", timeGame / 1000, winner.getNickname()));
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
    void test1kGames() throws ServerException {
        for (int i = 3000; i < 4000; i++) {
            Player bot1 = new SmartBot("Bot0", new RandomStrategy());
            Player bot2 = new SmartBot("Bot1", new RandomStrategy());
            SelfPlay selfPlay = new SelfPlay(bot1, bot2);
            selfPlay.play();
            System.out.println(i);
        }
    }
}