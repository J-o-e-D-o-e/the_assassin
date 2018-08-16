package net.joedoe.pathfinding;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import net.joedoe.entities.MapEntity;
import net.joedoe.utils.GameInfo;

import java.util.ArrayList;

public class GraphGenerator {
    private Array<Node> nodes = new Array<>();
    private TiledMapTileLayer layer;
    @SuppressWarnings("WeakerAccess")
    public static int mapWidth, mapHeight;

    public GraphGenerator(TiledMap map) {
        int index = 0;
        mapWidth = map.getProperties().get("width", Integer.class); // 40
        mapHeight = map.getProperties().get("height", Integer.class); // 25
        layer = (TiledMapTileLayer) map.getLayers().get("middle");
        for (int y = 0; y < mapHeight; y++) { //build empty nodes array
            for (int x = 0; x < mapWidth; x++) {
                nodes.add(new Node(x, y, index++));
            }
        }
    }

    public Graph generateGraph(MapEntity currentEntity, ArrayList<? extends MapEntity> entities) {
        Array<Node> otherEntities = getOtherEntities(currentEntity, entities);
        generateGraph(otherEntities);
        return new Graph(nodes);
    }

    private Array<Node> getOtherEntities(MapEntity currentEntity, ArrayList<? extends MapEntity> entities) {
        Array<Node> otherEntity = new Array<>();
        for (MapEntity enemy : entities) {
            if (enemy != currentEntity) {
                int index = (int) enemy.getX() / GameInfo.ONE_TILE + mapWidth * (int) enemy.getY() / GameInfo.ONE_TILE;
                otherEntity.add(nodes.get(index));
            }
        }
        return otherEntity;
    }

    private void generateGraph(Array<Node> nodes) {
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                if (layer.getCell(x, y) == null) { // current cell is accessible
                    Node currentNode = this.nodes.get(x + mapWidth * y);
                    if (layer.getCell(x, (y + 1)) == null && y != mapHeight - 1)  // N
                        currentNode.addConnection(this.nodes.get(x + mapWidth * (y + 1)));
                    if (layer.getCell((x - 1), y) == null && x != 0)  // W
                        currentNode.addConnection(this.nodes.get((x - 1) + mapWidth * y));
                    if (layer.getCell(x, (y - 1)) == null && y != 0) // S
                        currentNode.addConnection(this.nodes.get(x + mapWidth * (y - 1)));
                    if (layer.getCell((x + 1), y) == null && x != mapWidth - 1) // O
                        currentNode.addConnection(this.nodes.get((x + 1) + mapWidth * y));
                    if (nodes.size != 0)
                        nodes.forEach(node -> {
                            if (currentNode.getIndex() == node.getIndex()) currentNode.getConnections().clear();
                        });
                }
            }
        }
    }
}
