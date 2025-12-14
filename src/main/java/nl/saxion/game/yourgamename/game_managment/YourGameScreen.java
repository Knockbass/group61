package nl.saxion.game.yourgamename.game_managment;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import nl.saxion.game.yourgamename.collision.*;
import nl.saxion.game.yourgamename.movement.*;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.CameraControlledGameScreen;
import nl.saxion.game.yourgamename.entities.*;
import nl.saxion.gameapp.screens.GameScreen;

public class YourGameScreen extends BaseGameScreen {
    public static int worldWidth = 1920;        //world size
    public static int worldHeight = 1440;
    public static float viewportWidth = 320;     //visible screen size
    public static float viewportHeight = 240;

    public static Player player;
    private WorldMap world;
    private EnemySpawner enemySpawner;
    private CombatSystem combatSystem;

    public YourGameScreen() {
        super(viewportWidth, viewportHeight, worldWidth, worldHeight);
    }

    @Override
    public void show() {
        world = new WorldMap("maps/map.tmx");
        //imports objects of a certain object layer of the map
        MapObjects collisionObjects = world.getObjectLayer("Collisions").getObjects();
        CollisionManager.addMapObjects(collisionObjects);

        GameApp.addTexture("player", "textures/player.png");

        player = new Player("player", 125);
        enemySpawner = new EnemySpawner(worldWidth, worldHeight);
        combatSystem = new CombatSystem();

        CollisionManager.addEntity(player);
        setCameraTargetInstantly(player.getX(), player.getY());
    }

    @Override
    public void render(float delta) {
        // Update player movement
        PlayerMovement.checkMovementKeyPressed(player, delta);
        WorldBorder.clampToWorldBounds(player, worldWidth, worldHeight);

        //update screen
        setCameraTarget(player.getX(), player.getY());

        super.render(delta);
        GameApp.clearScreen("teal-300");

        //update map layers under entities
        world.getRenderer().setView(getCamera());
        world.getRenderer().render(world.getLayersBelowPlayer());
        //render entities
        renderEntities();
        //render map layers above entities
        world.getRenderer().render(world.getLayersAbovePlayer());


        // Update enemy spawning
        enemySpawner.update(delta, player, getViewportLeft(), getViewportRight(), getViewportTop(), getViewportBottom());

        // Update enemy movement
        EnemyMovement.updateEnemies(enemySpawner.getEnemies(), player, delta, worldWidth, worldHeight);

        // Render attack
        if (GameApp.isButtonJustPressed(Input.Buttons.LEFT) && !player.attacking) {
            combatSystem.startAttack(player);
        }

        if (player.attacking && !player.hasHitEnemy) {
            combatSystem.applyPlayerAttack(player, enemySpawner.getEnemies());
            enemySpawner.deleteDeadEnemies();
        }

        if (GameApp.isKeyJustPressed(Input.Keys.E)) {
            player.accessStatSystem().sleep();
            player.accessStatSystem().study();
            player.accessStatSystem().dringBeer();
        }

        combatSystem.updatePlayerAttack(player, delta);
        combatSystem.updateYapperAttack(enemySpawner.getEnemies(), player, delta);

        // Handle collisions
        CollisionManager.checkCollision(getViewportLeft(),getViewportRight(),getViewportTop(),getViewportBottom());

        // DisplayStats.render(player, getHUDWidth(), getHUDHeight());
    }

    private void renderEntities() {
        GameApp.startSpriteRendering();
        GameApp.drawTexture("player",player.getX(), player.getY(), player.getWidth(), player.getHeight());

        //GameApp.drawTexture("player", player.position.getX(), player.position.getY(), player.getWidth(), player.getHeight());

        //for (Yapper enemy : enemySpawner.getEnemies()) {
        //    GameApp.drawTexture("enemy", enemy.position.getX(), enemy.position.getY(), enemy.getWidth(), enemy.getHeight());
        // }
        GameApp.endSpriteRendering();
    }

    @Override
    public void hide() {
        world.dispose();
        GameApp.disposeTexture("player");
        //GameApp.disposeTexture("enemy");
    }
}
