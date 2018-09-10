package net.joedoe.utils;

import lombok.Getter;
import net.joedoe.entities.Player;
import net.joedoe.weapons.Hands;
import net.joedoe.weapons.Pistol;

@Getter
public class GameManager {
    private GameData gameData = new GameData();
    public static Player player;
    public static boolean isPaused, storyMode;
    public static int storyStage = 0;
    private static GameManager gameManager = new GameManager();

    public static GameManager getInstance() {
        return gameManager;
    }

    public void initializeGameData(String name, int gender) {
        gameData.setName(name.toUpperCase());
        gameData.setGender(gender);
        gameData.setX(28 * GameInfo.ONE_TILE);
        gameData.setY(22 * GameInfo.ONE_TILE);
        gameData.setHealth(100);
        gameData.setStrength(30);
        gameData.setDexterity(20);
        gameData.setIntelligence(15);
        gameData.setActionPoints(gameData.getStrength() / 5);
        gameData.setMoney(100);
        gameData.setExperience(0);
        gameData.getWeapons()[0] = new Hands();
        gameData.getWeapons()[1] = new Pistol();
        gameData.setActiveWeapon(gameData.getWeapons()[1]);
        gameData.setAccount(0);
    }

    @SuppressWarnings("unused")
    public void saveData() {
        gameData.setName(player.getName());
        gameData.setHealth(player.getHealth());
        gameData.setStrength(player.getStrength());
        gameData.setDexterity(player.getDexterity());
        gameData.setIntelligence(player.getIntelligence());
        gameData.setActionPoints(player.getActionPoints());
        gameData.setMoney(player.getMoney());
        gameData.setExperience(player.getExperience());
        gameData.setWeapons(player.getWeapons());
        gameData.setActiveWeapon(player.getActiveWeapon());
        gameData.setAccount(player.getAccount());
    }
}
