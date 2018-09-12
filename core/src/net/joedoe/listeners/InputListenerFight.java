package net.joedoe.listeners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import net.joedoe.controllers.IPlayerController;
import net.joedoe.utils.GameManager;

public class InputListenerFight extends InputAdapter {
    private IPlayerController controller;

    public InputListenerFight(IPlayerController controller) {
        this.controller = controller;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!GameManager.isPaused && !GameManager.storyMode && !controller.isTurnOver()) {
            switch (keycode) {
                case (Input.Keys.UP):
                    controller.move(1);
                    break;
                case (Input.Keys.LEFT):
                    controller.move(2);
                    break;
                case (Input.Keys.DOWN):
                    controller.move(3);
                    break;
                case (Input.Keys.RIGHT):
                    controller.move(4);
                    break;
                case (Input.Keys.W):
                    controller.attack(1);
                    break;
                case (Input.Keys.A):
                    controller.attack(2);
                    break;
                case (Input.Keys.S):
                    controller.attack(3);
                    break;
                case (Input.Keys.D):
                    controller.attack(4);
                    break;
                case (Input.Keys.NUM_1):
                    controller.changeWeapon(0);
                    break;
                case (Input.Keys.NUM_2):
                    controller.changeWeapon(1);
                    break;
                case (Input.Keys.NUM_3):
                    controller.changeWeapon(2);
                    break;
                case (Input.Keys.NUM_4):
                    controller.changeWeapon(3);
                    break;
                case (Input.Keys.R):
                    controller.reload();
                    break;
                case (Input.Keys.E):
                    controller.endTurn();
                    break;
            }
        }
        return false;
    }
}
