package nl.saxion.game.yourgamename.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Matrix4;
import nl.saxion.game.yourgamename.collision.*;
import nl.saxion.game.yourgamename.game_managment.*;
import nl.saxion.game.yourgamename.movement.*;
import nl.saxion.game.yourgamename.systems.*;
import nl.saxion.gameapp.GameApp;
import nl.saxion.game.yourgamename.entities.*;

public class YourGameScreen extends BaseGameScreen {
    public static int worldWidth = 1920;        //world size
    public static int worldHeight = 1440;
    public static float viewportWidth = 320;     //visible screen size
    public static float viewportHeight = 240;

    public static Player player;
    private WorldMap world;
    private EnemySpawner enemySpawner;
    private CombatSystem combatSystem;
    private NPCSystem npcSystem;
    private EventInteractionSystem eventSystem;
    private DataStorage dataStorage;
    private SaveLoadSystem saveLoadSystem;
    private OrthographicCamera hudCamera;

    public YourGameScreen() {
        super(viewportWidth, viewportHeight, worldWidth, worldHeight);
    }

    @Override
    public void show() {
        world = new WorldMap("maps/map.tmx");
        //imports objects of a certain object layer of the map
        MapObjects collisionObjects = world.getObjectLayer("Collisions").getObjects();
        CollisionManager.addMapObjects(collisionObjects);

        // Add enemy texture (using bear as placeholder - replace with enemy.png when available)
        GameApp.addTexture("enemy", "textures/bear.png");
        GameApp.addSpriteSheet("idle", "textures/idleanimation3.png", 128, 256);
        GameApp.addSpriteSheet("walkingRight", "textures/walkinganimright.png", 128, 256);
        GameApp.addSpriteSheet("walkingLeft", "textures/walkinganimleft.png", 128, 256);
        GameApp.addSpriteSheet("walkingUp", "textures/walkinganimup.png", 128, 256);
        GameApp.addSpriteSheet("walkingDown", "textures/walkinganimdown.png", 128, 256);
        GameApp.addAnimationFromSpritesheet("idleAnim", "idle", 0.15f, true);
        GameApp.addAnimationFromSpritesheet("walkingAnimDown", "walkingDown", 0.25f, true);
        GameApp.addAnimationFromSpritesheet("walkingAnimUp", "walkingUp", 0.15f, true);
        GameApp.addAnimationFromSpritesheet("walkingAnimRight", "walkingRight", 0.15f, true);
        GameApp.addAnimationFromSpritesheet("walkingAnimLeft", "walkingLeft", 0.15f, true);
        GameApp.addTexture("quizBackground", "textures/quizbg.png");
        GameApp.addFont("hud", "fonts/basic.ttf", 20, true);
        GameApp.addFont("default", "fonts/basic.ttf", 18);

        player = new Player("player", 125);
        enemySpawner = new EnemySpawner(worldWidth, worldHeight);
        combatSystem = new CombatSystem();
        npcSystem = new NPCSystem(player);
        eventSystem = new EventInteractionSystem(player, world, npcSystem, enemySpawner);
        saveLoadSystem = new SaveLoadSystem(dataStorage);

        // Load NPCs from Events object layer
        MapObjects eventObjects = world.getObjectLayer("Events").getObjects();
        npcSystem.loadNPCsFromMap(eventObjects);
        // Load event areas (like UniEntrance) from Events object layer
        eventSystem.loadEventsFromMap(eventObjects);
        // Load YapperSpawn areas from Spawns object layer
        MapObjects spawnObjects = world.getObjectLayer("Spawns").getObjects();
        enemySpawner.loadSpawnAreasFromMap(spawnObjects);

        CollisionManager.addEntity(player);
        setCameraTargetInstantly(player.getX(), player.getY());
    }

    @Override
    public void render(float delta) {
        // Save/load
        if (GameApp.isKeyPressed(Input.Keys.CONTROL_LEFT) && GameApp.isKeyJustPressed(Input.Keys.O)){
            dataStorage = new DataStorage(player, npcSystem);
            saveLoadSystem = new SaveLoadSystem(dataStorage);
            saveLoadSystem.saveGame();
            System.out.println("Saved");
        } else if(GameApp.isKeyPressed(Input.Keys.CONTROL_LEFT) && GameApp.isKeyJustPressed(Input.Keys.P)){
            enemySpawner.removeAllEnemies();
            dataStorage = saveLoadSystem.loadGame();
            player = dataStorage.player;
            npcSystem = dataStorage.npcSystem;
            npcSystem.setPlayer(player);
            eventSystem.setPlayer(player);

            CollisionManager.clear();
            CollisionManager.addMapObjects(world.getObjectLayer("Collisions").getObjects());
            CollisionManager.addEntity(player);

            System.out.println("Loaded");
        }
        // Update quiz system
        StudyQuizSystem quizSystem = eventSystem.getQuizSystem();
        quizSystem.update(delta);
        
        // Only allow player movement if quiz is not active
        if (!quizSystem.isActive()) {
            // Update player movement
            PlayerMovement.checkMovementKeyPressed(player, delta);
            WorldBorder.clampToWorldBounds(player, worldWidth, worldHeight);
        }

        // Check if quiz is active - if so, skip normal game rendering
        if (!quizSystem.isActive()) {
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
            //render interaction prompts on top of everything (trees, blocks, etc.)
            renderInteractionPrompts();
        }

        // Update NPC system (for clearing completed quest display)
        npcSystem.update(delta);

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

        // Only allow E key interaction if quiz is not active
        if (!quizSystem.isActive() && GameApp.isKeyJustPressed(Input.Keys.E)) {
            // Check for NPC interaction first (within 50 pixel range)
            if (!npcSystem.interactWithNearbyNPC(50f)) {
                // Check for event interaction (like UniEntrance) - increased range to 100
                eventSystem.interactWithNearbyEvent(100f);
                player.accessStatSystem().dringBeer();
            }
        }

        combatSystem.updatePlayerAttack(player, delta);
        combatSystem.updateYapperAttack(enemySpawner.getEnemies(), player, delta);

        // Handle collisions
        CollisionManager.checkCollision(getViewportLeft(),getViewportRight(),getViewportTop(),getViewportBottom());

        // Render quiz overlay if active (on top of everything)
        if (quizSystem.isActive()) {
            // Clear screen and draw quiz background
            GameApp.clearScreen("white");
            // Draw quiz background image covering full screen
            float screenWidth = getScreenWidth();
            float screenHeight = getScreenHeight();
            GameApp.startSpriteRendering();
            GameApp.drawTexture("quizBackground", player.position.getX() - viewportWidth/2, player.position.getY() - viewportHeight/2, viewportWidth, viewportHeight);
            GameApp.endSpriteRendering();
            // Render quiz UI on top of background
            quizSystem.render(screenWidth, screenHeight);
        } else {
            // Render HUD (stats display) - render after everything else so it's on top
            // Use screen dimensions for pixel-perfect rendering
            DisplayStats.render(player, getScreenWidth(), getScreenHeight(), getCamera());
            
            // Render quest progress on right side if quest is active or completed
            Quest activeQuest = npcSystem.getActiveQuest();
            Quest completedQuest = npcSystem.getCompletedQuest();
            if (activeQuest != null || completedQuest != null) {
                DisplayStats.renderQuestProgress(activeQuest, completedQuest, player, getScreenWidth(), getScreenHeight(), getCamera());
            }
        }
    }

    private void renderEntities() {
        GameApp.startSpriteRendering();
        if (GameApp.isKeyPressed(Input.Keys.RIGHT) || GameApp.isKeyPressed(Input.Keys.D)) {
            GameApp.updateAnimation("walkingAnimRight");
            GameApp.drawAnimation("walkingAnimRight", player.position.getX(), player.position.getY(), player.getWidth(), player.getHeight());
        } else if (GameApp.isKeyPressed(Input.Keys.UP) || GameApp.isKeyPressed(Input.Keys.W)) {
            GameApp.updateAnimation("walkingAnimUp");
            GameApp.drawAnimation("walkingAnimUp", player.position.getX(), player.position.getY(), player.getWidth(), player.getHeight());
        } else if (GameApp.isKeyPressed(Input.Keys.LEFT) || GameApp.isKeyPressed(Input.Keys.A)) {
            GameApp.updateAnimation("walkingAnimLeft");
            GameApp.drawAnimation("walkingAnimLeft", player.position.getX(), player.position.getY(), player.getWidth(), player.getHeight());
        } else if (GameApp.isKeyPressed(Input.Keys.DOWN) || GameApp.isKeyPressed(Input.Keys.S)) {
            GameApp.updateAnimation("walkingAnimDown");
            GameApp.drawAnimation("walkingAnimDown", player.position.getX(), player.position.getY(), player.getWidth(), player.getHeight());
        } else {
            GameApp.resetAnimation("walkingAnimRight");
            GameApp.resetAnimation("walkingAnimLeft");
            GameApp.resetAnimation("walkingAnimUp");
            GameApp.resetAnimation("walkingAnimDown");
            GameApp.updateAnimation("idleAnim");
            // Scale animation to match player size (16x24) - original spritesheet frames are 128x256
            GameApp.drawAnimation("idleAnim", player.position.getX(), player.position.getY(), player.getWidth(), player.getHeight());
        }
        for (Yapper enemy : enemySpawner.getEnemies()) {
           GameApp.drawTexture("enemy", enemy.position.getX(), enemy.position.getY(), enemy.getWidth(), enemy.getHeight());
         }
        
        GameApp.endSpriteRendering();
    }

    private void renderInteractionPrompts() {
        // Render interaction prompts in screen-space (pixel perfect) so text stays crisp and can be clamped on-screen.
        float screenWidth = getScreenWidth();
        float screenHeight = getScreenHeight();

        if (hudCamera == null) {
            hudCamera = new OrthographicCamera();
        }
        hudCamera.setToOrtho(false, screenWidth, screenHeight);
        hudCamera.update();

        Matrix4 oldProjection = GameApp.getSpriteBatch().getProjectionMatrix().cpy();
        GameApp.getSpriteBatch().setProjectionMatrix(hudCamera.combined);

        GameApp.startSpriteRendering();
        npcSystem.renderInteractionPrompt(50f, getCamera(), screenWidth, screenHeight);
        eventSystem.renderInteractionPrompt(50f, getCamera(), screenWidth, screenHeight);
        GameApp.endSpriteRendering();

        GameApp.getSpriteBatch().setProjectionMatrix(oldProjection);
    }


    @Override
    public void hide() {
        world.dispose();
        GameApp.disposeSpritesheet("idle");
        GameApp.disposeSpritesheet("walkingRight");
        GameApp.disposeSpritesheet("walkingLeft");
        GameApp.disposeSpritesheet("walkingUp");
        GameApp.disposeSpritesheet("walkingDown");
        GameApp.disposeAnimation("walkingAnimRight");
        GameApp.disposeAnimation("walkingAnimLeft");
        GameApp.disposeAnimation("walkingAnimUp");
        GameApp.disposeAnimation("walkingAnimDown");
        GameApp.disposeAnimation("idleAnim");
        GameApp.disposeTexture("quizBackground");
        GameApp.disposeTexture("enemy");
    }
}
