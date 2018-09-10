package net.joedoe.entities;

import net.joedoe.utils.GameInfo;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BulletTest {
    private Bullet bullet;

    @Before
    public void setUp() {
        bullet = new Bullet(0, 0, 1, 1, 1, "Pistol");
    }

    @Test
    public void move() {
        //given
        int range = 2;
        bullet.setRange(range);
        //when
        bullet.move();
        //then: bullet moved, range reduced
        assertEquals(0, bullet.getX(), 0.001);
        assertEquals(GameInfo.ONE_TILE, bullet.getY(), 0.001);
        assertEquals(range - 1, bullet.getRange());
    }

    @Test
    public void outOfMap() {
        //given
        bullet.setDirection(2);
        //when
        bullet.move();
        //then
        assertTrue(bullet.isRemove());
    }

    @Test
    public void noMoreRange() {
        //given
        bullet.setRange(1);
        //when
        bullet.move();
        //then
        assertTrue(bullet.isRemove());
    }
}