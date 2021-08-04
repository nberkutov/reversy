package client.models.ai.traversal;

import client.models.ai.traversal.tree.Node;
import client.models.ai.traversal.tree.SimGame;
import client.models.ai.traversal.tree.Tree;
import lombok.Getter;
import lombok.SneakyThrows;
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
    private final int countThreads;
    private final GameBoard startBoard;
    private final PlayerColor myColor;
    private final DeveloperOptions options;
    @Getter
    private Node endResult;


    public HeaderThread(GameBoard board, PlayerColor color, DeveloperOptions options) {
        developers = new ArrayList<>();
        tasks = new LinkedBlockingDeque<>();
        results = new LinkedBlockingDeque<>();
        countThreads = 5;
        for (int i = 0; i < countThreads; i++) {
            developers.add(new Developer(tasks, results, color, options));
        }
        startBoard = board;
        myColor = color;
        this.options = options;
    }

    public HeaderThread(GameBoard board, PlayerColor color, TraversalEnum option) {
        this(board, color, new DeveloperOptions(option, 100, 2));
    }

    @SneakyThrows
    @Override
    public void run() {
        Tree tree = new Tree(startBoard, myColor);

        tasks.add(Task.create(tree.getRoot()));
        for (Developer d : developers) {
            d.start();
        }
        Node maxGoodNode = null;

        autoCloser();

        try {
            while (true) {
                Node tmpNode = results.takeFirst();
                SimGame simGame = tmpNode.getState();
                GameBoard tmp = tmpNode.getState().getBoard();
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
        } catch (InterruptedException e) {
//            throw new GameException(GameErrorCode.AI_ERROR);
        } finally {
            closeDevelopers();
        }

        endResult = getNodeAfterRoot(tree, maxGoodNode);

        for (Developer dev : developers) {
            dev.join();
        }
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

    private Node getNodeAfterRoot(Tree tree, Node result) {
        Node root = tree.getRoot();
        Node tmp = result;

        while (!root.equals(tmp.getParent())) {
            tmp = tmp.getParent();
        }
        return tmp;
    }
}
