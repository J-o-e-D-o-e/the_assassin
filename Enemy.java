package net.joedoe.entities;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.Texture;

import net.joedoe.entities.weapons.Hands;
import net.joedoe.entities.weapons.Weapon;
import net.joedoe.logics.pathfinding.Graph;
import net.joedoe.logics.pathfinding.ManhattanHeuristic;
import net.joedoe.logics.pathfinding.Node;

public class Enemy extends Actor {
    public Graph graph;
    public DefaultGraphPath<Node> path;
    public IndexedAStarPathFinder<Node> pathfinder;
    public ManhattanHeuristic heuristic;
    public int pathIndex = 1;

    public Enemy(String name, float x, float y, int health, int strength, Weapon weapon) {
        texture = new Texture("entities/enemy.png");
        this.name = name;
        this.x = x;
        this.y = y;
        this.health = health;
        this.strength = strength;
        dexterity = 15;
        intelligence = 10;
        actionPoints = strength / 5;
        weapons = new Weapon[2];
        weapons[0] = new Hands();
        weapons[1] = weapon;
        activeWeapon = weapons[1];
        activeWeapon.setLoadedAmmo(weapons[1].getOneRound());// fully loaded
        activeWeapon.setAmmoStock(activeWeapon.getOneRound()); // +1 round
        path = new DefaultGraphPath<Node>();
        heuristic = new ManhattanHeuristic();
    }

    public boolean calculatePath(Node startNode, Node endNode) {
        pathIndex = 1;
        path.clear();
        return pathfinder.searchNodePath(startNode, endNode, heuristic, path);
    }

    public Node getCurrentNode() {
        return path.nodes.get(pathIndex - 1);
    }

    public Node getNextNode() {
        return path.nodes.get(pathIndex);
    }

    public Node getAfterNextNode() {
        if (path.nodes.size > pathIndex + 1) {
            return path.nodes.get(pathIndex + 1);
        }
        return null;
    }
}
