package net.joedoe.entities;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lombok.Getter;
import lombok.Setter;
import net.joedoe.pathfinding.Graph;
import net.joedoe.pathfinding.ManhattanHeuristic;
import net.joedoe.pathfinding.Node;
import net.joedoe.utils.GameInfo;

@Getter
@Setter
public abstract class EnemyEntity implements MapEntity {
    protected Texture texture;
    protected String name;
    protected float x, y;
    protected int direction;
    protected Graph graph;
    protected DefaultGraphPath<Node> path;
    protected IndexedAStarPathFinder<Node> pathfinder;
    protected ManhattanHeuristic heuristic;
    public int pathIndex = 1;

    public void setDirection() {
        if (getNextNode().getY() * GameInfo.ONE_TILE > y)
            direction = 1; // N
        if (getNextNode().getX() * GameInfo.ONE_TILE < x)
            direction = 2; // W
        if (getNextNode().getY() * GameInfo.ONE_TILE < y)
            direction = 3; // S
        if (getNextNode().getX() * GameInfo.ONE_TILE > x)
            direction = 4; // E
    }

    @SuppressWarnings({"unused", "Duplicates"})
    public void move() {
        switch (direction) {
            case 1: // N
                pathIndex++;
                y += GameInfo.ONE_TILE;
                if (y > GameInfo.HEIGHT - GameInfo.ONE_TILE) {
                    y = GameInfo.HEIGHT - GameInfo.ONE_TILE;
                }
                break;
            case 2: // W
                pathIndex++;
                x -= GameInfo.ONE_TILE;
                if (x < 0) {
                    x = 0;
                }
                break;
            case 3: // S
                pathIndex++;
                y -= GameInfo.ONE_TILE;
                if (y < 0) {
                    y = 0;
                }
                break;
            case 4: // O
                pathIndex++;
                x += GameInfo.ONE_TILE;
                if (x > GameInfo.WIDTH - GameInfo.ONE_TILE) {
                    x = GameInfo.WIDTH - GameInfo.ONE_TILE;
                }
                break;
        }
    }

    public int calculateDistance() {
        int distance = 0;
        for (int i = pathIndex - 1; i < path.getCount() - 1; i++) {
            distance++;
        }
        return distance;
    }

    @SuppressWarnings("Duplicates")
    public boolean calculatePath(Graph graph, float endX, float endY) {
        this.graph = graph;
        pathfinder = new IndexedAStarPathFinder<>(graph, true);
        pathIndex = 1;
        path.clear();
        Node startNode = graph.getNodeByCoordinates(x, y);
        Node endNode = graph.getNodeByCoordinates(endX, endY);
        return pathfinder.searchNodePath(startNode, endNode, heuristic, path);
    }


    Node getCurrentNode() {
        return path.nodes.get(pathIndex - 1);
    }

    Node getNextNode() {
        return path.nodes.get(pathIndex);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, GameInfo.ONE_TILE, GameInfo.ONE_TILE);
    }
}
