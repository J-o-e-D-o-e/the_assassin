package net.joedoe.gui_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.Getter;
import net.joedoe.GameMain;
import net.joedoe.entities.Player;
import net.joedoe.utils.GameInfo;
import net.joedoe.utils.GameManager;

@Getter
public class FightPanel {
    protected Stage stage;
    protected Table table;
    protected Skin skin;
    public Player player = GameManager.player;
    private Label message, health, ap, ammoStock, weapon, ammo;

    public FightPanel(GameMain game) {
        Viewport viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("fonts/uiskin.json"));
        createTable();
    }

    private void createTable() {
        message = new Label("", skin);
        Label name = new Label(player.getName().toUpperCase(), skin);
        health = new Label("", skin);
        ap = new Label("", skin);
        weapon = new Label("", skin);
        Label stats = new Label("", skin);
        stats.setText("S/D/I: " + player.getStrength() + "/" + player.getDexterity() + "/" + player.getIntelligence());
        ammoStock = new Label("", skin);
        weapon = new Label("", skin);
        ammo = new Label("", skin);
        table = new Table(skin);
        // table.setDebug(true);
        table.bottom().left();
        table.pad(10);
        table.setFillParent(true);
        table.add(message).left().width(200);
        table.row().padTop(12);
        table.add(name).left();
        table.row().padTop(5).expandX();
        table.add(health).left();
        table.add(ap).left().padLeft(20);
        table.add(weapon).left().padLeft(20).width(200);
        table.row().padTop(5);
        table.add(stats).left();
        table.add(ammoStock).left().padLeft(20).width(200);
        table.add(ammo).left().padLeft(20);
        stage.addActor(table);
    }

    public void update(String message) {
        this.message.setText(message);
        health.setText("Health: " + player.getHealth() + " %");
        ap.setText("Action points: " + player.getActionPoints() + "/" + player.getStrength() / 5);
        weapon.setText("Weapon: " + player.getActiveWeapon().getName() + " (" + player.getActiveWeapon().getApForOneAttack() + " AP)");
        if (player.getActiveWeapon().getOneRound() == 0) {
            ammoStock.setText("");
            ammo.setText("");
        } else {
            ammoStock.setText("Ammo stock: " + player.getActiveWeapon().getAmmoStock());
            ammo.setText("Ammo: " + player.getActiveWeapon().getLoadedAmmo() + "/" + player.getActiveWeapon().getOneRound());
        }
    }

    public void dispose() {
        skin.dispose();
        stage.dispose();
    }
}
