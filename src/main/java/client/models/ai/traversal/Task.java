package client.models.ai.traversal;

import client.models.ai.traversal.tree.Node;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Task {
    private Node node;

    public static Task create(Node node) {
        return new Task(node);
    }
}
