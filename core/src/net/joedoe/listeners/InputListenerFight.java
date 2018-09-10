package net.joedoe.listeners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import net.joedoe.controllers.FightController;
import net.joedoe.utils.GameManager;

public class InputListenerFight extends InputAdapter {
    private FightController controller;

    public InputListenerFight(FightController controller) {
        this.controller = controller;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!GameManager.isPaused && !GameManager.storyMode && !controller.playersTurnOver()) {
            switch (keycode) {
                case (Input.Keys.UP):
                    controller.playerMoves(1);
                    break;
                case (Input.Keys.LEFT):
                    controller.playerMoves(2);
                    break;
                case (Input.Keys.DOWN):
                    controller.playerMoves(3);
                    break;
                case (Input.Keys.RIGHT):
                    controller.playerMoves(4);
                    break;
                case (Input.Keys.W):
                    controller.playerAttacks(1);
                    break;
                case (Input.Keys.A):
                    controller.playerAttacks(2);
                    break;
                case (Input.Keys.S):
                    controller.playerAttacks(3);
                    break;
                case (Input.Keys.D):
                    controller.playerAttacks(4);
                    break;
                case (Input.Keys.NUM_1):
                    controller.playerChangesWeapon(0);
                    break;
                case (Input.Keys.NUM_2):
                    controller.playerChangesWeapon(1);
                    break;
                case (Input.Keys.NUM_3):
                    controller.playerChangesWeapon(2);
                    break;
                case (Input.Keys.NUM_4):
                    controller.playerChangesWeapon(3);
                    break;
                case (Input.Keys.R):
                    controller.playerReloads();
                    break;
                case (Input.Keys.E):
                    controller.playerEndsTurn();
                    break;
            }
        }
        return false;
    }
}
