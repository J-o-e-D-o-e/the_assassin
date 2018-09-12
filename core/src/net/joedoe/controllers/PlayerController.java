package net.joedoe.controllers;

import lombok.Getter;
import lombok.Setter;
import net.joedoe.entities.Bullet;
import net.joedoe.entities.Enemy;
import net.joedoe.entities.Player;
import net.joedoe.maps.MapController;
import net.joedoe.utils.GameInfo;
import net.joedoe.utils.GameManager;

import java.util.List;

@Getter
@Setter
public class PlayerController implements IPlayerController {
    private FightController fightController;
    private MapController mapController;
    private Player player;

    public PlayerController(FightController fightController) {
        this.fightController = fightController;
        this.mapController = fightController.getMapController();
        initializePlayer();
    }

    private void initializePlayer() {
        player = GameManager.player;
        player.setX(11 * GameInfo.ONE_TILE);
        player.setY(17 * GameInfo.ONE_TILE);
        player.setActionPointsToDefault();
        player.setTurnOver(false);
    }

    @Override
    public void move(int direction) {
        player.setDirection(direction);
        if (mapController.nextTileIsAccessible(player) && !collidesWithEnemy())
            player.move();
    }

    @SuppressWarnings("unchecked")
    private boolean collidesWithEnemy() {
        float[] nextTile = mapController.getCoordinatesOfNextTile(player);
        List<Enemy> enemies = (List<Enemy>) fightController.getEnemies();
        return enemies.stream().anyMatch(enemy -> enemy.getX() == nextTile[0] && enemy.getY() == nextTile[1]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void attack(int direction) {
        player.setDirection(direction);
        if (player.checkIfShoot()) {
            List<Bullet> bullets = (List<Bullet>) fightController.getBullets();
            bullets.add(player.shoot());
            return;
        }
        if (player.checkIfHit()) {
            float[] nextTile = mapController.getCoordinatesOfNextTile(player);
            List<Enemy> enemies = (List<Enemy>) fightController.getEnemies();
            for (Enemy enemy : enemies)
                if (nextTile[0] == enemy.getX() && nextTile[1] == enemy.getY()) {
                    int damage = player.hit();
                    enemy.isHitBy(damage);
                    if (damage != 0) {
                        String message = enemy.getName() + " is hit! Damage: " + damage;
                        fightController.setMessage(message);
                    }
                }
        }
    }

    @Override
    public void changeWeapon(int choice) {
        if (player.getWeapons()[choice] != null)
            player.changeWeapon(choice);
    }

    @Override
    public void reload() {
        player.reload();
    }
    @Override
    public void endTurn(){
        player.endTurn();
    }

    @Override
    public boolean isTurnOver() {
        return player.isTurnOver();
    }

    public boolean isHit() {
        boolean playerIsHit = player.isHit();
        if (playerIsHit)
            player.setHit(false);
        return playerIsHit;
    }

    public boolean isDead() {
        return player.isDead();
    }
}
