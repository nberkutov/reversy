package client.models;

import client.models.ai.expectimax.Expectimax;
import client.models.ai.minimax.MiniMaxInfo;
import client.models.ai.minimax.MultiThreadMinimax;
import client.models.ai.minimax.OneThreadMinimax;
import client.models.ai.minimax.tree.Tree;
import client.models.ai.montecarlo.MonteCarloTreeSearch;
import client.models.ai.myai.MyBot;
import client.models.ai.neural.Neural;
import client.models.ai.neural.NeuralGame;
import client.models.ai.traversal.HeaderThread;
import client.models.ai.traversal.TraversalEnum;
import client.models.strategies.SimpleStrategy;
import client.models.strategies.Strategy;
import exception.GameErrorCode;
import exception.GameException;
import models.GameProperties;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.neural.networks.BasicNetwork;
import services.BoardService;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class AiUtils {

    private AiUtils() {
    }

    private static final Strategy strategyBot = new SimpleStrategy();

    public static Point getPointByAi(final GameBoard board, final AiEnum ai, final PlayerColor color, final int perem, final Strategy strategyPlayer) throws GameException {
        switch (ai) {
            case NEURAL:
                return getPointByNeural(board, color);
            case MINIMAX:
                return getPointByMinimax(board, color, perem, strategyPlayer);
            case MULTI_MINIMAX:
                return getPointByMultiMiniMax(board, color, perem, strategyPlayer);
            case TRAVERSAL_DEEP:
                return getPointByTraversal(board, TraversalEnum.DEEP, color);
            case TRAVERSAL_WIDTH:
                return getPointByTraversal(board, TraversalEnum.WIDTH, color);
            case MONTE_CARLO:
                return getPointByMonteCarlo(board, color, perem);
            case EXPECT_MAX:
                return getPointByExpectiMax(board, color, perem, strategyPlayer);
            case MY_BOT:
                return getPointByMyBot(board, color, perem, strategyPlayer);
            default:
                return getPointByRandom(board, color);
        }
    }

    private static Point getPointByNeural(GameBoard board, PlayerColor color) throws GameException {
        final String path = GameProperties.NEURAL_FILE;
        final MLMethodGeneticAlgorithm train = Neural.loadOrCreate(board, color, path);
        Neural.training(train);
        Neural.save(train, path);
        final NeuralGame neuralGame = new NeuralGame((BasicNetwork) train.getMethod(), color, board);
        return neuralGame.getNeuralMove();
    }

    private static Point getPointByTraversal(GameBoard board, TraversalEnum option, final PlayerColor color) throws GameException {
        try {
            HeaderThread header = new HeaderThread(board, color, option);
            header.start();
            header.join();
            return header.getEndResult().getState().getMove();
        } catch (InterruptedException e) {
            throw new GameException(GameErrorCode.AI_ERROR);
        }
    }

    private static Point getPointByMultiMiniMax(GameBoard board, final PlayerColor color, final int maxDeath, final Strategy strategyPlayer) {
        final int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        final ForkJoinPool forkJoinPool = new ForkJoinPool(numberOfProcessors);
        Tree branch = new Tree();
        MiniMaxInfo info = new MiniMaxInfo(branch, maxDeath, color, strategyPlayer, strategyBot);
        MultiThreadMinimax minimax = new MultiThreadMinimax(board, color, null, 0, info);
        final ForkJoinTask<Integer> result = forkJoinPool.submit(minimax);
        result.join();
        return branch.getMove();
    }

    private static Point getPointByMonteCarlo(GameBoard board, final PlayerColor color, final int time) throws GameException {
        MonteCarloTreeSearch monte = new MonteCarloTreeSearch(time);
        return monte.findNextMove(board, color);
    }

    private static Point getPointByMyBot(GameBoard board, final PlayerColor color, final int maxDeath, final Strategy strategyPlayer) throws GameException {
        Tree branch = new Tree();
        MyBot myBot = new MyBot(maxDeath, color, strategyPlayer);
        myBot.calculateMove(branch, board, null, 0, color);
        return branch.getMove();
    }

    private static Point getPointByMinimax(final GameBoard board, final PlayerColor color, final int maxDeath, final Strategy strategyPlayer) throws GameException {
        Tree branch = new Tree();
        OneThreadMinimax oneThreadMinimax = new OneThreadMinimax(maxDeath, color, strategyPlayer, strategyBot);
        oneThreadMinimax.minimax(branch, board, null, 0, color);
        return branch.getMove();
    }

    private static Point getPointByExpectiMax(final GameBoard board, final PlayerColor color, final int maxDeath, final Strategy strategyPlayer) throws GameException {
        Tree branch = new Tree();
        Expectimax expectimax = new Expectimax(maxDeath, color, strategyPlayer, strategyBot);
        expectimax.expectimax(branch, board, null, 0, color);
        return branch.getMove();
    }

    private static Point getPointByRandom(final GameBoard board, final PlayerColor color) throws GameException {
        List<Point> points = BoardService.getAvailableMoves(board, color);
        return points.get(new Random().nextInt(points.size()));
    }
}
