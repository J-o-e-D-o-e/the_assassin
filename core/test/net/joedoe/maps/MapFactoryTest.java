package net.joedoe.maps;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import net.joedoe.GdxTestRunner;
import net.joedoe.utils.GameManager;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class MapFactoryTest {

    @Test
    public void createMap() {
        //given: creates map for FightScreen
        TiledMap mapDefault = new TmxMapLoader().load("maps/fight1.tmx");
        TiledMapTileLayer layerDefault = (TiledMapTileLayer) mapDefault.getLayers().get("middle");
        int idDefault = layerDefault.getCell(20,20).getTile().getId();
        //when
        String screen = "Fight";
        GameManager.storyStage = 2;
        TiledMap map = MapFactory.createMap();
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("middle");
        int id = layer.getCell(20,20).getTile().getId();
        //then
        assertEquals(1000, layer.getWidth() * layer.getHeight());
        assertEquals(idDefault,id);
    }
}