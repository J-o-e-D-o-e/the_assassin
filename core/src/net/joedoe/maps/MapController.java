package net.joedoe.maps;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import lombok.Getter;
import net.joedoe.entities.MapEntity;
import net.joedoe.pathfinding.Graph;
import net.joedoe.pathfinding.GraphGenerator;

import static net.joedoe.utils.GameInfo.ONE_TILE;

@Getter
public class MapController {
    TiledMap map;

    public MapController() {
        map = MapFactory.createMap();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean currentTileIsAccessible(float x, float y) {
        Cell cell = ((TiledMapTileLayer) map.getLayers().get("middle")).getCell((int) x / ONE_TILE, (int) y / ONE_TILE);
        return cell == null;
    }

    public boolean nextTileIsAccessible(MapEntity mapEntity) {
        float[] nextTile = getCoordinatesOfNextTile(mapEntity);
        return currentTileIsAccessible(nextTile[0], nextTile[1]);
    }


    public float[] getCoordinatesOfNextTile(MapEntity mapEntity) {
        float[] nextTile = new float[]{mapEntity.getX(), mapEntity.getY()};
        switch (mapEntity.getDirection()) {
            case 1: // N
                nextTile[1] += ONE_TILE;
                break;
            case 2: // W
                nextTile[0] -= ONE_TILE;
                break;
            case 3: // S
                nextTile[1] -= ONE_TILE;
                break;
            case 4: // E
                nextTile[0] += ONE_TILE;
                break;
        }
        return nextTile;
    }

    public void dispose() {
        map.dispose();
    }

    public Graph generateGraph() {
        return GraphGenerator.generateGraph(map);
    }
}
