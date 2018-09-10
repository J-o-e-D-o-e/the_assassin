package net.joedoe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.joedoe.GameMain;

import static net.joedoe.utils.GameInfo.*;

public class GameOver implements Screen {
    protected GameMain game;
    protected Viewport viewport;
    protected Stage stage;
    BitmapFont title;

    GameOver(GameMain game) {
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.position.set(WIDTH / 2f, HEIGHT / 2f, 0);
        viewport = new StretchViewport(WIDTH, HEIGHT, camera);
        title = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));
        title.setColor(GREY[0], GREY[1], GREY[2], 1);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        createTable();
    }

    @SuppressWarnings("Duplicates")
    private void createTable() {
        Skin skin = new Skin(Gdx.files.internal("fonts/uiskin.json"));
        Table table = new Table(skin);
        table.setPosition(0, -50);
        table.setFillParent(true);
        stage.addActor(table);
    }

    @Override
    public void show() {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/gameOver.wav"));
        sound.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(RED[0], RED[1], RED[2], 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        title.draw(game.getBatch(), "Game Over!", WIDTH / 2f - 150, HEIGHT / 2f + 25);
        game.getBatch().end();
        stage.draw();
        stage.act();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        title.dispose();
    }
}