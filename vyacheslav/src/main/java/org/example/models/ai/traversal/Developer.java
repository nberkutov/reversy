package org.example.models.ai.traversal;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.ai.traversal.tree.Node;
import org.example.models.ai.traversal.tree.SimGame;
import org.example.models.base.Cell;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;

import java.util.concurrent.BlockingDeque;

@AllArgsConstructor
public class Developer extends Thread {
    private final BlockingDeque<Task> tasks;
    private final BlockingDeque<Node> result;
    private final PlayerColor myColor;
    private final DeveloperOptions options;
    @Getter
    @Setter
    private boolean stopWork;
    @Getter
    private int maxDeath;

    public Developer(BlockingDeque<Task> tasks, BlockingDeque<Node> result, PlayerColor myColor, DeveloperOptions options) {
        this.tasks = tasks;
        this.result = result;
        this.myColor = myColor;
        this.options = options;
        stopWork = false;
        maxDeath = -1;
    }

    private static boolean canMove(final GameBoard board, final PlayerColor color) throws ServerException {
        return !BoardLogic.getAvailableMoves(board, color).isEmpty();
    }

    @Override
    public void run() {
        try {
            while (true) {
                final Task t = tasks.takeFirst();
                final Node n = t.getNode();
                if (n == null) {
                    break;
                }
                simulation(n);
                if (stopWork) {
                    result.putLast(n);
                    break;
                }
            }
        } catch (ServerException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        stopWork = true;
    }

    private void simulation(Node node) throws ServerException, InterruptedException {
        final SimGame state = node.getState();
        final PlayerColor color = state.getColorMove();
        final GameBoard board = state.getBoard();
        if (BoardLogic.isNotPossiblePlayOnBoard(board)) {
            state.setGameEnd(true);
        }

        if (node.getDeath() >= options.getMaxDeath() || state.isGameEnd()) {
            result.putLast(node);
            return;
        }

        for (final Point p : BoardLogic.getAvailableMoves(board, color)) {
            final GameBoard newB = board.clone();
            BoardLogic.makeMove(newB, p, Cell.valueOf(color));
            PlayerColor nextMoveColor = color.getOpponent();
            if (!canMove(newB, nextMoveColor)) {
                nextMoveColor = color;
            }
            final int score = BoardLogic.getCountCellByPlayerColor(board, myColor);
            final SimGame newState = new SimGame(newB, p, nextMoveColor, score, false);
            final Node newN = new Node(newState, node, node.getDeath() + 1);
            node.addChild(newN);
            addTask(newN);
        }
        maxDeath = Math.max(maxDeath, node.getDeath());
    }

    private void addTask(final Node node) throws InterruptedException {
        if (options.getOption() == TraversalEnum.DEEP) {
            tasks.putFirst(new Task(node));
        } else {
            tasks.putLast(new Task(node));
        }
    }

}
