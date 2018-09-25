package net.joedoe.entities;

import lombok.Getter;
import lombok.Setter;
import net.joedoe.utils.Direction;

import static net.joedoe.utils.GameInfo.*;

@Getter
@Setter
public class Bullet implements MapEntity {
    protected float x, y;
    protected Direction direction;
    private int range;
    private int damage;
    private String weaponName;
    private boolean remove;

    Bullet(float x, float y, Direction direction, int range, int damage, String weaponName) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.range = range;
        this.damage = damage;
        this.weaponName = weaponName;
    }

    public void move() {
        range -= 1;
        switch (direction) {
            case UP:
                y += ONE_TILE;
                break;
            case LEFT:
                x -= ONE_TILE;
                break;
            case DOWN:
                y -= ONE_TILE;
                break;
            case RIGHT:
                x += ONE_TILE;
                break;
        }
        if (x > WIDTH || y > HEIGHT || x < 0 || y < 0 || range == 0) {
            remove = true;
        }
    }
}
