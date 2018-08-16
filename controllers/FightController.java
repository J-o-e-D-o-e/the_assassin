package net.joedoe.screencontrollers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import lombok.Getter;
import net.joedoe.entities.Bullet;
import net.joedoe.entities.Enemy;
import net.joedoe.entities.Player;
import net.joedoe.pathfinding.Graph;
import net.joedoe.pathfinding.GraphGenerator;
import net.joedoe.utils.GameInfo;
import net.joedoe.utils.GameManager;

import java.util.ArrayList;

@Getter
public class FightController {
    private MapController mapController;
    private GraphGenerator graphGenerator;
    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private String message;
    private boolean severelyInjured;

    public FightController(String path, ArrayList<Enemy> enemies) {
        mapController = new MapController(path);
        graphGenerator = new GraphGenerator(mapController.getMap());
        initializePlayer();
        initializeEnemies(enemies);
    }

    private void initializePlayer() {
        player = GameManager.player;
        player.setX(11 * GameInfo.ONE_TILE);
        player.setY(18 * GameInfo.ONE_TILE);
        player.setTexture(new Texture("entities/fightingPlayer.png"));
        player.setActionPointsToDefault();
        player.setMoved(false);
    }

    private void initializeEnemies(ArrayList<Enemy> enemies) {
        enemies.forEach(enemy -> {
            if (mapController.currentTileIsAccessible(enemy.getX(), enemy.getY()))
                this.enemies.add(enemy);
        });
    }

    public void playerMoves() {
        if (mapController.nextTileIsAccessible(player) && !playerCollidesWithEnemy(player))
            player.move();
    }

    private boolean playerCollidesWithEnemy(Player player) {
        float[] nextTile = mapController.getCoordinatesOfNextTile(player);
        for (Enemy enemy : enemies) {
            if (nextTile[0] == enemy.getX() && nextTile[1] == enemy.getY())
                return true;
        }
        return false;
    }

    public void playerAttacks() {
        if (player.checkIfPossibleToShoot()) {
            bullets.add(player.shoot());
            return;
        }
        if (player.getActiveWeapon().getName().equals("Hands")) {
            int damage = player.hit();
            float[] nextTile = mapController.getCoordinatesOfNextTile(player);
            enemies.forEach(enemy -> {
                if ((nextTile[0] == enemy.getX()) && (nextTile[1] == enemy.getY()) && damage != 0) {
                    enemy.isHitBy(-damage);
                    message = enemy.getName() + " is hit! Damage: " + damage;
                }
            });
        }
    }

    public void updateBullets() {
        if (!bullets.isEmpty()) {
            bullets.forEach(bullet -> {
                if (mapController.nextTileIsAccessible(bullet) && !bulletHitsTarget(bullet))
                    bullet.move();
                else
                    bullet.setRemove(true);
            });
            ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
            bullets.forEach(bullet -> {
                if (bullet.isRemove())
                    bulletsToRemove.add(bullet);
            });
            bullets.removeAll(bulletsToRemove);
        }
    }

    private boolean bulletHitsTarget(Bullet bullet) {
        float[] nextTile = mapController.getCoordinatesOfNextTile(bullet);
        if (nextTile[0] == player.getX() && nextTile[1] == player.getY()) {
            bullet.setRemove(true);
            int damage = bullet.getDamage();
            player.isHitBy(-damage);
            message = player.getName().toUpperCase() + " is hit by " + bullet.getWeaponName() + "! Damage: " + damage;
            return true;
        }
        for (Enemy enemy : enemies) {
            if (nextTile[0] == enemy.getX() && nextTile[1] == enemy.getY()) {
                bullet.setRemove(true);
                int damage = bullet.getDamage();
                enemy.isHitBy(-damage);
                message = enemy.getName().toUpperCase() + " is hit by " + bullet.getWeaponName() + "! Damage: " + damage;
                if (enemy.getHealth() < 20 && !severelyInjured) {
                    message = message + ". " + enemy.getName().toUpperCase() + " is severely injured.";
                    severelyInjured = true;
                }
                return true;
            }
        }
        return false;
    }

    public void updateEnemies() {
        for (Enemy enemy : enemies) {
            if (!enemy.isMoved() && !player.isDead()) {
                Graph graph = graphGenerator.generateGraph(enemy, enemies);
                if (enemy.calculatePath(graph, player.getX(), player.getY())) {
                    float[] nextTile = mapController.getCoordinatesOfNextTile(enemy);
                    enemy.calculateDistance();
                    enemy.setDirection();
                    enemy.checkIfPathStraightFromNextNodeToPlayer();
                    if (enemy.isPathStraightFromNextNodeToPlayer())
                        enemy.checkIfPathStraightFromCurrentNodeToPlayer();
                    if (enemy.checkIfInShootingRange() && enemy.checkIfPossibleToShoot()) {
                        bullets.add(enemy.shoot());
                    } else if (nextTile[0] == player.getX() && nextTile[1] == player.getY()) {
                        if (enemy.checkIfReloadPossibleAndNecessary())
                            enemy.reload();
                        else {
                            int damage = enemy.hit();
                            player.isHitBy(-damage);
                            message = player.getName() + " is hit! Damage: " + damage;
                        }
                    } else if (enemy.checkIfReloadPossibleAndNecessary()) {
                        enemy.reload();
                    } else if (enemy.checkIfNotEnoughAPsForShootingAfterNextStep()) {
                        enemy.endTurn();
                    } else if (enemy.checkIfDistanceGreaterOne()) {
                        enemy.move();
                    }
                } else
                    enemy.endTurn();
            } else {
                if (enemiesHaveMoved() && enemy == enemies.get(enemies.size() - 1)) {
                    player.setMoved(false);
                }
            }
        }
    }

    public boolean enemiesHaveMoved() {
        for (Enemy enemy : enemies) {
            if (!enemy.isMoved()) {
                return false;
            }
        }
        return true;
    }

    public boolean isOver() {
        ArrayList<Enemy> enemiesToRemove = new ArrayList<>();
        enemies.forEach(enemy -> {
            if (enemy.isDead())
                enemiesToRemove.add(enemy);
        });
        enemies.removeAll(enemiesToRemove);
        return enemies.isEmpty() || player.isDead();
    }

    public void renderPlayer(SpriteBatch batch) {
        player.render(batch);
    }

    public void renderEnemies(SpriteBatch batch) {
        enemies.forEach(enemy -> enemy.render(batch));
    }

    public void renderBullets(SpriteBatch batch) {
        if (!bullets.isEmpty())
            bullets.forEach(bullet -> bullet.render(batch));
    }

    @SuppressWarnings("unused")
    public void renderEnemyPaths(ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(new Color(GameInfo.RED[0], GameInfo.RED[1], GameInfo.RED[2], 1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        enemies.forEach(enemy -> enemy.renderPath(shapeRenderer));
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void setActorTexturesToDefault() {
        if (player.isHit()) {
            player.setTexture(new Texture("entities/fightingPlayer.png"));
            player.setHit(false);
        }
        enemies.forEach(enemy -> {
            if (enemy.isHit()) {
                enemy.setTexture(new Texture("entities/enemy.png"));
                enemy.setHit(false);
            }
        });
    }

    public TiledMap getMap() {
        return mapController.getMap();
    }

    public boolean playerIsDead() {
        return player.isDead();
    }

    public boolean bulletsIsEmpty() {
        return bullets.isEmpty();
    }

    public void setEnemiesMoved(boolean moved) {
        enemies.forEach(enemy -> enemy.setMoved(moved));
    }

    public void dispose() {
        player.getTexture().dispose();
        enemies.forEach(enemy -> enemy.getTexture().dispose());
        bullets.forEach(bullet -> {
            bullet.getTexture().dispose();
            bullet.getShot().dispose();
        });
        mapController.dispose();
    }
}