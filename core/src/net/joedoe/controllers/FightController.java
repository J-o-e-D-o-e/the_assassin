package net.joedoe.controllers;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.maps.tiled.TiledMap;
import lombok.Getter;
import lombok.Setter;
import net.joedoe.entities.*;
import net.joedoe.maps.MapController;
import net.joedoe.pathfinding.Graph;
import net.joedoe.pathfinding.Node;
import net.joedoe.utils.GameInfo;
import net.joedoe.utils.GameManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class FightController {
    private Player player;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private MapController mapController;
    private String message;

    public FightController() {
        mapController = new MapController();
        initializePlayer();
        List<Enemy> enemies = EnemyFactory.createEnemies();
        initializeEnemies(enemies);
    }

    private void initializePlayer() {
        player = GameManager.player;
        player.setX(11 * GameInfo.ONE_TILE);
        player.setY(17 * GameInfo.ONE_TILE);
        player.setActionPointsToDefault();
        player.setTurnOver(false);
    }

    private void initializeEnemies(List<Enemy> enemies) {
        for (Enemy enemy : enemies)
            if (mapController.currentTileIsAccessible(enemy.getX(), enemy.getY()))
                this.enemies.add(enemy);
    }

    public void playerMoves(int direction) {
        player.setDirection(direction);
        if (mapController.nextTileIsAccessible(player) && !playerCollidesWithEnemy())
            player.move();
    }

    private boolean playerCollidesWithEnemy() {
        float[] nextTile = mapController.getCoordinatesOfNextTile(player);
        return enemies.stream().anyMatch(enemy -> enemy.getX() == nextTile[0] && enemy.getY() == nextTile[1]);
    }

    public void playerAttacks(int direction) {
        player.setDirection(direction);
        if (player.checkIfShoot()) {
            bullets.add(player.shoot());
            return;
        }
        if (player.checkIfHit()) {
            float[] nextTile = mapController.getCoordinatesOfNextTile(player);
            for (Enemy enemy : enemies)
                if (nextTile[0] == enemy.getX() && nextTile[1] == enemy.getY()) {
                    int damage = player.hit();
                    enemy.isHitBy(damage);
                    if (damage != 0)
                        message = enemy.getName() + " is hit! Damage: " + damage;
                }
        }
    }

    public void playerChangesWeapon(int choice) {
        if (player.getWeapons()[choice] != null)
            player.changeWeapon(choice);
    }

    public void playerReloads(){
        player.reload();
    }

    public void playerEndsTurn(){
        player.endTurn();
    }

    public void updateEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy.getActionPoints() != 0) {
                if (enemy.getPathCount() == 0) {
                    Graph graph = mapController.generateGraph();
                    enemy.calculatePath(graph, player, enemies);
                }
                if (!enemy.checkIfPathStraightFromCurrentNode())
                    enemy.checkIfPathStraightFromNextNode();
                if (enemy.checkIfChangeWeapon())
                    enemy.changeWeapon(1);
                enemy.setDirection();
                if (enemy.checkIfShoot()) {
                    bullets.add(enemy.shoot());
                    continue;
                }
                if (enemy.checkIfReload()) {
                    enemy.reload();
                    continue;
                }
                if (enemy.checkIfHit()) {
                    enemy.changeWeapon(0);
                    int damage = enemy.hit();
                    player.isHitBy(damage);
                    message = player.getName() + " is hit! Damage: " + damage;
                    continue;
                }
                if (enemy.checkIfEndTurn()) {
                    enemy.endTurn();
                    continue;
                }
                if (enemy.calculateDistance() > 1) {
                    enemy.move();
                }
            } else {
                enemy.endTurn();
                if (enemiesTurnOver())
                    player.setTurnOver(false);
            }
        }
    }

    public void updateBullets() {
        for (Bullet bullet : bullets)
            if (mapController.nextTileIsAccessible(bullet)) {
                float[] nextTile = mapController.getCoordinatesOfNextTile(bullet);
                if (nextTile[0] == player.getX() && nextTile[1] == player.getY()) {
                    bullet.setRemove(true);
                    int damage = bullet.getDamage();
                    player.isHitBy(damage);
                    message = player.getName() + " is hit by " + bullet.getWeaponName() + "! Damage: " + damage;
                    continue;
                }
                for (Enemy enemy : enemies)
                    if (nextTile[0] == enemy.getX() && nextTile[1] == enemy.getY()) {
                        bullet.setRemove(true);
                        int damage = bullet.getDamage();
                        enemy.isHitBy(damage);
                        message = enemy.getName() + " is hit by " + bullet.getWeaponName() + "! Damage: " + damage;
                        if (enemy.getHealth() < 20 && !enemy.isSeverelyInjured()) {
                            enemy.setSeverelyInjured(true);
                            //noinspection StringConcatenationInLoop
                            message = message + ". " + enemy.getName() + " is severely injured.";
                        }
                        break;
                    }
                bullet.move();
            } else
                bullet.setRemove(true);
        bullets = bullets.stream().filter(bullet -> !bullet.isRemove()).collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean bulletsIsEmpty() {
        return bullets.isEmpty();
    }

    public boolean playersTurnOver() {
        return player.isTurnOver();
    }

    public boolean enemiesTurnOver() {
        return enemies.stream().allMatch(Enemy::isTurnOver);
    }

    public void enemiesReset() {
        enemies.forEach(Enemy::reset);
    }

    public boolean playerIsDead() {
        return player.isDead();
    }

    public boolean enemiesAreDead() {
        enemies = enemies.stream().filter(enemy -> !enemy.isDead()).collect(Collectors.toCollection(ArrayList::new));
        return enemies.isEmpty();
    }

    public boolean playerHit() {
        boolean playerIsHit = player.isHit();
        if (playerIsHit)
            player.setHit(false);
        return playerIsHit;
    }

    public List<? extends MapEntity> getEnemiesHit() {
        List<Enemy> enemiesHit = enemies.stream().filter(Enemy::isHit).collect(Collectors.toCollection(ArrayList::new));
        enemiesHit.forEach(enemy -> enemy.setHit(false));
        return enemiesHit;
    }

    public List<? extends MapEntity> getEnemies() {
        return enemies;
    }

    public List<? extends MapEntity> getBullets() {
        return bullets;
    }

    public List<DefaultGraphPath<Node>> getPaths() {
        List<DefaultGraphPath<Node>> paths = new ArrayList<>();
        enemies.forEach(enemy -> paths.add(enemy.getPath()));
        return paths;
    }

    public TiledMap getMap() {
        return mapController.getMap();
    }

    public void dispose() {
        mapController.dispose();
    }
}