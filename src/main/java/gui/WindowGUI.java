package gui;

import exception.GameException;
import models.base.Cell;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Board;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;

import static models.GameProperties.BOARD_SIZE;

public class WindowGUI implements GameGUI {
    private final GameWindow gameWindow;

    public WindowGUI() {
        gameWindow = new GameWindow();
    }

    @Override
    public void updateGUI(GameBoard board, GameState gameState, String opponent) {
        gameWindow.updateGUI(board, gameState, opponent);
    }
}

class GameWindow extends JFrame {
    private static final Color BACKGROUND_COLOR = Color.decode("#187d47");
    private static final int CELL_SIZE = 50;
    private final JLabel stateInfoLabel;
    private final JLabel countBlackLabel;
    private final JLabel countWhiteLabel;
    private final JLabel opponentLabel;
    private final JPanel infoPanel;
    private final BoardPanel boardPanel;
    private GameBoard board;

    public GameWindow() {
        super("Reversi Client");
        board = new Board();
        int size = CELL_SIZE * BOARD_SIZE;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(size + 125, size + 50);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        stateInfoLabel = new JLabel();
        countBlackLabel = new JLabel();
        countWhiteLabel = new JLabel();
        opponentLabel = new JLabel();
        boardPanel = new BoardPanel(board);
        boardPanel.setBackground(BACKGROUND_COLOR);
        boardPanel.setBorder(new LineBorder(Color.BLACK));
        infoPanel = new JPanel();
        initLabels();
        add(infoPanel);
        add(boardPanel);
        setResizable(false);
        setVisible(true);
    }

    private void initLabels() {
        infoPanel.setMaximumSize(new Dimension(120, CELL_SIZE * BOARD_SIZE));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        stateInfoLabel.setText(" НАЧАЛО ИГРЫ");
        infoPanel.add(stateInfoLabel);

        countBlackLabel.setText(String.format(" ЧЕРНЫЕ: %d", board.getCountBlackCells()));
        infoPanel.add(countBlackLabel);

        countWhiteLabel.setText(String.format(" БЕЛЫЕ: %d", board.getCountWhiteCells()));
        infoPanel.add(countWhiteLabel);

        infoPanel.add(opponentLabel);
    }

    public void updateGUI(final GameBoard board, final GameState gameState, final String opponent) {
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
        opponentLabel.setText(String.format("Ваш противник: %s", opponent));
        boardPanel.update(board);
        repaint();

        if (gameState == GameState.END) {
            String message = "";
            if (board.getCountBlackCells() > board.getCountWhiteCells()) {
                message = "Победа черных";
            } else {
                message = "Победа белых";
            }
//            JOptionPane.showMessageDialog(new JFrame(), message);
        }
    }

    static class BoardPanel extends JPanel {
        private GameBoard board;
        private GameBoard prevBoard;
        private int mouseX;
        private int mouseY;

        public BoardPanel(final GameBoard board) {
            mouseX = -1;
            mouseY = -1;
            this.board = board;
            prevBoard = board;
            setSize(CELL_SIZE * 8, CELL_SIZE * 8);
            setMaximumSize(new Dimension(CELL_SIZE * 8, CELL_SIZE * 8));
            addMouseListener(new BoardMouseListener());
            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent mouseEvent) {
                }

                @Override
                public void mouseMoved(MouseEvent mouseEvent) {
                    mouseX = mouseEvent.getX();
                    mouseY = mouseEvent.getY();
                    repaint();
                }
            });
        }

        public void update(final GameBoard board) {
            prevBoard = this.board;
            this.board = board;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            float length = CELL_SIZE * BOARD_SIZE;

            g2.setColor(Color.BLACK);
            for (int i = 0; i < BOARD_SIZE + 1; i++) {
                float coordinate = i * CELL_SIZE;
                Line2D verticalLine =
                        new Line2D.Float(coordinate, 0, coordinate, length);
                Line2D horizontalLine =
                        new Line2D.Float(0, coordinate, length, coordinate);
                g2.draw(verticalLine);
                g2.draw(horizontalLine);
            }

            for (int y = 0; y < board.getSize(); y++) {
                for (int x = 0; x < board.getSize(); x++) {
                    try {
                        Cell prevCell = prevBoard.getCell(x, y);
                        Cell cell = board.getCell(x, y);
                        if (cell == Cell.EMPTY) {
                            continue;
                        }
                        if (prevCell == Cell.EMPTY) {
                            g2.setColor(Color.YELLOW);
                            g2.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                        if (cell == Cell.BLACK) {
                            g2.setColor(Color.BLACK);
                        }
                        if (cell == Cell.WHITE) {
                            g2.setColor(Color.WHITE);
                        }
                        g2.fillOval(
                                x * CELL_SIZE + (int) (CELL_SIZE * 0.125),
                                y * CELL_SIZE + (int) (CELL_SIZE * 0.125),
                                (int) (CELL_SIZE * 0.75),
                                (int) (CELL_SIZE * 0.75)
                        );
                        if (mouseX >= 0 && mouseY >= 0) {
                            g2.setColor(Color.YELLOW);
                            g2.drawRect(
                                    mouseX / CELL_SIZE * CELL_SIZE,
                                    mouseY / CELL_SIZE * CELL_SIZE,
                                    CELL_SIZE, CELL_SIZE
                            );
                            mouseX = -1;
                            mouseY = -1;
                        }
                    } catch (GameException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        class BoardMouseListener implements MouseListener {

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                mouseX = mouseEvent.getX();
                mouseY = mouseEvent.getY();
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        }
    }
}