package net.joedoe.entities;

import net.joedoe.GdxTestRunner;
import net.joedoe.utils.Direction;
import net.joedoe.utils.GameInfo;
import net.joedoe.utils.GameManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class PlayerTest {
    private Player player;

    @Before
    public void setUp() {
        GameManager.getInstance().initializeGameData("joe doe", 0);
        GameManager.player = new Player();
        player = GameManager.player;
        player.setX(0);
        player.setY(0);
    }

    @Test
    public void move() {
        //given
        int ap = player.getActionPoints();
        player.setDirection(Direction.UP);
        //when
        player.move();
        //then: player moved
        assertEquals(0, player.getX(), 0.001);
        assertEquals(GameInfo.ONE_TILE, player.getY(), 0.001);
        assertEquals(ap - 1, player.getActionPoints());
    }

    @Test
    public void moveOutOfMap(){
        //given
        int ap = player.getActionPoints();
        player.setDirection(Direction.DOWN);
        //when
        player.move();
        //then: player not moved, no ap changed
        assertEquals(0, player.getX(), 0.001);
        assertEquals(0, player.getY(), 0.001);
        assertEquals(ap, player.getActionPoints());
    }

    @Test
    public void changeHealth(){
        //given
        int health = player.getHealth();
        int damage = -10;
        //when
        player.changeHealth(damage);
        //then: player's health reduced
        assertEquals(health + damage, player.getHealth());
    }
    @Test
    public void changeHealthDead(){
        //given
        int health = player.getHealth();
        int damage = -100;
        //when
        player.changeHealth(damage);
        //then: player's dead
        assertEquals(0, player.getHealth());
        assertTrue(player.isDead());
    }
}