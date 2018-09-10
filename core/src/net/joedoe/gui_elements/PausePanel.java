package net.joedoe.gui_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.Getter;
import net.joedoe.GameMain;
import net.joedoe.utils.GameInfo;
import net.joedoe.utils.GameManager;

@Getter
public class PausePanel {
    protected Stage stage;
    protected Table table;
    protected Skin skin;

    public PausePanel(GameMain game) {
        Viewport viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("fonts/uiskin.json"));
    }

    public void show() {
        GameManager.isPaused = true;
        stage.addActor(createTable());
    }

    public void remove() {
        GameManager.isPaused = false;
        table.remove();
    }

    private Table createTable() {
        table = new Table(skin);
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("images/panel.png")));
        table.setBackground(textureRegionDrawable);
        table.setFillParent(true);
        table.top().center();
        table.add("-----------PAUSED-----------");
        table.row();
        table.add(GameInfo.CONTROLS);
        return table;
    }

    public void dispose() {
        skin.dispose();
        stage.dispose();
    }
}
