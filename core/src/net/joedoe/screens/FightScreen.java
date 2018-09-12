package net.joedoe.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.Getter;
import net.joedoe.GameMain;
import net.joedoe.controllers.FightController;
import net.joedoe.controllers.PlayerController;
import net.joedoe.entities.MapEntity;
import net.joedoe.gui_elements.FightPanel;
import net.joedoe.gui_elements.PausePanel;
import net.joedoe.gui_elements.StoryPanel;
import net.joedoe.listeners.InputListenerFight;
import net.joedoe.listeners.PauseListener;
import net.joedoe.listeners.StoryListener;
import net.joedoe.pathfinding.Node;
import net.joedoe.utils.GameManager;

import java.util.List;

import static net.joedoe.utils.GameInfo.*;

@SuppressWarnings("WeakerAccess")
@Getter
public class FightScreen implements IScreen {
    protected GameMain game;
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected OrthogonalTiledMapRenderer mapRenderer;
    protected ShapeRenderer shapeRenderer;
    protected Texture playerTexture;
    protected Texture enemyTexture;
    protected Texture bulletVerticalTexture;
    protected Texture bulletHorizontalTexture;
    protected Texture hitTexture;
    protected FightPanel fightPanel;
    protected PausePanel pausePanel;
    protected StoryPanel storyPanel;
    protected PauseListener pauseListener;
    protected StoryListener storyListener;
    protected FightController fightController;
    protected PlayerController playerController;
    protected float enemyTimer, bulletTimer;

    public FightScreen(GameMain game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.position.set(WIDTH / 2f, HEIGHT / 2f, 0);
        viewport = new StretchViewport(WIDTH, HEIGHT, camera);
        fightPanel = new FightPanel(this.game);
        pausePanel = new PausePanel(this.game);
        pauseListener = new PauseListener(this);
        storyPanel = new StoryPanel(this.game);
        storyListener = new StoryListener(this);
        playerTexture = new Texture("entities/fightingPlayer.png");
        enemyTexture = new Texture("entities/enemy.png");
        bulletVerticalTexture = new Texture("entities/bullet_vertical.png");
        bulletHorizontalTexture = new Texture("entities/bullet_horizontal.png");
        hitTexture = new Texture("entities/actorIsHit.png");
        fightController = new FightController();
        playerController = new PlayerController(fightController);
        mapRenderer = new OrthogonalTiledMapRenderer(fightController.getMap(), SCALE);
        shapeRenderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(new InputListenerFight(playerController));
    }

    @Override
    public void show() {
        storyPanel.show(GameManager.storyStage);
    }

    @Override
    public void render(float delta) {
        updateScreen(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapRenderer.render();
        mapRenderer.setView(camera);
        game.getBatch().begin();
        renderPlayer();
        renderEnemies();
        renderBullets();
        game.getBatch().end();
//        renderEnemyPaths();
        game.getBatch().setProjectionMatrix(fightPanel.getStage().getCamera().combined);
        storyPanel.getStage().draw();
        pausePanel.getStage().draw();
        fightPanel.getStage().draw();
    }

    protected void updateScreen(float delta) {
        storyListener.handleUserInput();
        pauseListener.handleUserInput();
        if (!GameManager.storyMode && !GameManager.isPaused) {
            fightPanel.update(fightController.getMessage());
            if (!playerController.isTurnOver() && fightController.enemiesTurnOver())
                fightController.enemiesReset();
            bulletTimer += delta;
            if (bulletTimer >= 0.1 && !fightController.bulletsIsEmpty()) {
                bulletTimer = 0;
                fightController.updateBullets();
            }
            enemyTimer += delta;
            if (enemyTimer >= 0.2 && playerController.isTurnOver() && fightController.bulletsIsEmpty()) {
                enemyTimer = 0;
                fightController.updateEnemies();
            }
            if (playerController.isDead()) {
                dispose();
                game.setScreen(new GameOver(game));
            }
            if (fightController.enemiesAreDead()) {
                dispose();
                game.setScreen(new Win(game));
            }
        }
    }

    private void renderPlayer() {
        MapEntity player = fightController.getPlayer();
        if (playerController.isHit())
            game.getBatch().draw(hitTexture, player.getX(), player.getY(), ONE_TILE, ONE_TILE);
        else
            game.getBatch().draw(playerTexture, player.getX(), player.getY(), ONE_TILE, ONE_TILE);
    }

    private void renderEnemies() {
        List<? extends MapEntity> enemies = fightController.getEnemies();
        List<? extends MapEntity> enemiesHit = fightController.getEnemiesHit();
        for (MapEntity enemy : enemies)
            if (enemiesHit.contains(enemy))
                game.getBatch().draw(hitTexture, enemy.getX(), enemy.getY(), ONE_TILE, ONE_TILE);
            else
                game.getBatch().draw(enemyTexture, enemy.getX(), enemy.getY(), ONE_TILE, ONE_TILE);
    }

    private void renderBullets() {
        List<? extends MapEntity> bullets = fightController.getBullets();
        if (!bullets.isEmpty())
            for (MapEntity bullet : bullets)
                if (bullet.getDirection() == 1 || bullet.getDirection() == 3)
                    game.getBatch().draw(bulletVerticalTexture, bullet.getX(), bullet.getY(), ONE_TILE, ONE_TILE);
                else
                    game.getBatch().draw(bulletHorizontalTexture, bullet.getX(), bullet.getY(), ONE_TILE, ONE_TILE);
    }

    @SuppressWarnings("unused")
    private void renderEnemyPaths() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(new Color(RED[0], RED[1], RED[2], 1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (DefaultGraphPath<Node> path : fightController.getPaths())
            for (Node node : path) {
                int x = node.getX() * ONE_TILE;
                int y = node.getY() * ONE_TILE;
                shapeRenderer.line(x, y, x, y + ONE_TILE);
                shapeRenderer.line(x + ONE_TILE, y, x + ONE_TILE, y + ONE_TILE);
                shapeRenderer.line(x, y, x + ONE_TILE, y);
                shapeRenderer.line(x, y + ONE_TILE, x + ONE_TILE, y + ONE_TILE);
            }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void showPausePanel() {
        pausePanel.show();
    }

    @Override
    public void removePausePanel() {
        pausePanel.remove();
    }

    @Override
    public void removeStoryPanel() {
        storyPanel.remove();
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
        fightController.dispose();
        fightPanel.dispose();
        pausePanel.dispose();
        storyPanel.dispose();
        playerTexture.dispose();
        enemyTexture.dispose();
        bulletVerticalTexture.dispose();
        bulletHorizontalTexture.dispose();
        hitTexture.dispose();
    }
}