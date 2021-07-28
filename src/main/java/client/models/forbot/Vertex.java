package client.models.forbot;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vertex implements Comparable {
    private int depth;
    private int score;

    @Override
    public String toString() {
        return "Vertex{" +
                "depth=" + depth +
                ", score=" + score +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        Vertex other = (Vertex) o;
        if (getDepth() == other.getDepth()) {
            return Integer.compare(other.getScore(), getScore());
        }
        return Integer.compare(getDepth(), other.getDepth());
    }
}
