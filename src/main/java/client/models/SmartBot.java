package client.models;


import client.models.ai.expectimax.Expectimax;
import client.models.ai.minimax.MiniMaxInfo;
import client.models.ai.minimax.MultiThreadMinimax;
import client.models.ai.minimax.OneThreadMinimax;
import client.models.ai.minimax.tree.Tree;
import client.models.ai.montecarlo.MonteCarloTreeSearch;
import client.models.ai.myai.MyBot;
import client.models.ai.traversal.HeaderThread;
import client.models.ai.traversal.TraversalEnum;
import client.models.strategies.SimpleStrategy;
import client.models.strategies.Strategy;
import exception.GameErrorCode;
import exception.GameException;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class SmartBot extends Player {
    private final int perem;
    private AiEnum ai;
    private final Strategy strategyPlayer;
    private final Strategy strategyBot;

    public SmartBot(String nickname, AiEnum ai, int perem, Strategy strategyPlayer) {
        super(nickname);
        this.ai = ai;
        this.perem = perem;
        this.strategyPlayer = strategyPlayer;
        strategyBot = new SimpleStrategy();
    }

    @Override
    public Point move(final GameBoard board) throws GameException {
        return getPointByAi(board);
    }

    private Point getPointByAi(final GameBoard board) throws GameException {
        switch (ai) {
            case MINIMAX:
                return getPointByMinimax(board);
            case MULTI_MINIMAX:
                return getPointByMultiMiniMax(board);
            case TRAVERSAL_DEEP:
                return getPointByTraversal(board, TraversalEnum.DEEP);
            case TRAVERSAL_WIDTH:
                return getPointByTraversal(board, TraversalEnum.WIDTH);
            case MONTE_CARLO:
                return getPointByMonteCarlo(board);
            case EXPECT_MAX:
                return getPointByExpectiMax(board);
            case MY_BOT:
            default:
                return getPointByMyBot(board);
        }
    }

    private Point getPointByTraversal(GameBoard board, TraversalEnum option) throws GameException {
        try {
            HeaderThread header = new HeaderThread(board, color, option);
            header.start();
            header.join();
            return header.getEndResult().getState().getMove();
        } catch (InterruptedException e) {
            throw new GameException(GameErrorCode.AI_ERROR);
        }
    }

    private Point getPointByMultiMiniMax(GameBoard board) {
        final int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        final ForkJoinPool forkJoinPool = new ForkJoinPool(numberOfProcessors);
        Tree branch = new Tree();
        MiniMaxInfo info = new MiniMaxInfo(branch, perem, color, strategyPlayer, strategyBot);
        MultiThreadMinimax minimax = new MultiThreadMinimax(board, color, null, 0, info);
        final ForkJoinTask<Integer> result = forkJoinPool.submit(minimax);
        result.join();
        return branch.getMove();
    }

    private Point getPointByMonteCarlo(GameBoard board) throws GameException {
        MonteCarloTreeSearch monte = new MonteCarloTreeSearch(perem);
        return monte.findNextMove(board, color);
    }

    private Point getPointByMyBot(GameBoard board) throws GameException {
        Tree branch = new Tree();
        MyBot myBot = new MyBot(perem, color, strategyPlayer, strategyBot);
        myBot.calculateMove(branch, board, null, 0, color);
        return branch.getMove();
    }

    private Point getPointByMinimax(final GameBoard board) throws GameException {
        Tree branch = new Tree();
        OneThreadMinimax oneThreadMinimax = new OneThreadMinimax(perem, color, strategyPlayer, strategyBot);
        oneThreadMinimax.minimax(branch, board, null, 0, color);
        return branch.getMove();
    }

    private Point getPointByExpectiMax(final GameBoard board) throws GameException {
        Tree branch = new Tree();
        Expectimax expectimax = new Expectimax(perem, color, strategyPlayer, strategyBot);
        expectimax.expectimax(branch, board, null, 0, color);
        return branch.getMove();
    }

    private Point getPointByRandom(final GameBoard board) throws GameException {
        List<Point> points = BoardService.getAvailableMoves(board, color);
        return points.get(new Random().nextInt(points.size()));
    }
}
