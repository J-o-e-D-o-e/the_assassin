package net.joedoe.entities;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import lombok.Getter;
import lombok.Setter;
import net.joedoe.pathfinding.Graph;
import net.joedoe.pathfinding.Node;
import net.joedoe.weapons.Hands;
import net.joedoe.weapons.Weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class Enemy extends EnemyEntity {
    private int health, strength, actionPoints;
    private Weapon[] weapons;
    private Weapon activeWeapon;
    private boolean turnOver, hit, severelyInjured, dead;
    private boolean pathStraightFromCurrentNode, pathStraightFromNextNode;

    Enemy(String name, float x, float y, int health, int strength, Weapon weapon) {
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
        activeWeapon.setLoadedAmmo(activeWeapon.getOneRound()); // fully loaded
        activeWeapon.setAmmoStock(activeWeapon.getOneRound()); // +1 round
    }

    @Override
    public void move() {
        super.move();
        actionPoints -= 1;
    }

    public boolean checkIfShoot() {
        return pathStraightFromCurrentNode && activeWeapon.getRange() >= calculateDistance()
                && activeWeapon.getLoadedAmmo() > 0 && actionPoints >= activeWeapon.getApForOneAttack();
    }

    public Bullet shoot() {
        actionPoints -= activeWeapon.getApForOneAttack();
        activeWeapon.playSound();
        activeWeapon.decreaseLoadedAmmo();
        int range = activeWeapon.getRange();
        int damage = activeWeapon.getRandomizedDamage();
        String name = activeWeapon.getName();
        return new Bullet(x, y, direction, range, damage, name);
    }

    public boolean checkIfHit() {
        return calculateDistance() == 1 && actionPoints >= weapons[0].getApForOneAttack();
    }

    public int hit() {
        actionPoints -= activeWeapon.getApForOneAttack();
        activeWeapon.playSound();
        return activeWeapon.getRandomizedDamage();
    }

    public boolean checkIfReload() {
        return activeWeapon.getLoadedAmmo() <= 2 && activeWeapon.getAmmoStock() > 0;
    }

    public void reload() {
        actionPoints -= 1;
        activeWeapon.reload();
    }

    public boolean checkIfChangeWeapon() {
        return activeWeapon.isInFight();
    }

    public void changeWeapon(int weapon) {
        activeWeapon = weapons[weapon];
    }

    public boolean checkIfEndTurn() {
        return pathStraightFromNextNode && calculateDistance() - 1 <= activeWeapon.getRange() && actionPoints - 1 <= activeWeapon.getApForOneAttack();
    }

    public void endTurn() {
        actionPoints = 0;
        pathIndex = 1;
        path.clear();
        turnOver = true;
    }

    public void reset() {
        turnOver = false;
        actionPoints = strength / 5;
    }

    public void isHitBy(int damage) {
        health -= damage;
        if (health <= 0) {
            dead = true;
            actionPoints = 0;
        } else
            hit = true;
    }

    public void calculatePath(Graph graph, MapEntity player, List<Enemy> enemies) {
        ArrayList<Enemy> otherEnemies = enemies.stream().filter(enemy -> enemy != this).collect(Collectors.toCollection(ArrayList::new));
        for (Enemy enemy : otherEnemies) {
            Node enemyNode = graph.getNodeByCoordinates(enemy.getX(), enemy.getY());
            for (Node node : graph.getNodes())
                if (enemyNode == node)
                    node.getConnections().clear();
        }
        Node startNode = graph.getNodeByCoordinates(x, y);
        Node endNode = graph.getNodeByCoordinates(player.getX(), player.getY());
        new IndexedAStarPathFinder<>(graph, true).searchNodePath(startNode, endNode, heuristic, path);
    }

    public boolean checkIfPathStraightFromCurrentNode() {
        Node currentNode = getCurrentNode();
        Node nextNode = getNextNode();
        pathStraightFromCurrentNode = checkIfPathStraight(currentNode, nextNode);
        if (pathStraightFromCurrentNode)
            pathStraightFromNextNode = true;
        return pathStraightFromCurrentNode;
    }

    public void checkIfPathStraightFromNextNode() {
        Node nextNode = getNextNode();
        Node afterNextNode = getAfterNextNode();
        pathStraightFromNextNode = checkIfPathStraight(nextNode, afterNextNode);
    }

    private boolean checkIfPathStraight(Node startNode, Node endNode) {
        if (endNode != null && endNode.getY() != startNode.getY()) {
            for (int i = pathIndex; i < path.getCount(); i++)
                if (path.get(i).getX() != startNode.getX())
                    return false;
        } else if (endNode != null && endNode.getX() != startNode.getX()) {
            for (int i = pathIndex; i < path.getCount(); i++)
                if (path.get(i).getY() != startNode.getY())
                    return false;
        }
        return true;
    }

    private Node getAfterNextNode() {
        if (path.nodes.size > pathIndex + 1)
            return path.nodes.get(pathIndex + 1);
        return null;
    }
}