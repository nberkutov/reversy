package models.ai.traversal;

import exception.GameErrorCode;
import exception.ServerException;
import lombok.Getter;
import lombok.SneakyThrows;
import models.ai.traversal.tree.Node;
import models.ai.traversal.tree.SimGame;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class HeaderThread extends Thread {
    private final List<Developer> developers;
    private final BlockingDeque<Task> tasks;
    private final BlockingDeque<Node> results;
    private final DeveloperOptions options;
    @Getter
    private final Node root;
    @Getter
    private Node endResult;


    public HeaderThread(GameBoard board, PlayerColor color, DeveloperOptions options) {
        developers = new ArrayList<>();
        tasks = new LinkedBlockingDeque<>();
        results = new LinkedBlockingDeque<>();
        int countThreads = 5;
        for (int i = 0; i < countThreads; i++) {
            developers.add(new Developer(tasks, results, color, options));
        }
        root = new Node(board, color);
        this.options = options;
    }

    public HeaderThread(GameBoard board, PlayerColor color, TraversalEnum option) {
        this(board, color, option, 2);
    }

    public HeaderThread(GameBoard board, PlayerColor color, TraversalEnum option, int time) {
        this(board, color, new DeveloperOptions(option, 100, time));
    }

    private static Node getNodeAfterRoot(Node root, Node result) {
        Node tmp = result;

        while (!root.equals(tmp.getParent())) {
            tmp = tmp.getParent();
        }
        return tmp;
    }

    private void autoCloser() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                int minDeathInDev = 100;
                for (Developer dev : developers) {
                    minDeathInDev = Math.min(minDeathInDev, dev.getMaxDeath());
                }
                options.setMaxDeath(minDeathInDev);
                closeDevelopers();
            }
        };
        timer.schedule(timerTask, getMillisSec());
    }

    private void closeDevelopers() throws InterruptedException {
        for (Developer dev : developers) {
            dev.setStopWork(true);
            tasks.putFirst(Task.create(null));
        }
    }

    private long getMillisSec() {
        return options.getSecWork() * 1000L;
    }

    @SneakyThrows
    @Override
    public void run() {
        tasks.add(Task.create(root));
        for (Developer d : developers) {
            d.start();
        }
        Node maxGoodNode = null;

        autoCloser();

        try {
            while (true) {
                final Node tmpNode = results.takeFirst();
                final SimGame simGame = tmpNode.getState();

                if (maxGoodNode == null || simGame.getScore() > maxGoodNode.getState().getScore()) {
                    maxGoodNode = tmpNode;
                    if (simGame.isGameEnd()) {
                        break;
                    }
                }
                boolean finish = false;
                for (Developer dev : developers) {
                    if (dev.isStopWork()) {
                        finish = true;
                        break;
                    }
                }
                if (finish) {
                    break;
                }
            }
        } catch (InterruptedException ignore) {
            throw new ServerException(GameErrorCode.AI_ERROR);
        } finally {
            closeDevelopers();
        }

        endResult = getNodeAfterRoot(root, maxGoodNode);

        for (Developer dev : developers) {
            dev.join();
        }
    }
}
