package net.joedoe.pathfinding;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

import static net.joedoe.utils.GameInfo.MAP_HEIGHT;
import static net.joedoe.utils.GameInfo.MAP_WIDTH;

public class GraphGenerator {
    public static Graph generateGraph(TiledMap map) {
        Array<Node> nodes = createArray();
        return new Graph(addConnections(map, nodes));
    }

    private static Array<Node> createArray() {
        Array<Node> nodes = new Array<>();
        int index = 0;
        for (int y = 0; y < MAP_HEIGHT; y++)
            for (int x = 0; x < MAP_WIDTH; x++)
                nodes.add(new Node(x, y, index++));
        return nodes;
    }

    private static Array<Node> addConnections(TiledMap map, Array<Node> nodes) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("middle");
        for (int y = 0; y < MAP_HEIGHT; y++)
            for (int x = 0; x < MAP_WIDTH; x++)
                if (layer.getCell(x, y) == null) { // current cell is accessible
                    Node currentNode = nodes.get(x + MAP_WIDTH * y);
                    if (layer.getCell(x, (y + 1)) == null && y != MAP_HEIGHT - 1)  // UP
                        currentNode.addConnection(nodes.get(x + MAP_WIDTH * (y + 1)));
                    if (layer.getCell((x - 1), y) == null && x != 0)  // LEFT
                        currentNode.addConnection(nodes.get((x - 1) + MAP_WIDTH * y));
                    if (layer.getCell(x, (y - 1)) == null && y != 0) // DOWN
                        currentNode.addConnection(nodes.get(x + MAP_WIDTH * (y - 1)));
                    if (layer.getCell((x + 1), y) == null && x != MAP_WIDTH - 1) // SOUTH
                        currentNode.addConnection(nodes.get((x + 1) + MAP_WIDTH * y));
                }
        return nodes;
    }
}
