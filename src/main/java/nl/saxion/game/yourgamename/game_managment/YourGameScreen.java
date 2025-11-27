package nl.saxion.game.yourgamename.game_managment;

import nl.saxion.game.yourgamename.collision.*;
import nl.saxion.game.yourgamename.movement.*;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;
import nl.saxion.game.yourgamename.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class YourGameScreen extends ScalableGameScreen {
    public static int worldWidth = 1280;
    public static int worldHeight = 720;
    Player player;
    Yapper yapper;
    Yapper yapper2;
    Box box;
    
    // Enemy spawning
    private float spawnTimer;
    private float spawnInterval = 3.0f; // seconds
    private int baseMaxEnemies = 1; // Base max enemies when mental health is at 100
    private List<Yapper> spawnedEnemies;
    private Random random;


    public YourGameScreen() {
        super(worldWidth, worldHeight);
    }

    @Override
    public void show() {
        GameApp.addTexture("player", "textures/bear.png");
        GameApp.addTexture("yapper2", "textures/crocodile.png");
        GameApp.addTexture("yapper", "textures/crocodile.png");
        GameApp.addTexture("box", "textures/rhino.png");
        player = new Player("test", 100, 10, 5, 300);
        yapper2 = new Yapper("testCollision", 100, 20, 5, 200, 300.0f);
        yapper2.setX(700);
        yapper2.setY(300);
        box = new Box(200, 100);
        yapper = new Yapper("common", 100, 10, 100, 200, 300.0f);
        CollisionManager.addEntity(player);
        CollisionManager.addEntity(yapper2);
        CollisionManager.addEntity(yapper);
        CollisionManager.addEntity(box);
        
        // Initialize enemy spawning
        spawnedEnemies = new ArrayList<>();
        random = new Random();
        spawnTimer = 0.0f;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        GameApp.clearScreen("teal-300");

        // Update player movement
        PlayerMovement.checkMovementKeyPressed(player, delta);
        
        // Update enemy spawning
        spawnTimer += delta;
        spawnedEnemies.removeIf(enemy -> enemy.getHealth() <= 0);
        
        // Calculate max enemies based on mental health (lower mental health = more enemies)
        // Formula: maxEnemies = baseMaxEnemies * (1 + (100 - mentalHealth) / 100.0)
        // At 100 mental health: 1x (10 enemies), At 0 mental health: 2x (20 enemies)
        int mentalHealth = player.getPlayerStats().getMentalHealth();
        int maxEnemies = Math.round(baseMaxEnemies * (1.0f + (100.0f - mentalHealth) / 100.0f));
        
        if (spawnTimer >= spawnInterval && spawnedEnemies.size() < maxEnemies) {
            spawnEnemy();
            spawnTimer = 0.0f;
        }
        
        // Move enemies towards player if in range
        yapper.moveTowardsPlayer(player, delta);
        yapper2.moveTowardsPlayer(player, delta);
        for (Yapper enemy : spawnedEnemies) {
            enemy.moveTowardsPlayer(player, delta);
        }
        
        // Clamp entities to world bounds
        WorldBorder.clampToWorldBounds(yapper2, worldWidth, worldHeight);
        WorldBorder.clampToWorldBounds(player, worldWidth, worldHeight);
        WorldBorder.clampToWorldBounds(yapper, worldWidth, worldHeight);
        for (Yapper enemy : spawnedEnemies) {
            WorldBorder.clampToWorldBounds(enemy, worldWidth, worldHeight);
        }


         for (int i = 0; i < CollisionManager.dynamic.size(); i++) {    //implement collision for pushable entity in the list
            Collidable a = CollisionManager.dynamic.get(i);

            for (int j = i + 1; j < CollisionManager.dynamic.size(); j++) {
                Collidable b = CollisionManager.dynamic.get(j);
                Collision.isColliding(a, b);
            }
        }

        for (Collidable a : CollisionManager.dynamic) {        //implement collision for unPushable entity in the list
            for (Collidable s : CollisionManager.solid) {
                Collision.isColliding(a, s);
            }
        }

        GameApp.startSpriteRendering();
        GameApp.drawTexture("box", box.getX(), box.getY());
        GameApp.drawTexture("player", player.position.getX(), player.position.getY(), player.getWidth(), player.getHeight());
        GameApp.drawTexture("yapper", yapper.position.getX(), yapper.position.getY(), yapper.getWidth(), yapper.getHeight());
        GameApp.drawTexture("yapper2", yapper2.position.getX(), yapper2.position.getY(), yapper2.getWidth(), yapper2.getHeight());
        
        // Render spawned enemies
        for (Yapper enemy : spawnedEnemies) {
            GameApp.drawTexture("yapper", enemy.position.getX(), enemy.position.getY(), enemy.getWidth(), enemy.getHeight());
        }
        
        GameApp.endSpriteRendering();
    }
    
    /**
     * Spawns a new enemy at a random position on the edge of the world
     */
    private void spawnEnemy() {
        int x, y;
        int edge = random.nextInt(4);
        
        switch (edge) {
            case 0: // Top edge
                x = random.nextInt(worldWidth);
                y = worldHeight;
                break;
            case 1: // Bottom edge
                x = random.nextInt(worldWidth);
                y = 0;
                break;
            case 2: // Left edge
                x = 0;
                y = random.nextInt(worldHeight);
                break;
            case 3: // Right edge
                x = worldWidth;
                y = random.nextInt(worldHeight);
                break;
            default:
                x = random.nextInt(worldWidth);
                y = random.nextInt(worldHeight);
        }
        
        Yapper enemy = new Yapper(
            "Enemy_" + spawnedEnemies.size(),
            100, // health
            10,  // damage
            5,   // attackSpeed
            200, // movementSpeed
            300.0f // detectionRadius
        );
        
        enemy.position.setX(x);
        enemy.position.setY(y);
        
        spawnedEnemies.add(enemy);
        CollisionManager.addEntity(enemy);
    }

    @Override
    public void hide() {
        GameApp.disposeTexture("player");
        GameApp.disposeTexture("crocodile");
        GameApp.disposeTexture("yapper2");
        GameApp.disposeTexture("box");
    }
}
