package net.joedoe.screens;

import com.badlogic.gdx.Screen;

public interface IScreen extends Screen {

    void showPausePanel();

    void removePausePanel();

    void removeStoryPanel();
}
