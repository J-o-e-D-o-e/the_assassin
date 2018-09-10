package net.joedoe.controllers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import net.joedoe.GdxTestRunner;
import net.joedoe.entities.Enemy;
import net.joedoe.entities.Player;
import net.joedoe.maps.MapController;
import net.joedoe.pathfinding.Graph;
import net.joedoe.pathfinding.GraphGenerator;
import net.joedoe.utils.GameInfo;
import net.joedoe.utils.GameManager;
import net.joedoe.weapons.Weapon;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(GdxTestRunner.class)
public class FightControllerEnemyTest {
    private FightController controller;
    @Mock
    private MapController mapController;
    private static Graph graph;
    private Enemy enemy;
    private int initialAp;

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
        MockitoAnnotations.initMocks(this);
        controller = new FightController();
        controller.setMapController(mapController);
        enemy = (Enemy) controller.getEnemies().get(0);
        initialAp = enemy.getActionPoints();
    }

    @Test
    public void updateEnemiesCalculatePath() {
        //given: no path calculated
        enemy.getPath().clear();
        //when
        when(mapController.generateGraph()).thenReturn(graph);
        controller.updateEnemies();
        //then: path calculated
        assertTrue(enemy.getPathCount() > 0);
    }

    @Test
    public void updateEnemiesChangeWeapon() {
        //given: hands as active weapon
        enemy.changeWeapon(0);
        //when
        when(mapController.generateGraph()).thenReturn(graph);
        controller.updateEnemies();
        //then: pistol as active weapon
        Weapon[] weapons = enemy.getWeapons();
        assertSame(weapons[1], enemy.getActiveWeapon());
    }

    @Test
    public void updateEnemiesReload() {
        //given: short on loaded pistol ammo
        Weapon weapon = enemy.getActiveWeapon();
        weapon.setLoadedAmmo(2);
        //when
        when(mapController.generateGraph()).thenReturn(graph);
        controller.updateEnemies();
        //then: fully loaded pistol, less ap
        assertSame(weapon.getOneRound(), weapon.getLoadedAmmo());
        assertTrue(initialAp > enemy.getActionPoints());
    }

    @Test
    public void updateEnemiesHit() {
        //given: player stands right before enemy, no pistol ammo
        float x = enemy.getX();
        float y = enemy.getY();
        Player player = controller.getPlayer();
        int initialHealth = player.getHealth();
        player.setX(x - GameInfo.ONE_TILE);
        player.setY(y);
        Weapon pistol = enemy.getActiveWeapon();
        pistol.setAmmoStock(0);
        pistol.setLoadedAmmo(0);
        //when
        when(mapController.generateGraph()).thenReturn(graph);
        controller.updateEnemies();
        //then: hands as active weapon, less ap, less player's health
        Weapon[] weapons = enemy.getWeapons();
        assertSame(weapons[0], enemy.getActiveWeapon());
        assertEquals(initialAp - 1, enemy.getActionPoints());
        assertTrue( initialHealth > player.getHealth());
    }

    @Test
    public void updateEnemiesEndTurn() {
        //given: path straight from next node, player within range, not many aps
        float x = enemy.getX();
        float y = enemy.getY();
        enemy.setX(x - GameInfo.ONE_TILE);
        enemy.setY(y + GameInfo.ONE_TILE);
        Player player = controller.getPlayer();
        player.setX(x - 5 * GameInfo.ONE_TILE);
        player.setY(y + 2 * GameInfo.ONE_TILE);
        Weapon pistol = enemy.getActiveWeapon();
        enemy.setActionPoints(pistol.getApForOneAttack() + 1);
        //when
        when(mapController.generateGraph()).thenReturn(graph);
        controller.updateEnemies();
        //then: ap zero, path cleared, turn over
        assertEquals(0, enemy.getActionPoints());
        assertEquals(1, enemy.getPathIndex());
        assertEquals(0, enemy.getPath().getCount());
        assertTrue(enemy.isTurnOver());
    }

    @Test
    public void updateEnemiesMove() {
        //given: stands against wall (only one possible path)
        float x = enemy.getX() - GameInfo.ONE_TILE;
        enemy.setX(x);
        float y = enemy.getY();
        //when
        when(mapController.generateGraph()).thenReturn(graph);
        controller.updateEnemies();
        //then: moved up by one tile, less ap
        assertEquals(x, enemy.getX(), 0.001);
        assertEquals(y + GameInfo.ONE_TILE, enemy.getY(), 0.001);
        assertEquals(initialAp - 1, enemy.getActionPoints());
    }

    @Test
    public void updateEnemyNoAp() {
        //given: no ap
        Player player = controller.getPlayer();
        enemy.setActionPoints(0);
        //when
        when(mapController.generateGraph()).thenReturn(graph);
        controller.updateEnemies();
        //then: player's turn
        assertTrue(enemy.isTurnOver());
        assertFalse(player.isTurnOver());
    }
}