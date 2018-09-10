package net.joedoe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import net.joedoe.GameMain;

import static net.joedoe.utils.GameInfo.*;

public class Win extends GameOver {

    Win(GameMain game) {
        super(game);
        title.setColor(RED[0], RED[1], RED[2], 1);
    }

    @Override
    public void show() {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/win.wav"));
        sound.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(GREEN[0], GREEN[1], GREEN[2], 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        title.draw(game.getBatch(), "You win!", WIDTH / 2f - 150, HEIGHT / 2f + 25);
        game.getBatch().end();
        stage.draw();
        stage.act();
    }
}