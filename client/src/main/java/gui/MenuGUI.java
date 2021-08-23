package gui;

import client.ClientController;
import gui.listener.GameWindowListener;
import lombok.AllArgsConstructor;
import org.example.dto.request.player.GetInfoAboutUserRequest;
import org.example.dto.request.player.GetReplayGameRequest;
import org.example.dto.request.player.LogoutPlayerRequest;
import org.example.dto.request.player.WantPlayRequest;
import org.example.dto.request.room.CreateRoomRequest;
import org.example.dto.request.room.JoinRoomRequest;
import org.example.dto.response.player.PlayerResponse;
import org.example.dto.response.room.ListRoomResponse;
import org.example.dto.response.room.RoomResponse;
import org.example.models.ClientConnection;
import org.example.models.base.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

@AllArgsConstructor
public class MenuGUI extends JFrame {
    private final JButton createRoomButton;
    private final JButton searchGameButton;
    private final JMenuBar menuBar;
    private final JPanel roomHandlingPanel;
    private final JPanel roomsPanel;
    private final JPanel roomsList;
    private final JPanel playerPanel;
    private final JScrollPane scrool;
    private final JLabel roomsHeaderLabel;
    private final JLabel playerId;
    private final JLabel playerNickname;
    private final GUI gui;

    private ClientConnection connection;

    public MenuGUI(final GUI gui) {
        this.gui = gui;
        this.createRoomButton = new JButton("Создать комнату");
        this.searchGameButton = new JButton("Искать игру");
        this.roomHandlingPanel = new JPanel();
        this.menuBar = new JMenuBar();
        this.roomsPanel = new JPanel();
        this.roomsList = new JPanel();
        this.playerPanel = new JPanel();
        this.scrool = new JScrollPane(roomsList);
        this.roomsHeaderLabel = new JLabel();
        this.playerId = new JLabel();
        this.playerNickname = new JLabel();
        init();
        setLayoutManager();
        addActionEvent();
        addComponentsToJFrame();
        settingMenuBar();
        updateHeaderRooms(0);
    }

    private static PlayerColor colorSelection() {
        final Object[] possibilities = {"Хочу за белых", "Хочу за чёрных", "Любой цвет"};
        final int i = JOptionPane.showOptionDialog(null,
                "За какой цвет вы бы хотели играть?",
                "Создание комнаты",
                JOptionPane.PLAIN_MESSAGE,
                INFORMATION_MESSAGE,
                null,
                possibilities,
                0);
        return PlayerColor.valueOf(i);
    }

    public static void main(final String[] a) {
        final MenuGUI m = new MenuGUI(new Ui());
        m.setVisible(true);
        m.updateInfoPlayer(10, "Test");
        int id = 1;
        final ArrayList<RoomResponse> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final RoomResponse rs = new RoomResponse(id, new PlayerResponse("test " + id++), new PlayerResponse(null));
            list.add(rs);
        }

        for (int i = 0; i < 5; i++) {
            final RoomResponse rs = new RoomResponse(id, new PlayerResponse(null), new PlayerResponse("test " + id++));
            list.add(rs);
        }
        final ListRoomResponse listRoomResponse = new ListRoomResponse(new ArrayList<>());
        m.updateRooms(listRoomResponse);

    }

    public void initConnection(final ClientConnection connection) {
        this.connection = connection;
    }

    private void init() {
        setSize(700, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new GameWindowListener());
        setResizable(false);
        setVisible(false);
        scrool.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        scrool.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private String getNicknameForSearch() {
        return JOptionPane.showInputDialog(null, "Введите его никнейм", "Получить информацию об игроке", INFORMATION_MESSAGE);
    }

    private long getIdGameForSearch() {
        final String str = JOptionPane.showInputDialog(null, "Введите id игры", "Получить Replay", INFORMATION_MESSAGE);
        try {
            final long id = Long.parseLong(str);
            return id;
        } catch (final NumberFormatException e) {
            gui.createError("Не удалось считать id игры");
            return -1;
        }
    }

    private void settingMenuBar() {
        final JMenu miniMenu = new JMenu("Другое");
        final JMenuItem getInfo = new JMenuItem("Получить информацию об игроке");
        getInfo.addActionListener((e) -> {
            final String nickname = getNicknameForSearch();
            ClientController.safeSendRequest(connection, new GetInfoAboutUserRequest(nickname));
        });

        final JMenuItem getReplay = new JMenuItem("Получить Replay игры");
        getReplay.addActionListener((e) -> {
            final long id = getIdGameForSearch();
            if (id != -1) {
                ClientController.safeSendRequest(connection, new GetReplayGameRequest(id));
            }
        });

        final JMenuItem logOut = new JMenuItem("Выйти из аккаунта");
        logOut.addActionListener((e) -> {
            ClientController.safeSendRequest(connection, new LogoutPlayerRequest());
        });
        miniMenu.add(getInfo);
        miniMenu.add(getReplay);
        miniMenu.add(logOut);

        menuBar.add(miniMenu);
    }

    private void addActionEvent() {
        createRoomButton.addActionListener(actionEvent -> {
            final PlayerColor wantColor = colorSelection();
            ClientController.safeSendRequest(connection, new CreateRoomRequest(wantColor));
        });
        searchGameButton.addActionListener(actionEvent -> {
            ClientController.safeSendRequest(connection, new WantPlayRequest());
        });
    }

    private void setLayoutManager() {
        setLayout(new BorderLayout());
        roomHandlingPanel.setLayout(new GridLayout(4, 2, 30, 30));
        roomsPanel.setLayout(new BoxLayout(roomsPanel, BoxLayout.Y_AXIS));
        roomsList.setLayout(new BoxLayout(roomsList, BoxLayout.Y_AXIS));
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
    }

    private void addComponentsToJFrame() {
        roomHandlingPanel.add(createRoomButton);
        roomHandlingPanel.add(searchGameButton);
        playerPanel.add(playerId);
        playerPanel.add(playerNickname);
        roomHandlingPanel.add(playerPanel);

        roomsPanel.add(roomsHeaderLabel);
        roomsPanel.add(scrool);
        add(roomsPanel, BorderLayout.WEST);
        add(roomHandlingPanel, BorderLayout.EAST);
        setJMenuBar(menuBar);
    }

    private void updateHeaderRooms(final int n) {
        String roomsHeaderText = "Количество доступных комнат: " + n;
        if (n == 0) {
            roomsHeaderText = "Комнат не найдено.           ";
        }
        roomsHeaderLabel.setText(roomsHeaderText);

    }

    public void updateInfoPlayer(final long id, final String nickname) {
        playerId.setText(String.format("id: %d", id));
        playerNickname.setText(String.format("nickname: %s", nickname));
    }

    public void updateRooms(final ListRoomResponse listRoomResponse) {
        final Set<RoomResponse> set = new HashSet<>(listRoomResponse.getList());
        roomsList.removeAll();

        for (final RoomResponse room : set) {
            roomsList.add(getRoomItem(room));
        }
        updateHeaderRooms(set.size());
        roomsList.updateUI();
    }

    private JPanel getRoomItem(final RoomResponse room) {
        final JPanel panel = new JPanel();
        final JLabel roomTitleLabel = new JLabel();
        roomTitleLabel.setText("Комната " + room.getId());
        panel.add(roomTitleLabel);
        final JLabel roomBlackPlayerLabel = new JLabel();
        final JLabel roomWhitePlayerLabel = new JLabel();
        panel.setLayout(new GridLayout(1, 5, 0, 0));
        final PlayerResponse blackPlayer = room.getBlackPlayer();
        final PlayerResponse whitePlayer = room.getWhitePlayer();
        if (blackPlayer.getNickname() == null) {
            roomBlackPlayerLabel.setText("Черные: Свободно");
        } else {
            roomBlackPlayerLabel.setText("Черные: " + blackPlayer.getNickname());
        }
        if (whitePlayer.getNickname() == null) {
            roomWhitePlayerLabel.setText("Белые: Свободно");
        } else {
            roomWhitePlayerLabel.setText("Белые: " + whitePlayer.getNickname());
        }
        panel.add(roomBlackPlayerLabel);
        panel.add(roomWhitePlayerLabel);
        if (blackPlayer.getNickname() == null) {
            final JButton joinBlackButton = new JButton("Играть черным");
            joinBlackButton.addActionListener((e) -> {
                ClientController.safeSendRequest(connection, new JoinRoomRequest(room.getId()));
            });
            panel.add(joinBlackButton);
        }
        if (whitePlayer.getNickname() == null) {
            final JButton joinWhiteButton = new JButton("Играть белыми");
            joinWhiteButton.addActionListener((e) -> {
                ClientController.safeSendRequest(connection, new JoinRoomRequest(room.getId()));
            });
            panel.add(joinWhiteButton);
        }
        return panel;
    }
}
