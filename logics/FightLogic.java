package net.joedoe.logics;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.audio.Sound;

import net.joedoe.GameInfo;
import net.joedoe.entities.Actor;
import net.joedoe.entities.Bullet;
import net.joedoe.entities.Enemy;
import net.joedoe.entities.MovingEntity;
import net.joedoe.entities.Player;
import net.joedoe.entities.weapons.MachineGun;
import net.joedoe.entities.weapons.Pistol;
import net.joedoe.entities.weapons.Rifle;
import net.joedoe.helpers.GameManager;
import net.joedoe.logics.pathfinding.GraphGenerator;
import net.joedoe.logics.pathfinding.Node;

public class FightLogic extends MapLogic {
    private ArrayList<Player> gangmembers;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;
    private GraphGenerator graphGenerator;
    private String message;
    private boolean severelyInjured;
    public Sound slap;
    public int roundsLeft;

    public FightLogic(String path) {
        super(path);
        graphGenerator = new GraphGenerator(map);
        initializePlayer();
        initializeEnemies();
        bullets = new ArrayList<Bullet>();
        slap = Gdx.audio.newSound(Gdx.files.internal("sounds/slap.wav"));
    }

    void initializePlayer() {
        player = GameManager.player;
        player.setX(GameInfo.WIDTH / 2f - 9 * GameInfo.ONE_TILE);
        player.setY(GameInfo.HEIGHT - 8 * GameInfo.ONE_TILE);
        player.setTexture("entities/fightingPlayer.png");
        player.setActionPointsToDefault();
        player.hasMoved = false;
        gangmembers = new ArrayList<Player>();
    }

    void initializeEnemies() {
        enemies = new ArrayList<Enemy>();
        if (GameManager.storyStage == 2) { // SNEAKY PETE
            String name = "Sneaky Pete";
            float x = GameInfo.WIDTH / 2f + 9 * GameInfo.ONE_TILE;
            float y = GameInfo.HEIGHT - 10 * GameInfo.ONE_TILE;
            int health = 70;
            int strength = 25;
            enemies.add(new Enemy(name, x, y, health, strength, new Pistol()));
        } else if (GameManager.storyStage == 4) { // CARLA
            String name = "Carla Montepulciano";
            float x = GameInfo.WIDTH / 2f + 9 * GameInfo.ONE_TILE;
            float y = GameInfo.HEIGHT - 10 * GameInfo.ONE_TILE;
            int health = 70;
            int strength = 30;
            enemies.add(new Enemy(name, x, y, health, strength, new Rifle()));
        } else if (GameManager.storyStage == 8) { // BODYGUARD
            for (int i = 0; i < 2; i++) {
                String name = "Bodyguard " + (i + 1);
                float x = GameInfo.WIDTH / 2f + 4 * GameInfo.ONE_TILE;
                float y = GameInfo.HEIGHT - (9 + i * 2) * GameInfo.ONE_TILE;
                int health = 50;
                int strength = 25;
                enemies.add(new Enemy(name, x, y, health, strength, new Rifle()));
            }
            roundsLeft = 18;
        } else if (GameManager.storyStage == 10) { // THE GHOST
            String name = "The Ghost";
            float x = GameInfo.WIDTH / 2f + 7 * GameInfo.ONE_TILE;
            float y = GameInfo.HEIGHT - 8 * GameInfo.ONE_TILE;
            int health = 70;
            int strength = 30;
            enemies.add(new Enemy(name, x, y, health, strength, new MachineGun()));
        }
    }

    public void addBullets(Actor actor) {
        actor.getActiveWeapon().decreaseLoadedAmmo();
        int direction = actor.getDirection();
        int range = actor.getActiveWeapon().getRange();
        int damage = actor.getActiveWeapon().getDamage();
        String name = actor.getActiveWeapon().getName();
        bullets.add(new Bullet(actor.getX(), actor.getY(), direction, range, damage, name));
    }

    public void updateBullets() {
        for (Bullet bullet : bullets) {
            if (nextTileIsAccessible(bullet) && !collidesWithActor(bullet)) {
                bullet.move();
            }
        }
        ArrayList<Bullet> bulletsToRemove = new ArrayList<Bullet>();
        for (Bullet bullet : bullets) {
            if (bullet.remove) {
                bulletsToRemove.add(bullet);
            }
        }
        bullets.removeAll(bulletsToRemove);
    }

    public boolean collidesWithActor(MovingEntity entity) {
        float[] nextTile = getCoordinatesOfNextTile(entity);
        if (nextTile[0] == player.getX() && nextTile[1] == player.getY()) {
            if (entity instanceof Bullet) {
                ((Bullet) entity).remove = true;
                int damage = ((Bullet) entity).getRandomizedDamage();
                player.isHit(-damage);
                message = player.getName().toUpperCase() + " is hit by " + ((Bullet) entity).getWeaponName()
                        + "! Damage: " + damage;
            }
            return true;
        }
        for (Enemy enemy : enemies) {
            if (nextTile[0] == enemy.getX() && nextTile[1] == enemy.getY()) {
                if (entity instanceof Bullet) {
                    ((Bullet) entity).remove = true;
                    int damage = ((Bullet) entity).getRandomizedDamage();
                    enemy.isHit(-damage);
                    message = enemy.getName().toUpperCase() + " is hit by " + ((Bullet) entity).getWeaponName()
                            + "! Damage: " + damage;
                    if (enemy.getHealth() < 25 && !severelyInjured) {
                        message = message + ". " + enemy.getName().toUpperCase() + " is severely injured.";
                        severelyInjured = true;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void updateEnemies() {
        // REMOVE DEAD ENEMIES:
        ArrayList<Enemy> enemiesToRemove = new ArrayList<Enemy>();
        for (Enemy enemy : enemies) {
            if (enemy.isDead) {
                enemiesToRemove.add(enemy);
            }
        }
        enemies.removeAll(enemiesToRemove);
        // ############# ENEMY STEERING: ############# //
        for (Enemy enemy : enemies) {
            if (!enemy.hasMoved && !enemy.isDead) {
                // PREPRATION FOR DECISION MAKING:
                // PATHFINDING:
                enemy.graph = graphGenerator.generateGraph(enemy, enemies);
                enemy.pathfinder = new IndexedAStarPathFinder<Node>(enemy.graph, true);
                Node start = enemy.graph.getNodeByCoordinates(enemy.getX(), enemy.getY());
                Node end = enemy.graph.getNodeByCoordinates(player.getX(), player.getY());
                if (enemy.calculatePath(start, end)) {
                    Node currentNode = enemy.getCurrentNode();
                    Node nextNode = enemy.getNextNode();
                    Node afterNextNode = enemy.getAfterNextNode();
                    boolean straightPathFromCurrentNode = true;
                    boolean straightPathFromNextNode = true;
                    // SET DIRECTION:
                    if (nextNode.getY() * GameInfo.ONE_TILE > enemy.getY()) {
                        enemy.setDirection(1); // N
                    } else if (nextNode.getX() * GameInfo.ONE_TILE < enemy.getX()) {
                        enemy.setDirection(2); // W
                    } else if (nextNode.getY() * GameInfo.ONE_TILE < enemy.getY()) {
                        enemy.setDirection(3); // S
                    } else if (nextNode.getX() * GameInfo.ONE_TILE > enemy.getX()) {
                        enemy.setDirection(4); // O
                    }
                    // DISTANCE BETWEEN PLAYER & ENEMY:
                    int distance = 0;
                    for (int i = enemy.pathIndex - 1; i < enemy.path.getCount() - 1; i++) {
                        distance++;
                    }
                    // IS PATH STRAIGHT FROM NEXT NODE ON?
                    if (afterNextNode != null) {
                        if (afterNextNode.getY() > nextNode.getY() || afterNextNode.getY() < nextNode.getY()) {
                            for (int i = enemy.pathIndex; i < enemy.path.getCount(); i++) {
                                if (enemy.path.get(i).getX() != nextNode.getX()) {
                                    straightPathFromNextNode = false;
                                    straightPathFromCurrentNode = false;
                                    break;
                                }
                            }
                        } else {
                            for (int i = enemy.pathIndex; i < enemy.path.getCount(); i++) {
                                if (enemy.path.get(i).getY() != nextNode.getY()) {
                                    straightPathFromNextNode = false;
                                    straightPathFromCurrentNode = false;
                                    break;
                                }
                            }
                        }
                    }
                    // IS PATH STRAIGHT FROM CURRENT NODE ON?
                    if (straightPathFromNextNode) {
                        if (nextNode.getY() > currentNode.getY() || nextNode.getY() < currentNode.getY()) {
                            for (int i = enemy.pathIndex - 1; i < enemy.path.getCount(); i++) {
                                if (enemy.path.get(i).getX() != currentNode.getX()) {
                                    straightPathFromCurrentNode = false;
                                    break;
                                }
                            }
                        } else {
                            for (int i = enemy.pathIndex - 1; i < enemy.path.getCount(); i++) {
                                if (enemy.path.get(i).getY() != currentNode.getY()) {
                                    straightPathFromCurrentNode = false;
                                    break;
                                }
                            }
                        }
                    }
                    // DECISION MAKING:
                    // SHOOT:
                    if (straightPathFromCurrentNode && distance <= enemy.getActiveWeapon().getRange()
                            && enemy.getActionPoints() >= enemy.getActiveWeapon().getApForOneShot()) {
                        enemy.attack(this);
                    } // DON'T STEP(+1) INTO WEAPON RANGE IF NO APs FOR
                      // SHOOTING:
                    else if (straightPathFromNextNode && distance <= enemy.getActiveWeapon().getRange() + 1
                            && enemy.getActionPoints() < enemy.getActiveWeapon().getApForOneShot() + 1) {
                        // System.out.println(enemy.getName() + ": TACTICS!
                        // Unused
                        // aps: " + enemy.getActionPoints());
                        enemy.endTurn();
                    } // MOVE:
                      // && !collidesWithActor(enemy), if 'enemy.pathFound' is
                      // used
                    else if (distance > 1) {
                        enemy.move();
                        enemy.pathIndex++;
                    }
                } // END TURN AS DEFAULT:
                else {
                    enemy.endTurn();
                }
            } else {
                if (enemiesHaveMoved() && enemy == enemies.get(enemies.size() - 1)) {
                    player.setHasMoved(false);
                    if (GameManager.storyStage == 9) {
                        roundsLeft--;
                        message = "Rounds left: " + roundsLeft;
                    }
                }
            }
        }
    }

    boolean enemiesHaveMoved() {
        for (Enemy enemy : enemies) {
            if (!enemy.hasMoved) {
                return false;
            }
        }
        return true;
    }

    public boolean isOver() {
        if (GameManager.storyStage == 9 && roundsLeft == 0) {
            message = "\"The Ghost\" has fled. PRESS ANY KEY TO CONTINUE...";
            player.isDead = true;
            GameManager.storyMode = true;
            return true;
        }
        ArrayList<Enemy> enemiesToRemove = new ArrayList<Enemy>();
        ArrayList<Player> buddiesToRemove = new ArrayList<Player>();
        for (Enemy enemy : enemies) {
            if (enemy.isDead) {
                enemiesToRemove.add(enemy);
            }
        }
        for (Player buddy : gangmembers) {
            if (buddy.isDead) {
                buddiesToRemove.add(buddy);
            }
        }
        enemies.removeAll(enemiesToRemove);
        gangmembers.removeAll(buddiesToRemove);
        if (enemies.isEmpty() || player.isDead) {
            if (enemies.isEmpty()) {
                message = "Enemy has died!";
                GameManager.storyMode = true;
            } else {
                message = player.getName().toUpperCase() + " has died! PRESS ANY KEY TO CONTINUE...";
                GameManager.storyMode = true;
            }
            return true;
        }
        return false;
    }

    public void setActorTexturesToDefault() {
        if (player.isHit) {
            player.setTexture("entities/fightingPlayer.png");
            player.isHit = false;
        }
        for (Enemy enemy : enemies) {
            if (enemy.isHit) {
                enemy.setTexture("entities/enemy.png");
                enemy.isHit = false;
            }
        }
    }

    public ArrayList<Player> getGangmembers() {
        return gangmembers;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public GraphGenerator getGraphGenerator() {
        return graphGenerator;
    }

    public void setGraphGenerator(GraphGenerator graphGenerator) {
        this.graphGenerator = graphGenerator;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void dispose() {
        player.getTexture().dispose();
        for (Enemy enemy : enemies) {
            enemy.getTexture().dispose();
        }
        slap.dispose();
        for (Bullet bullet : bullets) {
            bullet.getShot().dispose();
            bullet.getTexture().dispose();
        }
        map.dispose();
    }
}
