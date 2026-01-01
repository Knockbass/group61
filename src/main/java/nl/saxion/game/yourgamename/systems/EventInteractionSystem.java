package nl.saxion.game.yourgamename.systems;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import nl.saxion.game.yourgamename.collision.CollisionManager;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.game_managment.EnemySpawner;
import nl.saxion.game.yourgamename.game_managment.WorldMap;
import nl.saxion.game.yourgamename.screens.BaseGameScreen;
import nl.saxion.game.yourgamename.screens.YourGameScreen;
import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;
import java.util.List;

public class EventInteractionSystem {
    private List<EventArea> eventAreas;
    private Player player;
    private StudyQuizSystem quizSystem;
    private WorldMap world;
    private NPCSystem npcSystem;
    private EnemySpawner enemySpawner;

    public EventInteractionSystem(Player player, WorldMap world, NPCSystem npcSystem, EnemySpawner enemySpawner) {
        this.eventAreas = new ArrayList<>();
        this.player = player;
        this.quizSystem = new StudyQuizSystem(player);
        this.world = world;
        this.npcSystem = npcSystem;
        this.enemySpawner = enemySpawner;
    }

    public void loadEventsFromMap(MapObjects mapObjects) {
        System.out.println("Loading events from map. Total objects: " + mapObjects.getCount());
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof RectangleMapObject rectObject) {
                String name = mapObject.getName();
                if (name != null && !name.startsWith("NPC")) { // Skip NPCs, they're handled separately
                    Rectangle rect = rectObject.getRectangle();
                    EventArea event = new EventArea(name, rect.x, rect.y, rect.width, rect.height);
                    eventAreas.add(event);
                    System.out.println("Loaded event: " + name + " at (" + rect.x + ", " + rect.y + ")");
                }
            }
        }
        System.out.println("Total events loaded: " + eventAreas.size());
    }

    public EventArea getNearbyEvent(float interactionRange) {
        float playerCenterX = player.getX() + player.getWidth() / 2f;
        float playerCenterY = player.getY() + player.getHeight() / 2f;

        for (EventArea event : eventAreas) {
            Rectangle area = event.getArea();
            float eventCenterX = area.x + area.width / 2f;
            float eventCenterY = area.y + area.height / 2f;

            // Check if player is within interaction range
            float distance = (float) Math.sqrt(
                    Math.pow(playerCenterX - eventCenterX, 2) +
                            Math.pow(playerCenterY - eventCenterY, 2)
            );

            if (distance <= interactionRange) {
                return event;
            }
        }
        return null;
    }

    public boolean interactWithNearbyEvent(float interactionRange) {
        EventArea nearbyEvent = getNearbyEvent(interactionRange);
        if (nearbyEvent != null) {
            System.out.println("Found nearby event: " + nearbyEvent.getName());
            handleEventInteraction(nearbyEvent);
            return true;
        } else {
            System.out.println("No nearby event found within range: " + interactionRange);
            // Debug: print all events and distances
            float playerCenterX = player.getX() + player.getWidth() / 2f;
            float playerCenterY = player.getY() + player.getHeight() / 2f;
            System.out.println("Player position: (" + playerCenterX + ", " + playerCenterY + ")");
            System.out.println("Total events loaded: " + eventAreas.size());
            for (EventArea event : eventAreas) {
                Rectangle area = event.getArea();
                float eventCenterX = area.x + area.width / 2f;
                float eventCenterY = area.y + area.height / 2f;
                float distance = (float) Math.sqrt(
                        Math.pow(playerCenterX - eventCenterX, 2) +
                                Math.pow(playerCenterY - eventCenterY, 2)
                );
                System.out.println("Event '" + event.getName() + "' at (" + eventCenterX + ", " + eventCenterY + ") - Distance: " + distance);
            }
        }
        return false;
    }

    private void handleEventInteraction(EventArea event) {
        String eventName = event.getName();
        System.out.println("Event interaction triggered: " + eventName);

        if (eventName.equals("UniEntrance")) {
            switchScreens(960, 640, "maps/first floor map 1/first floor.tmx", 1);
            // Start quiz instead of direct study
  /*          if (!quizSystem.isActive()) {
                System.out.println("Starting quiz...");
                quizSystem.startQuiz();
                if (quizSystem.isActive()) {
                    System.out.println("Quiz started. Active: " + quizSystem.isActive());
                } else {
                    System.out.println("Quiz could not start - may have been completed today already");
                }
            } else {
                System.out.println("Quiz already active, cannot start new one");
            }
            if (quizSystem.canStartQuiz()) {
                        promptText = "Press E to study at University";
                        descriptionText = "Take Quiz (4 questions)";
                    } else {
                        promptText = "Press E to study at University";
                        descriptionText = "Quiz already completed today";
                    }
    */
        } else if (eventName.equals("exit")) {
            switchScreens(1920, 1440, "maps/map.tmx", 0);
        } else if (eventName.equals("buyable_area")) {
            int beerPrice = 5;

            if (player.accessStatSystem().getMoney() >= beerPrice) {
                player.accessStatSystem().setBeerCount(player.accessStatSystem().getBeerCount() + 1);
                player.accessStatSystem().setMoney(player.accessStatSystem().getMoney() - beerPrice);
            }
        } else if (eventName.equals("Sleep")) {
            // Sleep event - restore stats and advance to next day
            StatSystem stats = player.accessStatSystem();
            int currentDay = stats.getCurrentDay();
            stats.sleep();
            int newDay = stats.getCurrentDay();
            System.out.println("Player slept! Day advanced from " + currentDay + " to " + newDay);
            System.out.println("Mental Health and Energy restored!");
        } else {
            System.out.println("Event name '" + eventName + "' does not match known events");
        }
    }

    public StudyQuizSystem getQuizSystem() {
        return quizSystem;
    }

    public void renderInteractionPrompt(float interactionRange) {
        try {
            EventArea nearbyEvent = getNearbyEvent(interactionRange);
            if (nearbyEvent != null) {
                String eventName = nearbyEvent.getName();
                String promptText = "";
                String descriptionText = "";

                if (eventName.equals("UniEntrance")) {
                    promptText = "Press E to enter University";
                } else if (eventName.equals("Sleep")) {
                    promptText = "Press E to Sleep";
                    descriptionText = "Restore Energy & Mental Health (Next Day)";
                } else if (eventName.equals("buyable_area")) {
                    promptText = "Press E to buy beer";
                    descriptionText = "Buy beer for 5 coins";           //should be changed manually according to the beer price
                } else if (eventName.equals("exit")) {
                    promptText = "Press E to exit University";
                } else if (eventName.equals("Sleep")) {
                    promptText = "Press E to Sleep";
                    descriptionText = "Restore Energy & Mental Health (Next Day)";
                } else if (eventName.equals("MoveToSecondFloor")) {
                    promptText = "Press E to move to next floor";
                    descriptionText = "Buy beer for 5 coins";
                }

                if (!promptText.isEmpty()) {
                    // Position text above the player (in world coordinates)
                    float playerCenterX = player.getX() + player.getWidth() / 2f;
                    float textY = player.getY() + player.getHeight() + 35f;
                    float lineHeight = 22f;

                    // Display interaction prompt above player (horizontally centered, using hud font for same style)
                    GameApp.drawTextHorizontallyCentered("hud", promptText, playerCenterX, textY + lineHeight, "white");

                    if (!descriptionText.isEmpty()) {
                        GameApp.drawTextHorizontallyCentered("hud", descriptionText, playerCenterX, textY, "yellow-500");
                    }
                }
            }
        } catch (Exception e) {
            // Silently handle any rendering errors to prevent crashes
            System.err.println("Error rendering event interaction prompt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class EventArea {
        private String name;
        private Rectangle area;

        public EventArea(String name, float x, float y, float width, float height) {
            this.name = name;
            this.area = new Rectangle(x, y, width, height);
        }

        public String getName() {
            return name;
        }

        public Rectangle getArea() {
            return area;
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
        quizSystem.setPlayer(player);
    }

    public void switchScreens(int newWorldWidth, int newWorldHeight, String newMapPath, int mapType) {
        //change main variables of the world
        YourGameScreen.setWorldWidth(newWorldWidth);
        YourGameScreen.setWorldHeight(newWorldHeight);
        YourGameScreen.worldWidth = newWorldWidth;
        YourGameScreen.worldHeight = newWorldHeight;
        world.setMap(newMapPath);
        world.setMapType(mapType);

        //place player in the correct position on the screen
        switch (mapType) {
            case 0:
                player.setX(200);
                player.setY(YourGameScreen.worldHeight - player.entityHeight - 281);
                break;
            case 1:
                player.setX(466);
                player.setY(YourGameScreen.worldHeight - player.entityHeight - 576);
                break;
            case 2:
                player.setX(0);
                player.setY(0);
                break;
        }

        player.collisionBox.x = player.getX() + 2;
        player.collisionBox.y = player.getY();

        //change objects for collision
        CollisionManager.clear();
        CollisionManager.addEntity(player);
        MapObjects collisionObjects = world.getObjectLayer("Collisions").getObjects();
        CollisionManager.addMapObjects(collisionObjects);

        // Load NPCs from Events object layer
        MapObjects eventObjects = world.getObjectLayer("Events").getObjects();
        npcSystem.loadNPCsFromMap(eventObjects);
        // Load event areas (like UniEntrance) from Events object layer
        loadEventsFromMap(eventObjects);
        // Load YapperSpawn areas from Spawns object layer
        MapObjects spawnObjects = world.getObjectLayer("Spawns").getObjects();
        enemySpawner.loadSpawnAreasFromMap(spawnObjects);
    }
}
