package net.joedoe.gui_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.Getter;
import net.joedoe.GameMain;
import net.joedoe.utils.GameInfo;
import net.joedoe.utils.GameManager;

import java.io.IOException;

@Getter
public class StoryPanel {
    protected Stage stage;
    protected Table table;
    protected Skin skin;
    private Element root;

    public StoryPanel(GameMain game) {
        Viewport viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("fonts/uiskin.json"));
        try {
            root = new XmlReader().parse(Gdx.files.internal("story.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void show(int storyStage) {
        GameManager.storyMode = true;
        stage.addActor(createTable(storyStage));
    }

    public void remove() {
        GameManager.storyMode = false;
        table.remove();
    }

    private Table createTable(int storyStage) {
        table = new Table(skin);
        // table.debug();
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("images/panel.png")));
        table.setBackground(textureRegionDrawable);
        table.setFillParent(true);
        Label label = new Label(getContent(storyStage), skin);
        label.setWrap(true);
        table.add(label).left().width(600);
        return table;
    }

    private String getContent(int storyStage) {
        String[] heShe = new String[]{"he", "she"};
        String[] himHer = new String[]{"him", "her"};
        String[] hisHer = new String[]{"his", "her"};
        int gender = GameManager.player.getGender();
        try {
            return root.get("stage" + storyStage).replaceAll("\\s+", " ").replaceAll("NAME", GameManager.player.getName())
                    .replaceAll("HESHE", heShe[gender]).replaceAll("HIMHER", himHer[gender])
                    .replaceAll("HISHER", hisHer[gender]) + " \n\nPRESS SPACE TO CONTINUE...";
        } catch (Exception e) {
            return "PRESS SPACE TO CONTINUE...";
        }
    }

    public void dispose() {
        skin.dispose();
        stage.dispose();
    }
}
