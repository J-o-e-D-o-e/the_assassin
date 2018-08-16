package net.joedoe.screencontrollers;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import lombok.Getter;
import net.joedoe.entities.MapEntity;
import net.joedoe.utils.GameInfo;

import static net.joedoe.utils.GameInfo.ONE_TILE;

@Getter
class MapController {
    TiledMap map;

    MapController(String path) {
        map = new TmxMapLoader().load(path);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean currentTileIsAccessible(float x, float y) {
        Cell cell = ((TiledMapTileLayer) map.getLayers().get("middle")).getCell((int) x / ONE_TILE, (int) y / ONE_TILE);
        return cell == null;
    }

    boolean nextTileIsAccessible(MapEntity mapEntity) {
        float[] nextTile = getCoordinatesOfNextTile(mapEntity);
        return currentTileIsAccessible(nextTile[0], nextTile[1]);
    }


    float[] getCoordinatesOfNextTile(MapEntity mapEntity) {
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

    String getTileName(MapEntity mapEntity) {
        float x = mapEntity.getX() / ONE_TILE;
        float y = mapEntity.getY() / ONE_TILE;
        MapObjects objects = map.getLayers().get("objects").getObjects();
        for (MapObject object : objects) {
            float placeX = object.getProperties().get("x", float.class) / GameInfo.PIXEL;
            float placeY = object.getProperties().get("y", float.class) / GameInfo.PIXEL;
            if (x == placeX && y == placeY) {
                return object.getName();
            }
        }
        return null;
    }

    void dispose() {
        map.dispose();
    }
}
