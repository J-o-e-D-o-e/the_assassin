package net.joedoe.pathfinding;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import net.joedoe.GdxTestRunner;
import net.joedoe.utils.GameInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class GraphGeneratorTest {
    private TiledMap map;

    @Before
    public void setUp() {
        map = new TmxMapLoader().load("maps/fight1.tmx");
    }

    @Test
    public void generateGraph() {
        //when
        Graph graph = GraphGenerator.generateGraph(map);
        Array<Node> nodes = graph.getNodes();
        //then
        assertEquals(GameInfo.MAP_WIDTH * GameInfo.MAP_HEIGHT, nodes.size);
        int index = 0;
        for (Node node : nodes)
            assertEquals(index++, node.getIndex());
    }
}