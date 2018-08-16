package net.joedoe.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import lombok.Getter;
import net.joedoe.utils.GameInfo;

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
        if (node != null) { // Make sure there is a neighboring node
            connections.add(new DefaultConnection<>(this, node));
        }
    }

    public void renderFill(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(x * GameInfo.ONE_TILE, y * GameInfo.ONE_TILE, GameInfo.ONE_TILE, GameInfo.ONE_TILE);
    }

    public void renderLine(ShapeRenderer shapeRenderer) {
        int x = this.x * GameInfo.ONE_TILE;
        int y = this.y * GameInfo.ONE_TILE;
        shapeRenderer.line(x, y, x, y + GameInfo.ONE_TILE);
        shapeRenderer.line(x + GameInfo.ONE_TILE, y, x + GameInfo.ONE_TILE, y + GameInfo.ONE_TILE);
        shapeRenderer.line(x, y, x + GameInfo.ONE_TILE, y);
        shapeRenderer.line(x, y + GameInfo.ONE_TILE, x + GameInfo.ONE_TILE, y + GameInfo.ONE_TILE);
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