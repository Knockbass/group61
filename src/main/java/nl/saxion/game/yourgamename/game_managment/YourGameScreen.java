package nl.saxion.game.yourgamename.game_managment;

import com.badlogic.gdx.Input;
import nl.saxion.game.yourgamename.collision.*;
import nl.saxion.game.yourgamename.movement.*;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.CameraControlledGameScreen;
import nl.saxion.game.yourgamename.entities.*;

public class YourGameScreen extends CameraControlledGameScreen  {
    public static int worldWidth = 3240;        //world size
    public static int worldHeight = 2060;
    public static int viewportWidth = 1280;     //visible screen size
    public static int viewportHeight = 720;

    public static Player player;
    private Box box;
    private EnemySpawner enemySpawner;
    private CombatSystem combatSystem;

    public YourGameScreen() {
        super(viewportWidth, viewportHeight,worldWidth, worldHeight);
    }

    @Override
    public void show() {
        // Enable the built-in HUD
        enableHUD(viewportWidth, viewportHeight);
        
        GameApp.addTexture("player", "textures/bear.png");
        GameApp.addTexture("enemy", "textures/crocodile.png");
        GameApp.addTexture("box", "textures/rhino.png");

        player = new Player("test",    300);
        box = new Box(200, 100);
        enemySpawner = new EnemySpawner(getWorldWidth(),getWorldHeight());
        combatSystem = new CombatSystem();

        CollisionManager.addEntity(player);
        CollisionManager.addEntity(box);

        setCameraTargetInstantly(player.getX(),player.getY());
    }

    @Override
    public void render(float delta) {

        // Update player movement
        PlayerMovement.checkMovementKeyPressed(player, delta);
        WorldBorder.clampToWorldBounds(player, getWorldWidth(), getWorldHeight());

        //update deployed screen
        setCameraTarget(player.getX(),player.getY());

        super.render(delta);
        GameApp.clearScreen("teal-300");

        // Update enemy spawning
        enemySpawner.update(delta, player, getViewportLeft(),getViewportRight(),getViewportTop(),getViewportBottom());

        // Update enemy movement
        EnemyMovement.updateEnemies(enemySpawner.getEnemies(), player, delta, worldWidth, worldHeight);

        // Render attack
        if(GameApp.isButtonJustPressed(Input.Buttons.LEFT) && !player.attacking){
            combatSystem.startAttack(player);
        }

        if(player.attacking && !player.hasHitEnemy){
            combatSystem.applyPlayerAttack(player, enemySpawner.getEnemies());
            enemySpawner.deleteDeadEnemies();
        }

        if(GameApp.isKeyJustPressed(Input.Keys.E)){
            player.accessStatSystem().sleep();
            player.accessStatSystem().study();
            player.accessStatSystem().dringBeer();
        }

        combatSystem.updatePlayerAttack(player, delta);
        combatSystem.updateYapperAttack(enemySpawner.getEnemies(), player, delta);

        // Handle collisions
        CollisionManager.checkCollision(getViewportLeft(),getViewportRight(),getViewportTop(),getViewportBottom());

        // Render all entities
        renderEntities();
        
        // Switch to HUD rendering and display stats
        switchToHudRendering();
        DisplayStats.render(player, getHUDWidth(), getHUDHeight());
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
