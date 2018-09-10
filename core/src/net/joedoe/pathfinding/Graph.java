package net.joedoe.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import lombok.Getter;

import static net.joedoe.utils.GameInfo.MAP_WIDTH;
import static net.joedoe.utils.GameInfo.ONE_TILE;

@Getter
public class Graph implements IndexedGraph<Node> {
    private Array<Node> nodes;

    Graph(Array<Node> nodes) {
        this.nodes = nodes;
    }

    public Node getNodeByCoordinates(float x, float y) {
        try {
            return nodes.get((int) x / ONE_TILE + MAP_WIDTH * (int) y / ONE_TILE);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public int getIndex(Node node) {
        return node.getIndex();
    }

    @Override
    public Array<Connection<Node>> getConnections(Node fromNode) {
        return fromNode.getConnections();
    }

    @Override
    public int getNodeCount() {
        return nodes.size;
    }
}