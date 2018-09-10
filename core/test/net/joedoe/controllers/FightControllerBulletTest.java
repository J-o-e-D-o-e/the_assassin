package net.joedoe.controllers;

import net.joedoe.GdxTestRunner;
import net.joedoe.entities.Bullet;
import net.joedoe.entities.Enemy;
import net.joedoe.entities.Player;
import net.joedoe.maps.MapController;
import net.joedoe.utils.GameInfo;
import net.joedoe.utils.GameManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GdxTestRunner.class)
public class FightControllerBulletTest {
    private FightController controller;
    @Mock
    private MapController mapController;
    private Player player;
    private Enemy enemy;
    private Bullet bullet;

    @BeforeClass
    public static void setUpClass() {
        GameManager.getInstance().initializeGameData("joe doe", 0);
        GameManager.player = new Player();
        GameManager.storyStage = 2;
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new FightController();
        controller.setMapController(mapController);
        player = controller.getPlayer();
        player.setDirection(1);
        enemy = (Enemy) controller.getEnemies().get(0);
        //noinspection unchecked
        List<Bullet> bullets = (List<Bullet>) controller.getBullets();
        bullets.add(player.shoot());
        bullet = bullets.get(0);
    }

    @Test
    public void updateBulletsHitsPlayer() {
        //given: bullet right before player
        float[] nextTile = new float[]{player.getX(), player.getY()};
        int health = player.getHealth();
        //when
        when(mapController.nextTileIsAccessible(bullet)).thenReturn(true);
        when(mapController.getCoordinatesOfNextTile(bullet)).thenReturn(nextTile);
        controller.updateBullets();
        //then: bullet removed, player's health reduced
        assertTrue(bullet.isRemove());
        assertFalse(controller.getBullets().contains(bullet));
        assertEquals(health - bullet.getDamage(), player.getHealth());
    }

    @Test
    public void updateBulletsHitsEnemy() {
        //given: bullet right before enemy, enemy low health
        float[] nextTile = new float[]{enemy.getX(), enemy.getY()};
        int health = 20;
        enemy.setHealth(health);
        //when
        when(mapController.nextTileIsAccessible(bullet)).thenReturn(true);
        when(mapController.getCoordinatesOfNextTile(bullet)).thenReturn(nextTile);
        controller.updateBullets();
        //then: bullet removed, enemy severely injured, less health
        assertTrue(bullet.isRemove());
        assertFalse(controller.getBullets().contains(bullet));
        assertTrue(enemy.isSeverelyInjured());
        assertEquals(health - bullet.getDamage(), enemy.getHealth());
    }

    @Test
    public void updateBulletsMove(){
        //given: bullet does neither hit nor collide
        float[] nextTile = new float[]{player.getX(), player.getY() + GameInfo.ONE_TILE};
        int playerHealth = player.getHealth();
        int enemyHealth = enemy.getHealth();
        //when
        when(mapController.nextTileIsAccessible(bullet)).thenReturn(true);
        when(mapController.getCoordinatesOfNextTile(bullet)).thenReturn(nextTile);
        controller.updateBullets();
        //then: bullet moved
        assertTrue(controller.getBullets().contains(bullet));
        assertEquals(nextTile[0], bullet.getX(),0.001);
        assertEquals(nextTile[1], bullet.getY(), 0.001);
        assertEquals(playerHealth, player.getHealth());
        assertEquals(enemyHealth, enemy.getHealth());
    }

    @Test
    public void updateBulletsNotAccessible() {
        //when: next tile is not accessible
        when(mapController.nextTileIsAccessible(bullet)).thenReturn(false);
        controller.updateBullets();
        //then: bullet removed, no health change, method body not executed
        assertTrue(bullet.isRemove());
        assertFalse(controller.getBullets().contains(bullet));
        verify(mapController, never()).getCoordinatesOfNextTile(bullet);
    }
}