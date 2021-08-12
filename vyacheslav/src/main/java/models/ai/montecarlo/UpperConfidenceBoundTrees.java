package models.ai.montecarlo;

import models.ai.montecarlo.tree.Node;

import java.util.Collections;
import java.util.Comparator;

//https://www.baeldung.com/java-monte-carlo-tree-search
public class UpperConfidenceBoundTrees {

    private UpperConfidenceBoundTrees() {
    }

    public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return (nodeWinScore / nodeVisit) + 1.41 * Math.sqrt(Math.log(totalVisit) / nodeVisit);
    }

    static Node findBestNodeWithUCT(Node node) {
        int parentVisit = node.getState().getVisitCount();
        return Collections.max(
                node.getChildArray(),
                Comparator.comparing(c -> uctValue(parentVisit, c.getState().getWinScore(), c.getState().getVisitCount())));
    }
}
