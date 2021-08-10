package models.ai.montecarlo;


import exception.ServerException;
import logic.BoardLogic;
import models.ai.montecarlo.tree.Node;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.List;

public class MonteCarloTreeSearch {
    private static final int WIN_SCORE = 10;
    private int time;
    private PlayerColor opponent;

    public MonteCarloTreeSearch(int time) {
        this.time = time;
    }

    private static boolean isAvailableMovesPlayer(final GameBoard board, final PlayerColor color) throws ServerException {
        return BoardLogic.getAvailableMoves(board, color).isEmpty();
    }

    private int getMillisSec() {
        return time * 1000;
    }

    public Point findNextMove(GameBoard board, PlayerColor playerNo) throws ServerException {
        long start = System.currentTimeMillis();
        long end = start + getMillisSec();

        opponent = playerNo.getOpponent();

        Node root = new Node(board, playerNo);

        root.getState().setBoard(board);
        root.getState().setColorMove(playerNo);

        while (System.currentTimeMillis() < end) {
            Node promisingNode = selectPromisingNode(root);

            if (isNotCriticalStateForGame(promisingNode.getState().getBoard())) {
                expandNode(promisingNode);
            }

            Node nodeToExplore = promisingNode;
            if (!promisingNode.getChildArray().isEmpty()) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            PlayerColor playoutResult = simulateRandomPlayout(nodeToExplore);

            backPropogation(nodeToExplore, playoutResult);
        }

        Node winnerNode = root.getChildWithMaxScore();

        return winnerNode.getState().getMove();
    }

    private boolean isNotCriticalStateForGame(final GameBoard board) {
        return BoardLogic.getCountEmpty(board) != 0
                && BoardLogic.getCountWhite(board) != 0
                && BoardLogic.getCountBlack(board) != 0;
    }

    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (!node.getChildArray().isEmpty()) {
            node = UpperConfidenceBoundTrees.findBestNodeWithUCT(node);
        }
        return node;
    }

    private void expandNode(Node node) throws ServerException {
        final List<State> possibleStates = node.getState().getAllPossibleStates();
        for (final State state : possibleStates) {
            Node newNode = new Node(state);
            newNode.setParent(node);
            newNode.getState().setColorMove(node.getState().getOpponent());
            node.getChildArray().add(newNode);
        }
    }

    private void backPropogation(Node nodeToExplore, PlayerColor playerNo) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            if (opponent != playerNo) {
                tempNode.getState().addScore(WIN_SCORE);
            }
            tempNode = tempNode.getParent();
        }
    }

    private PlayerColor simulateRandomPlayout(Node node) throws ServerException {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
        GameBoard board = tempState.getBoard();
        if (isPlayerLose(board, opponent.getOpponent())) {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return tempState.getOpponent();
        }
        while (!BoardLogic.isNotPossiblePlayOnBoard(board)) {
            if (!isAvailableMovesPlayer(board, tempState.getOpponent())) {
                tempState.togglePlayer();
                tempState.randomPlay();
                continue;
            }
            tempState.randomPlay();
        }

        if (isPlayerLose(board, opponent)) {
            return opponent;
        }
        return opponent.getOpponent();
    }

    private boolean isPlayerLose(GameBoard board, PlayerColor color) {
        if (color == PlayerColor.BLACK && board.getCountBlackCells() == 0) {
            return true;
        }
        if (color == PlayerColor.WHITE && board.getCountWhiteCells() == 0) {
            return true;
        }
        if (board.getCountEmpty() == 0) {
            if (color == PlayerColor.BLACK && board.getCountBlackCells() <= board.getCountWhiteCells()) {
                return true;
            }
            return color == PlayerColor.WHITE && board.getCountWhiteCells() < board.getCountBlackCells();
        }
        return false;
    }

}
