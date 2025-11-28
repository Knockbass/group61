package nl.saxion.game.yourgamename.game_managment;

import nl.saxion.game.yourgamename.collision.*;
import nl.saxion.game.yourgamename.movement.*;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;
import nl.saxion.game.yourgamename.entities.*;

public class YourGameScreen extends ScalableGameScreen {
    public static int worldWidth = 1280;
    public static int worldHeight = 720;

    public static Player player;
    private Box box;
    private EnemySpawner enemySpawner;

    public YourGameScreen() {
        super(worldWidth, worldHeight);
    }

    @Override
    public void show() {
        GameApp.addTexture("player", "textures/bear.png");
        GameApp.addTexture("enemy", "textures/crocodile.png");
        GameApp.addTexture("box", "textures/rhino.png");

        player = new Player("test", 100, 10, 5, 300);
        box = new Box(200, 100);
        enemySpawner = new EnemySpawner(worldWidth, worldHeight);

        CollisionManager.addEntity(player);
        CollisionManager.addEntity(box);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        GameApp.clearScreen("teal-300");

        // Update player movement
        PlayerMovement.checkMovementKeyPressed(player, delta);
        WorldBorder.clampToWorldBounds(player, worldWidth, worldHeight);

        // Update enemy spawning
        enemySpawner.update(delta, player);

        // Update enemy movement
        EnemyMovement.updateEnemies(enemySpawner.getEnemies(), player, delta, worldWidth, worldHeight);

        // Handle collisions
        CollisionManager.checkCollision();

        // Render all entities
        renderEntities();
    }
    private void renderEntities() {
        GameApp.startSpriteRendering();
        GameApp.drawTexture("box", box.getX(), box.getY());
        GameApp.drawTexture("player", player.position.getX(), player.position.getY(), player.getWidth(), player.getHeight());

        for (Yapper enemy : enemySpawner.getEnemies()) {
            GameApp.drawTexture("enemy", enemy.position.getX(), enemy.position.getY(), enemy.getWidth(), enemy.getHeight());
        }

        GameApp.endSpriteRendering();
    }

    @Override
    public void hide() {
        GameApp.disposeTexture("player");
        GameApp.disposeTexture("enemy");
        GameApp.disposeTexture("box");
    }
}
