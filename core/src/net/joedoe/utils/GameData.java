package net.joedoe.utils;

import lombok.Getter;
import lombok.Setter;
import net.joedoe.weapons.Weapon;

/* for persistent storage */

@Getter
@Setter
public class GameData {
    private String name;
    private float x, y;
    private int health, strength, dexterity, intelligence, money, experience, actionPoints, gender, account;
    private Weapon[] weapons = new Weapon[4];
    private Weapon activeWeapon;
}