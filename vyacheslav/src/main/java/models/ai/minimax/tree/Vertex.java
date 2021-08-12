package models.ai.minimax.tree;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vertex implements Comparable {
    private final int depth;
    private final float score;

    @Override
    public String toString() {
        return "Vertex{" +
                "depth=" + depth +
                ", score=" + score +
                '}';
    }

    @Override
    public int compareTo(final Object o) {
        final Vertex other = (Vertex) o;
        if (getDepth() == other.getDepth()) {
            return Float.compare(other.getScore(), getScore());
        }
        return Float.compare(getDepth(), other.getDepth());
    }
}
