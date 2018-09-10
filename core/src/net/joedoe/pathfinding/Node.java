package net.joedoe.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.utils.Array;
import lombok.Getter;

@Getter
public class Node implements Connection<Node> {
    private final int x, y;
    private int index;
    private Array<Connection<Node>> connections;

    Node(int x, int y, int index) {
        this.index = index;
        this.x = x;
        this.y = y;
        connections = new Array<>();
    }

    void addConnection(Node node) {
        if (node != null) {
            connections.add(new DefaultConnection<>(this, node));
        }
    }

    @SuppressWarnings("unused")
    @Override
    public String toString() {
        return "\"" + index + "\" @ " + x + "/" + y;
    }

    @Override
    public float getCost() {
        return 0;
    }

    @Override
    public Node getFromNode() {
        return null;
    }

    @Override
    public Node getToNode() {
        return null;
    }
}