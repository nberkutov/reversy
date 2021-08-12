package models.ai.montecarlo;

import exception.ServerException;
import logic.BoardLogic;
import lombok.Data;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.ArrayList;
import java.util.List;

@Data
public class State {
    private GameBoard board;
    private Point move;
    private PlayerColor colorMove;
    private int visitCount;
    private double winScore;

    public State(final GameBoard board, final PlayerColor color) {
        this.board = board;
        this.colorMove = color;
    }

    public State(State state) {
        this.board = state.getBoard().clone();
        this.move = state.getMove();
        this.colorMove = state.getColorMove();
        this.visitCount = state.getVisitCount();
        this.winScore = state.getWinScore();
    }

    public State(final GameBoard board, final Point move) {
        this.board = board.clone();
        this.move = move;
    }


    PlayerColor getOpponent() throws ServerException {
        return colorMove.getOpponent();
    }

    public List<State> getAllPossibleStates() throws ServerException {
        final List<State> possibleStates = new ArrayList<>();
        final List<Point> availablePositions = BoardLogic.getAvailableMoves(board, colorMove);
        for (final Point p : availablePositions) {
            final State newState = new State(board, p);
            newState.setColorMove(colorMove);
            BoardLogic.makeMove(newState.getBoard(), p, Cell.valueOf(newState.getColorMove()));
            possibleStates.add(newState);
        }

        return possibleStates;
    }

    void incrementVisit() {
        this.visitCount++;
    }

    void addScore(final double score) {
        if (this.winScore != Integer.MIN_VALUE)
            this.winScore += score;
    }

    void randomPlay() throws ServerException {
        final List<Point> availablePositions = BoardLogic.getAvailableMoves(board, colorMove);
        final int totalPossibilities = availablePositions.size();
        final int selectRandom = (int) (Math.random() * totalPossibilities);
        BoardLogic.makeMove(board, availablePositions.get(selectRandom), Cell.valueOf(colorMove));
    }

    void togglePlayer() throws ServerException {
        this.colorMove = colorMove.getOpponent();
    }

    @Override
    public String toString() {
        return "State{" +
                "move=" + move +
                ", playerNo=" + colorMove +
                ", visitCount=" + visitCount +
                ", winScore=" + winScore +
                '}';
    }
}
