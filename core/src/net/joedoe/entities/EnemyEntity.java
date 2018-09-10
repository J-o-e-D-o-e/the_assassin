package net.joedoe.entities;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import lombok.Getter;
import lombok.Setter;
import net.joedoe.pathfinding.ManhattanHeuristic;
import net.joedoe.pathfinding.Node;

import static net.joedoe.utils.GameInfo.*;

@Getter
@Setter
public abstract class EnemyEntity implements MapEntity {
    protected String name;
    protected float x, y;
    protected int direction;
    protected DefaultGraphPath<Node> path = new DefaultGraphPath<>();
    protected ManhattanHeuristic heuristic = new ManhattanHeuristic();
    protected int pathIndex = 1;

    public void setDirection() {
        if (getNextNode().getY() * ONE_TILE > y)
            direction = 1; // N
        if (getNextNode().getX() * ONE_TILE < x)
            direction = 2; // W
        if (getNextNode().getY() * ONE_TILE < y)
            direction = 3; // S
        if (getNextNode().getX() * ONE_TILE > x)
            direction = 4; // E
    }

    public void move() {
        switch (direction) {
            case 1: // N
                pathIndex++;
                y += ONE_TILE;
                if (y > HEIGHT - ONE_TILE) {
                    y = HEIGHT - ONE_TILE;
                }
                break;
            case 2: // W
                pathIndex++;
                x -= ONE_TILE;
                if (x < 0) {
                    x = 0;
                }
                break;
            case 3: // S
                pathIndex++;
                y -= ONE_TILE;
                if (y < 0) {
                    y = 0;
                }
                break;
            case 4: // E
                pathIndex++;
                x += ONE_TILE;
                if (x > WIDTH - ONE_TILE) {
                    x = WIDTH - ONE_TILE;
                }
                break;
        }
    }

    public int calculateDistance() {
        return  path.getCount() - pathIndex;
    }

    Node getCurrentNode() {
        return path.nodes.get(pathIndex - 1);
    }

    Node getNextNode() {
        return path.nodes.get(pathIndex);
    }

    public int getPathCount() {
        return path.getCount();
    }
}
