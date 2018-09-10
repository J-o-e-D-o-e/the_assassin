package net.joedoe.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.joedoe.GameMain;
import net.joedoe.utils.GameInfo;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "The Assassin";
        // config.fullscreen = true;
        config.addIcon("icons/icon_128.png", FileType.Internal);
        config.addIcon("icons/icon_32.png", FileType.Internal);
        config.addIcon("icons/icon_16.png", FileType.Internal);
        config.width = GameInfo.WIDTH;
        config.height = GameInfo.HEIGHT;
        config.foregroundFPS = 30;
        new LwjglApplication(new GameMain(), config);
    }
}