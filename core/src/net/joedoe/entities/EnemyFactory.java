package net.joedoe.entities;

import net.joedoe.utils.GameManager;
import net.joedoe.weapons.MachineGun;
import net.joedoe.weapons.Pistol;
import net.joedoe.weapons.Rifle;

import java.util.ArrayList;
import java.util.List;

import static net.joedoe.utils.GameInfo.ONE_TILE;

public class EnemyFactory {
    public static List<Enemy> createEnemies() {
        List<Enemy> enemies = new ArrayList<>();
        switch (GameManager.storyStage) {
            case 2:
                String name = "Sneaky Pete";
                float x = 30 * ONE_TILE;
                float y = 15 * ONE_TILE;
                int health = 50;
                int strength = 25;
                enemies.add(new Enemy(name, x, y, health, strength, new Pistol()));
                break;
            case 4:
                name = "Carla Montepulciano";
                x = 30 * ONE_TILE;
                y = 15 * ONE_TILE;
                health = 70;
                strength = 30;
                enemies.add(new Enemy(name, x, y, health, strength, new Rifle()));
                break;
            case 8:
                for (int i = 0; i < 2; i++) {
                    name = "Bodyguard " + (i + 1);
                    x = 25 * ONE_TILE;
                    y = (15 + i * 2) * ONE_TILE;
                    health = 50;
                    strength = 25;
                    enemies.add(new Enemy(name, x, y, health, strength, new Rifle()));
                }
                break;
            case 10:
                name = "The Ghost";
                x = 27 * ONE_TILE;
                y = 17 * ONE_TILE;
                health = 70;
                strength = 30;
                enemies.add(new Enemy(name, x, y, health, strength, new MachineGun()));
                break;
            default:
                name = "Sneaky Pete";
                x = 30 * ONE_TILE;
                y = 15 * ONE_TILE;
                health = 50;
                strength = 25;
                enemies.add(new Enemy(name, x, y, health, strength, new Pistol()));
                break;
        }
        return enemies;
    }
}
