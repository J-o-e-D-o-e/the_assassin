package net.joedoe.entities;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import lombok.Setter;
import net.joedoe.pathfinding.ManhattanHeuristic;
import net.joedoe.pathfinding.Node;
import net.joedoe.weapons.Hands;
import net.joedoe.weapons.Weapon;

@Getter
@Setter
public class Enemy extends EnemyEntity {
    int health, strength, actionPoints;
    private Weapon[] weapons;
    private Weapon activeWeapon;
    protected boolean moved, hit, severelyInjured, dead;
    private boolean pathStraightFromCurrentNodeToPlayer, pathStraightFromNextNodeToPlayer;

    public Enemy(String name, float x, float y, int health, int strength, Weapon weapon) {
        texture = new Texture("entities/enemy.png");
        this.name = name;
        this.x = x;
        this.y = y;
        this.health = health;
        this.strength = strength;
        actionPoints = strength / 5;
        weapons = new Weapon[2];
        weapons[0] = new Hands();
        weapons[1] = weapon;
        activeWeapon = weapons[1];
        activeWeapon.setLoadedAmmo(weapons[1].getOneRound());// fully loaded
        activeWeapon.setAmmoStock(activeWeapon.getOneRound()); // +1 round
        path = new DefaultGraphPath<>();
        heuristic = new ManhattanHeuristic();
    }

    @Override
    public void move() {
        super.move();
        decreaseActionPointsBy(1);
    }


    @SuppressWarnings("Duplicates")
    public Bullet shoot() {
        activeWeapon.playSound();
        activeWeapon.decreaseLoadedAmmo();
        int range = activeWeapon.getRange();
        int damage = activeWeapon.getRandomizedDamage();
        String name = activeWeapon.getName();
        return new Bullet(x, y, direction, range, damage, name);
    }

    public int hit() {
        if (decreaseActionPointsBy(activeWeapon.getApForOneShot())) {
            activeWeapon.playSound();
            return activeWeapon.getRandomizedDamage();
        }
        return 0;
    }

    public void reload() {
        if (activeWeapon.getLoadedAmmo() != activeWeapon.getOneRound() && activeWeapon.getAmmoStock() > 0) {
            decreaseActionPointsBy(1);
            activeWeapon.reload();
        }
    }

    public void endTurn() {
        decreaseActionPointsBy(actionPoints);
    }

    private boolean decreaseActionPointsBy(int amount) {
        if (actionPoints >= amount) {
            actionPoints -= amount;
            if (actionPoints == 0) {
                moved = true;
            }
            return true;
        }
        return false;
    }

    public void isHitBy(int damage) {
        hit = true;
        changeHealth(damage);
        setTexture(new Texture("entities/actorIsHit.png"));
    }

    @SuppressWarnings("Duplicates")
    private void changeHealth(int health) {
        this.health += health;
        if (this.health > 100) {
            this.health = 100;
        } else if (this.health <= 0) {
            this.health = 0;
            dead = true;
        }
    }

    public void setMoved(boolean moved) {
        if (!moved) {
            this.actionPoints = strength / 5;
        }
        this.moved = moved;
    }

    public boolean checkIfPossibleToShoot() {
        return activeWeapon.getRange() > 1 && activeWeapon.getLoadedAmmo() > 0 && decreaseActionPointsBy(activeWeapon.getApForOneShot());
    }

    public void checkIfPathStraightFromCurrentNodeToPlayer() {
        Node currentNode = getCurrentNode();
        Node nextNode = getNextNode();
        if (nextNode.getY() > currentNode.getY() || nextNode.getY() < currentNode.getY()) {
            for (int i = pathIndex - 1; i < path.getCount(); i++) {
                if (path.get(i).getX() != currentNode.getX()) {
                    pathStraightFromCurrentNodeToPlayer = false;
                    break;
                }
            }
        } else {
            for (int i = pathIndex - 1; i < path.getCount(); i++) {
                if (path.get(i).getY() != currentNode.getY()) {
                    pathStraightFromCurrentNodeToPlayer = false;
                    break;
                }
            }
        }
    }

    public void checkIfPathStraightFromNextNodeToPlayer() {
        pathStraightFromCurrentNodeToPlayer = true;
        pathStraightFromNextNodeToPlayer = true;
        Node nextNode = getNextNode();
        Node afterNextNode = getAfterNextNode();
        if (afterNextNode != null) {
            if (afterNextNode.getY() > nextNode.getY() || afterNextNode.getY() < nextNode.getY()) {
                for (int i = pathIndex; i < path.getCount(); i++) {
                    if (path.get(i).getX() != nextNode.getX()) {
                        pathStraightFromNextNodeToPlayer = false;
                        pathStraightFromCurrentNodeToPlayer = false;
                        break;
                    }
                }
            } else {
                for (int i = pathIndex; i < path.getCount(); i++) {
                    if (path.get(i).getY() != nextNode.getY()) {
                        pathStraightFromNextNodeToPlayer = false;
                        pathStraightFromCurrentNodeToPlayer = false;
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public boolean checkIfNotEnoughAPsToDoAnything() {
        return actionPoints == 0 && !checkIfInShootingRange() && !checkIfReloadPossibleAndNecessary() && checkIfNotEnoughAPsForShootingAfterNextStep();
    }

    public boolean checkIfInShootingRange() {
        return pathStraightFromCurrentNodeToPlayer && calculateDistance() <= activeWeapon.getRange() && actionPoints >= activeWeapon.getApForOneShot();
    }

    public boolean checkIfReloadPossibleAndNecessary() {
        return activeWeapon.getLoadedAmmo() <= 2 && activeWeapon.getAmmoStock() > 0;
    }

    public boolean checkIfNotEnoughAPsForShootingAfterNextStep() {
        return pathStraightFromNextNodeToPlayer && calculateDistance() <= activeWeapon.getRange() + 1 && actionPoints < activeWeapon.getApForOneShot() + 1;
    }

    public boolean checkIfDistanceGreaterOne() {
        return calculateDistance() > 1;
    }

    private Node getAfterNextNode() {
        if (path.nodes.size > pathIndex + 1) {
            return path.nodes.get(pathIndex + 1);
        }
        return null;
    }

    public void renderPath(ShapeRenderer shapeRenderer) {
        path.forEach(node -> node.renderLine(shapeRenderer));
    }
}