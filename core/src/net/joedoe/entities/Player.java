package net.joedoe.entities;

import lombok.Getter;
import lombok.Setter;
import net.joedoe.utils.GameData;
import net.joedoe.utils.GameManager;

@Getter
@Setter
public class Player extends PlayerEntity {
    private int money;
    private int experience;
    private int gender;
    private int account;

    public Player() {
        GameData gameData = GameManager.getInstance().getGameData();
        name = gameData.getName();
        x = gameData.getX();
        y = gameData.getY();
        health = gameData.getHealth();
        strength = gameData.getStrength();
        dexterity = gameData.getDexterity();
        intelligence = gameData.getIntelligence();
        actionPoints = gameData.getActionPoints();
        weapons = gameData.getWeapons();
        weapons[1].setAmmoStock(20);
        activeWeapon = gameData.getActiveWeapon();
        money = gameData.getMoney();
        experience = gameData.getExperience();
        gender = gameData.getGender();
    }
}
