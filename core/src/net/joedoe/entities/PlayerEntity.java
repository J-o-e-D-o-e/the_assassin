package net.joedoe.entities;

import lombok.Getter;
import lombok.Setter;
import net.joedoe.utils.Direction;
import net.joedoe.weapons.Weapon;

import static net.joedoe.utils.GameInfo.*;

/* super class currently for player and possibly also for gangmembers of player */

@Getter
@Setter
public abstract class PlayerEntity implements MapEntity {
    protected float x, y;
    protected Direction direction;
    protected String name;
    protected int health;
    protected int strength, dexterity, intelligence;
    protected int actionPoints;
    protected Weapon[] weapons;
    protected Weapon activeWeapon;
    protected boolean turnOver, hit, severelyInjured, dead;

    public void move() {
        switch (direction) {
            case UP:
                if (y + ONE_TILE == HEIGHT)
                    return;
                y += ONE_TILE;
                break;
            case LEFT:
                if (x == 0)
                    return;
                x -= ONE_TILE;
                break;
            case DOWN:
                if (y == 0)
                    return;
                y -= ONE_TILE;
                break;
            case RIGHT:
                if (x + ONE_TILE == WIDTH)
                    return;
                x += ONE_TILE;
                break;
        }
        decreaseActionPoints(1);
    }

    public boolean checkIfShoot() {
        return activeWeapon.getRange() > 1 && activeWeapon.getLoadedAmmo() > 0 && actionPoints >= activeWeapon.getApForOneAttack();
    }

    @SuppressWarnings("Duplicates")
    public Bullet shoot() {
        int ap = activeWeapon.getApForOneAttack();
        decreaseActionPoints(ap);
        activeWeapon.playSound();
        activeWeapon.decreaseLoadedAmmo();
        int range = activeWeapon.getRange();
        int damage = activeWeapon.getRandomizedDamage();
        String name = activeWeapon.getName();
        return new Bullet(x, y, direction, range, damage, name);
    }

    public boolean checkIfHit() {
        return activeWeapon.isInFight() && actionPoints >= activeWeapon.getApForOneAttack();
    }

    public int hit() {
        int ap = activeWeapon.getApForOneAttack();
        decreaseActionPoints(ap);
        activeWeapon.playSound();
        return activeWeapon.getRandomizedDamage();
    }

    private boolean checkIfReload() {
        return activeWeapon.getLoadedAmmo() != activeWeapon.getOneRound() && activeWeapon.getAmmoStock() > 0;
    }

    public void reload() {
        if (checkIfReload()) {
            decreaseActionPoints(1);
            activeWeapon.reload();
        }
    }

    public void changeWeapon(int choice) {
        activeWeapon = weapons[choice];
    }

    void changeHealth(int health) {
        this.health += health;
        if (this.health > 100)
            this.health = 100;
        else if (this.health <= 0)
            dead = true;
    }

    public void isHitBy(int damage) {
        hit = true;
        changeHealth(-damage);
    }

    public void endTurn() {
        decreaseActionPoints(actionPoints);
    }

    public void setTurnOver(boolean turnOver) {
        if (!turnOver)
            setActionPointsToDefault();
        this.turnOver = turnOver;
    }

    private void decreaseActionPoints(int amount) {
        actionPoints -= amount;
        if (actionPoints == 0)
            this.turnOver = !turnOver;
    }

    public void setActionPointsToDefault() {
        this.actionPoints = strength / 5;
    }
}