package net.joedoe.entities;

import lombok.Getter;
import lombok.Setter;

import static net.joedoe.utils.GameInfo.*;

@Getter
@Setter
public class Bullet implements MapEntity {
    protected float x, y;
    protected int direction;
    private int range;
    private int damage;
    private String weaponName;
    private boolean remove;

    Bullet(float x, float y, int direction, int range, int damage, String weaponName) {
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
            case 1: // N
                y += ONE_TILE;
                break;
            case 2: // W
                x -= ONE_TILE;
                break;
            case 3: // S
                y -= ONE_TILE;
                break;
            case 4: // E
                x += ONE_TILE;
                break;
        }
        if (x > WIDTH || y > HEIGHT || x < 0 || y < 0 || range == 0) {
            remove = true;
        }
    }
}
