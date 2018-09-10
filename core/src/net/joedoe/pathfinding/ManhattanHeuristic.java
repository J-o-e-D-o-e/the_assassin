package net.joedoe.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

import static net.joedoe.utils.GameInfo.MAP_WIDTH;

public class ManhattanHeuristic implements Heuristic<Node> {

    @Override
    public float estimate(Node startNode, Node endNode) {
        int startY = startNode.getIndex() / MAP_WIDTH;
        int startX = startNode.getIndex() % MAP_WIDTH;
        int endY = endNode.getIndex() / MAP_WIDTH;
        int endX = endNode.getIndex() % MAP_WIDTH;
        return Math.abs(startX - endX) + Math.abs(startY - endY);
    }
}