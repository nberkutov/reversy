package client.models;


import client.models.forbot.OneThreadMinimax;
import client.models.forbot.Tree;
import client.models.strategies.SimpleStrategy;
import client.models.strategies.Strategy;
import exception.GameException;
import models.base.interfaces.GameBoard;
import models.board.Point;

public class SmartBot extends Player {
    private final int depth;
    private final Strategy strategyPlayer;
    private final Strategy strategyBot;

    public SmartBot(final String nickname) {
        this(nickname, 3);
    }

    public SmartBot(final String nickname, final int depth) {
        this(nickname, depth, new SimpleStrategy());
    }

    public SmartBot(String nickname, int depth, Strategy strategyPlayer) {
        super(nickname);
        this.depth = depth;
        this.strategyPlayer = strategyPlayer;
        this.strategyBot = new SimpleStrategy();
    }

    @Override
    public Point move(final GameBoard board) throws GameException {

        Tree branch = new Tree();
        OneThreadMinimax oneThreadMinimax = new OneThreadMinimax(depth, color, strategyPlayer, strategyBot);
        oneThreadMinimax.minimax(branch, board, null, 0, color);

//        final int numberOfProcessors = Runtime.getRuntime().availableProcessors();
//        final ForkJoinPool forkJoinPool = new ForkJoinPool(numberOfProcessors);
//        Tree branch2 = new Tree();
//        MiniMaxInfo info = new MiniMaxInfo(branch2, depth, color, strategyPlayer, strategyBot);
//        final ForkJoinTask<Integer> result = forkJoinPool.submit(new MultiThreadMinimax(board, color, null, 0, info));
//        result.join();

        return branch.getMove();
    }

}
