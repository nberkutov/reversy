package neural;

import models.ai.neural.BoardNeuralScore;
import models.ai.neural.Neural;
import models.base.PlayerColor;
import models.board.Board;
import org.encog.Encog;
import org.encog.neural.neat.NEATPopulation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class NeuralTest {
    @TempDir
    static Path sharedTempDir;
    private static String neuralTestFile;

    @BeforeAll
    private static void initFileNeural() {
        neuralTestFile = sharedTempDir.resolve("neuralTest.eg").toString();
    }

    @Test
    void testTrain() {
        final Board board = new Board();
        final PlayerColor color = PlayerColor.BLACK;
        final BoardNeuralScore score = new BoardNeuralScore(color, board);
        final NEATPopulation pop = Neural.createNetwork();
        assertNull(pop.getBestGenome());
        Neural.training(pop, score);

        assertNotNull(pop.getBestGenome());

        Encog.getInstance().shutdown();
    }

    @Test
    void testTrainSaveAndLoad() {
        final Board board = new Board();
        final PlayerColor color = PlayerColor.BLACK;
        final BoardNeuralScore score = new BoardNeuralScore(color, board);
        final NEATPopulation pop = Neural.createNetwork();

        assertNull(pop.getBestGenome());
        Neural.training(pop, score);
        assertNotNull(pop.getBestGenome());

        Neural.save(pop, neuralTestFile);

        final NEATPopulation loaded = Neural.loadOrCreate(neuralTestFile);
        assertNotNull(loaded.getBestGenome());

        Encog.getInstance().shutdown();
    }
}
