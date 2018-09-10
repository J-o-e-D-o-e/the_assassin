package net.joedoe.maps;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import net.joedoe.utils.GameManager;

class MapFactory {
    static TiledMap createMap() {
        String path;
        switch (GameManager.storyStage) {
            case 2:
                path = "maps/fight1.tmx"; // SNEAKY PETE
                break;
            case 4:
                path = "maps/fight2.tmx"; // CARLA MONTEPULCIANO
                break;
            case 8:
                path = "maps/fight3.tmx"; // BODYGUARDS
                break;
            case 10:
                path = "maps/fight4.tmx"; // THE GHOST
                break;
            default:
                path = "maps/fight1.tmx";
                break;
        }
        return new TmxMapLoader().load(path);
    }
}
