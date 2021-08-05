package client.models.ai.neural;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;

public class BoardNeuralScore implements CalculateScore {

    @Override
    public double calculateScore(MLMethod network) {
        NeuralGame game = new NeuralGame((BasicNetwork) network);
        return 0;
    }

    @Override
    public boolean shouldMinimize() {
        return false;
    }

    @Override
    public boolean requireSingleThreaded() {
        return false;
    }
}
