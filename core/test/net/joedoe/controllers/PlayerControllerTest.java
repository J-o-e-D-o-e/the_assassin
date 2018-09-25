package net.joedoe.controllers;

import net.joedoe.GdxTestRunner;
import net.joedoe.entities.Bullet;
import net.joedoe.entities.Enemy;
import net.joedoe.entities.Player;
import net.joedoe.maps.MapController;
import net.joedoe.utils.Direction;
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
public class PlayerControllerTest {
    private PlayerController controller;
    @Mock
    private MapController mapController;
    private Player player;
    private Enemy enemy;
    private List<Bullet> bullets;

    @BeforeClass
    public static void setUpClass() {
        GameManager.getInstance().initializeGameData("joe doe", 0);
        GameManager.player = new Player();
        GameManager.storyStage = 2;
    }

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        FightController fightController = new FightController();
        controller = new PlayerController(fightController);
        controller.setMapController(mapController);
        player = controller.getPlayer();
        enemy = (Enemy) fightController.getEnemies().get(0);
        bullets = (List<Bullet>) fightController.getBullets();
    }

    @Test
    public void playerMoves() {
        // given
        float[] nextTile = new float[]{player.getX(), player.getY() + GameInfo.ONE_TILE};
        //when
        when(mapController.nextTileIsAccessible(player)).thenReturn(true);
        when(mapController.getCoordinatesOfNextTile(player)).thenReturn(nextTile);
        controller.move(Direction.UP);
        //then: player moved, playerCollidesWithEnemy() executed
        assertEquals(nextTile[0], player.getX(), 0.001);
        assertEquals(nextTile[1], player.getY(), 0.001);
        verify(mapController).getCoordinatesOfNextTile(player);
    }

    @Test
    public void playerMovesNotAccessible() {
        //given
        float x = player.getX();
        float y = player.getY();
        int ap = player.getActionPoints();
        //when
        when(mapController.nextTileIsAccessible(player)).thenReturn(false);
        controller.move(Direction.UP);
        //then: player didn't move, no ap changed
        assertEquals(x, player.getX(), 0.001);
        assertEquals(y, player.getY(), 0.001);
        assertEquals(ap, player.getActionPoints());
        verify(mapController, never()).getCoordinatesOfNextTile(player);
    }

    @Test
    public void playerMovesCollidesWithEnemy() {
        //given: player stands right before player
        float[] nextTile = new float[]{enemy.getX(), enemy.getY()};
        int ap = player.getActionPoints();
        //when
        when(mapController.getCoordinatesOfNextTile(player)).thenReturn(nextTile);
        controller.move(Direction.UP);
        //then: player not moved, ap same
        assertEquals(player.getX(), player.getX(), 0.001);
        assertEquals(player.getY(), player.getY(), 0.001);
        assertEquals(ap, player.getActionPoints());
    }

    @Test
    public void playerAttacksShoot() {
        //given
        int ap = player.getActionPoints();
        int oneShot = player.getActiveWeapon().getApForOneAttack();
        //when
        controller.attack(Direction.UP);
        controller.attack(Direction.UP);
        //then: bullets +2, ap reduced, other branch not accessed
        assertEquals(2, bullets.size());
        assertEquals(ap - 2 * oneShot, player.getActionPoints());
        verify(mapController, never()).getCoordinatesOfNextTile(player);

    }

    @Test
    public void playerAttacksHit() {
        //given: hands as active weapon, player stands right before enemy
        player.changeWeapon(0);
        int initialHealth = enemy.getHealth();
        float[] nextTile = new float[]{enemy.getX(), enemy.getY()};
        //when
        when(mapController.getCoordinatesOfNextTile(player)).thenReturn(nextTile);
        controller.attack(Direction.UP);
        //then: no bullet added to bullets, enemy's health reduced, message not null
        assertEquals(0, bullets.size());
        assertTrue(enemy.getHealth() < initialHealth);
        assertNotNull(controller.getFightController().getMessage());
    }
}