package net.joedoe.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import net.joedoe.screens.IScreen;
import net.joedoe.utils.GameManager;

public class PauseListener {
    private IScreen screen;

    public PauseListener(IScreen screen) {
        this.screen = screen;
    }

    public void handleUserInput() {
        if (!GameManager.isPaused && !GameManager.storyMode && Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            screen.showPausePanel();
            return;
        }
        if (GameManager.isPaused && !GameManager.storyMode && Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            screen.removePausePanel();
        }
    }
}
