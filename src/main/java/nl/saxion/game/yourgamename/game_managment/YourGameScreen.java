package nl.saxion.game.yourgamename.game_managment;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import nl.saxion.game.yourgamename.collision.*;
import nl.saxion.game.yourgamename.movement.*;
import nl.saxion.game.yourgamename.screens.BaseGameScreen;
import nl.saxion.game.yourgamename.systems.*;
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
    private NPCSystem npcSystem;
    private EventInteractionSystem eventSystem;

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
        GameApp.addAnimationFromSpritesheet("idleAnim", "idle", 0.15f, true);
        GameApp.addFont("hud", "fonts/basic.ttf", 20, true);
        GameApp.addFont("default", "fonts/basic.ttf", 18);
        GameApp.addFont("pixel2", "fonts/Jersey10-Regular.ttf", 100);

        player = new Player("player", 125);
        enemySpawner = new EnemySpawner(worldWidth, worldHeight);
        combatSystem = new CombatSystem();
        npcSystem = new NPCSystem(player);
        eventSystem = new EventInteractionSystem(player);

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
        GameApp.setCustomCursor("textures/cursor (1).png", 23, 17);
    }

    @Override
    public void render(float delta) {
        // Update quiz system
        StudyQuizSystem quizSystem = eventSystem.getQuizSystem();
        if (quizSystem.isActive()) {
            System.out.println("Updating quiz - Phase: " + quizSystem.getCurrentPhase() + ", Delta: " + delta);
        }
        quizSystem.update(delta);
        
        // Only allow player movement if quiz is not active
        if (!quizSystem.isActive()) {
            // Update player movement
            PlayerMovement.checkMovementKeyPressed(player, delta);
            WorldBorder.clampToWorldBounds(player, worldWidth, worldHeight);
        }

        // Check if quiz is active - if so, skip normal game rendering
        StudyQuizSystem quizSystemCheck = eventSystem.getQuizSystem();
        if (!quizSystemCheck.isActive()) {
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
            System.out.println("E key pressed!");
            // Check for NPC interaction first (within 50 pixel range)
            if (!npcSystem.interactWithNearbyNPC(50f)) {
                System.out.println("No NPC nearby, checking for events...");
                // Check for event interaction (like UniEntrance) - increased range to 100
                boolean eventFound = eventSystem.interactWithNearbyEvent(100f);
                if (!eventFound) {
                    System.out.println("No event found nearby");
                }
            } else {
                System.out.println("NPC interaction handled");
            }
        }

        combatSystem.updatePlayerAttack(player, delta);
        combatSystem.updateYapperAttack(enemySpawner.getEnemies(), player, delta);

        // Handle collisions
        CollisionManager.checkCollision(getViewportLeft(),getViewportRight(),getViewportTop(),getViewportBottom());

        // Render quiz overlay if active (on top of everything)
        // Get fresh reference to quiz system
        StudyQuizSystem quizSystemForRender = eventSystem.getQuizSystem();
        boolean quizActive = quizSystemForRender.isActive();
        System.out.println("Checking quiz for render - isActive: " + quizActive + ", Phase: " + quizSystemForRender.getCurrentPhase());
        
        if (quizActive) {
            System.out.println("Quiz is active! Phase: " + quizSystemForRender.getCurrentPhase() + ", Screen: " + getScreenWidth() + "x" + getScreenHeight());
            // Clear screen to light beige/off-white for quiz (matching template)
            // Try white if beige doesn't work
            GameApp.clearScreen("white");
            System.out.println("Screen cleared to white");
            // Render quiz UI
            quizSystemForRender.render(getScreenWidth(), getScreenHeight());
            System.out.println("Quiz render completed");
        } else {
            System.out.println("Quiz is NOT active, rendering normal HUD");
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
        GameApp.updateAnimation("idleAnim");
        // Scale animation to match player size (16x24) - original spritesheet frames are 128x256
        GameApp.drawAnimation("idleAnim", player.position.getX(), player.position.getY(), player.getWidth(), player.getHeight());

        for (Yapper enemy : enemySpawner.getEnemies()) {
           GameApp.drawTexture("enemy", enemy.position.getX(), enemy.position.getY(), enemy.getWidth(), enemy.getHeight());
         }
        
        GameApp.endSpriteRendering();
    }

    private void renderInteractionPrompts() {
        // Render interaction prompts in a separate sprite batch so they appear on top of all map layers
        GameApp.startSpriteRendering();
        // Render NPC interaction prompt
        npcSystem.renderInteractionPrompt(50f);
        // Render event interaction prompt (like UniEntrance)
        eventSystem.renderInteractionPrompt(50f);
        GameApp.endSpriteRendering();
    }

    @Override
    public void hide() {
        world.dispose();
        GameApp.disposeSpritesheet("idle");
        GameApp.disposeAnimation("idleAnim");
        //GameApp.disposeTexture("enemy");
    }
}
