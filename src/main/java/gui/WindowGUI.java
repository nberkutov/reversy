package gui;

import exception.GameException;
import models.base.Cell;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Board;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

import static models.GameProperties.BOARD_SIZE;

public class WindowGUI extends JFrame implements GameGUI {
    private static final Color BACKGROUND_COLOR = Color.decode("#187d47");
    private static final int CELL_SIZE = 50;
    private static final int OFFSET = 50;
    private final JLabel stateInfoLabel;
    private final JLabel countBlackLabel;
    private final JLabel countWhiteLabel;
    private GameBoard board;

    public WindowGUI() {
        super("Reversi Client");
        board = new Board();
        int xSize = CELL_SIZE * BOARD_SIZE + 2 * OFFSET;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(xSize, xSize);
        setBackground(Color.GREEN);
        setLocationRelativeTo(null);
        setLayout(null);
        stateInfoLabel = new JLabel();
        stateInfoLabel.setBounds(OFFSET, 0, 200, 30);
        stateInfoLabel.setText("НАЧАЛО ИГРЫ");

        countBlackLabel = new JLabel();
        countBlackLabel.setBounds(xSize - OFFSET - 200, 0, xSize - OFFSET - 120, 30);
        countBlackLabel.setText(String.format("ЧЕРНЫЕ: %d", board.getCountBlackCells()));

        countWhiteLabel = new JLabel();
        countWhiteLabel.setBounds(xSize - OFFSET - 80, 0, xSize - OFFSET, 30);
        countWhiteLabel.setText(String.format("БЕЛЫЕ: %d", board.getCountWhiteCells()));

        add(stateInfoLabel);
        add(countBlackLabel);
        add(countWhiteLabel);

        setVisible(true);
    }

    public void updateGUI(GameBoard board, GameState gameState) {
        String stateText;
        switch (gameState) {
            case BLACK_MOVE:
                stateText = "ХОД ЧЕРНЫХ";
                break;
            case WHITE_MOVE:
                stateText = "ХОД БЕЛЫХ";
                break;
            case END:
                stateText = "КОНЕЦ ИГРЫ";
                break;
            default:
                throw new NotImplementedException();
        }
        stateInfoLabel.setText(stateText);
        this.board = board;
        countBlackLabel.setText(String.format("ЧЕРНЫЕ: %d", board.getCountBlackCells()));
        countWhiteLabel.setText(String.format("БЕЛЫЕ: %d", board.getCountWhiteCells()));
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        float length = CELL_SIZE * BOARD_SIZE;

        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(OFFSET, OFFSET, (int) (length), (int) (length));
        g2.setColor(Color.BLACK);
        for (int i = 0; i < BOARD_SIZE + 1; i++) {
            float coordinate = i * CELL_SIZE;
            Line2D verticalLine =
                    new Line2D.Float(coordinate + OFFSET, OFFSET, coordinate + OFFSET, length + OFFSET);
            Line2D horizontalLine =
                    new Line2D.Float(OFFSET, coordinate + OFFSET, length + OFFSET, coordinate + OFFSET);
            g2.draw(verticalLine);
            g2.draw(horizontalLine);
        }

        for (int y = 0; y < board.getSize(); y++) {
            for (int x = 0; x < board.getSize(); x++) {
                try {
                    Cell cell = board.getCell(x, y);
                    if (cell == Cell.EMPTY) {
                        continue;
                    }
                    if (cell == Cell.BLACK) {
                        g2.setColor(Color.BLACK);
                    }
                    if (cell == Cell.WHITE) {
                        g2.setColor(Color.WHITE);
                    }
                    g2.fillOval(
                            x * CELL_SIZE + OFFSET + (int) (CELL_SIZE * 0.125),
                            y * CELL_SIZE + OFFSET + (int) (CELL_SIZE * 0.125),
                            (int) (CELL_SIZE * 0.75),
                            (int) (CELL_SIZE * 0.75)
                    );
                } catch (GameException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
