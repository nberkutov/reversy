package gui;

import org.example.dto.response.player.PlayerResponse;

import javax.swing.*;
import java.awt.*;

public class PlayerGUI extends JFrame {
    private final JPanel mainPanel;
    private final JPanel playerPanel;
    private final JLabel nicknamePlayer;
    private final JLabel countGames;
    private final JLabel countWin;
    private final JLabel countLose;

    public PlayerGUI() {
        mainPanel = new JPanel();
        playerPanel = new JPanel();
        nicknamePlayer = new JLabel("Никнейм игрока: ");
        countGames = new JLabel("Всего игр: 0");
        countWin = new JLabel("Всего побед: 0");
        countLose = new JLabel("Всего поражений: 0");
        setLayoutManager();
        addComponentsToJFrame();
        init();
    }

    public static void main(final String[] a) {
        final PlayerGUI frame = new PlayerGUI();
        frame.initPlayer(new PlayerResponse("test", 1, 2, 3));
        frame.setVisible(true);
        final PlayerGUI frame2 = new PlayerGUI();
        frame2.initPlayer(new PlayerResponse("test", 1, 2, 3));
        frame2.setVisible(true);
    }

    private void init() {
        setTitle("Игрок");
        setPreferredSize(new Dimension(350, 200));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setLayoutManager() {
        mainPanel.setLayout(new BorderLayout());
        playerPanel.setLayout(new GridLayout(5, 2, 1, 1));
    }

    private void addComponentsToJFrame() {
        mainPanel.add(playerPanel, BorderLayout.CENTER);
        playerPanel.add(nicknamePlayer);
        playerPanel.add(countGames);
        playerPanel.add(countWin);
        playerPanel.add(countLose);
        add(mainPanel);
    }

    public void initPlayer(final PlayerResponse player) {
        setTitle(String.format("Игрок: %s", player.getNickname()));
        nicknamePlayer.setText(String.format("Никнейм игрока: %s", player.getNickname()));
        countGames.setText(String.format("Всего игр: %d", player.getTotalGames()));
        countWin.setText(String.format("Всего побед: %d", player.getWinGames()));
        countLose.setText(String.format("Всего поражений: %d", player.getLoseGames()));
    }
}
