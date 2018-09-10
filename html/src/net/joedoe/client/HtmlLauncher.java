package net.joedoe.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import net.joedoe.GameMain;
import net.joedoe.utils.GameInfo;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        return new GwtApplicationConfiguration(GameInfo.WIDTH, GameInfo.HEIGHT);
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new GameMain();
    }
}