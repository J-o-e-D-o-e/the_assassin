package net.joedoe.entities;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import net.joedoe.GdxTestRunner;
import net.joedoe.pathfinding.Graph;
import net.joedoe.pathfinding.GraphGenerator;
import net.joedoe.utils.GameManager;
import net.joedoe.weapons.Pistol;
import net.joedoe.weapons.Weapon;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static net.joedoe.utils.GameInfo.ONE_TILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(GdxTestRunner.class)
public class EnemyTest {
    private Enemy enemy;
    private float x, y;
    private Player player;
    private static Graph graph;

    @SuppressWarnings("Duplicates")
    @BeforeClass
    public static void setUpClass() {
        GameManager.getInstance().initializeGameData("joe doe", 0);
        GameManager.player = new Player();
        GameManager.storyStage = 2;
        TiledMap map = new TmxMapLoader().load("maps/fight1.tmx");
        graph = GraphGenerator.generateGraph(map);
    }

    @Before
    public void setUp() {
        x = 30 * ONE_TILE;
        y = 15 * ONE_TILE;
        Weapon pistol = new Pistol();
        enemy = new Enemy("Sneaky Pete", x, y, 50, 50, pistol);
        player = GameManager.player;
    }

    @Test
    public void reset() {
        //given
        enemy.setTurnOver(true);
        enemy.setActionPoints(0);
        //when
        enemy.reset();
        //then
        assertFalse(enemy.isTurnOver());
        assertEquals(enemy.getStrength() / 5, enemy.getActionPoints());
    }

    @Test
    public void checkIfPathStraightFromCurrentNode() {
        //given: path to player straight from current node
        player.setX(x);
        player.setY(y + 3 * ONE_TILE);
        enemy.calculatePath(graph, player, new ArrayList<>());
        //when
        enemy.checkIfPathStraightFromCurrentNode();
        //then
        assertTrue(enemy.isPathStraightFromCurrentNode());
        assertTrue(enemy.isPathStraightFromNextNode());
    }

    @Test
    public void checkIfPathStraightFromNextNode() {
        //given: path not straight from current node, but from next node
        player.setX(x + ONE_TILE);
        player.setY(y + ONE_TILE);
        enemy.calculatePath(graph, player, new ArrayList<>());
        //when
        enemy.checkIfPathStraightFromCurrentNode();
        enemy.checkIfPathStraightFromNextNode();
        //then
        assertFalse(enemy.isPathStraightFromCurrentNode());
        assertTrue(enemy.isPathStraightFromNextNode());
    }
}