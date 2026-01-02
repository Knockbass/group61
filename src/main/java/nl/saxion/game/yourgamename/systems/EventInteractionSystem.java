package nl.saxion.game.yourgamename.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import nl.saxion.game.yourgamename.collision.CollisionManager;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.game_managment.EnemySpawner;
import nl.saxion.game.yourgamename.game_managment.WorldMap;
import nl.saxion.game.yourgamename.game_managment.WorldMap.MapType;
import nl.saxion.game.yourgamename.quest_logic.Quest;
import nl.saxion.game.yourgamename.quest_logic.StudyQuizSystem;
import nl.saxion.game.yourgamename.quest_logic.TutorialQuestChain;
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
    private TutorialQuestChain tutorialQuestChain;

    public EventInteractionSystem(Player player, WorldMap world, NPCSystem npcSystem, EnemySpawner enemySpawner) {
        this.eventAreas = new ArrayList<>();
        this.player = player;
        this.quizSystem = new StudyQuizSystem(player);
        this.world = world;
        this.npcSystem = npcSystem;
        this.enemySpawner = enemySpawner;
        this.tutorialQuestChain = npcSystem.accessTutorialQuestChain();
    }

    public void loadEventsFromMap(MapObjects mapObjects) {
        // Important: this system persists across map switches, so always clear first to avoid "old floor" events leaking.
        eventAreas.clear();
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
            // Enter university (first floor)
            switchScreens(960, 640, "maps/first floor map 1/first floor.tmx", MapType.UNI_FLOOR1, "SpawnPoint1");
            if (tutorialQuestChain.getCurrentQuest().getObjective().getType() == Quest.QuestObjective.ObjectiveType.ENTER_UNIVERSITY) { //quest task - enter university
                tutorialQuestChain.getCurrentQuest().setProgress(1);
            }
        } else if (eventName.equals("exit")) {
            // Exit university back to the open world, spawn outside the university
            switchScreens(1920, 1440, "maps/map.tmx", MapType.OPEN_WORLD, "uniout");
        } else if (eventName.equals("MoveToSecondFloor")) {
            // Go upstairs
            switchScreens(960, 640, "maps/second floor map/second_floor.tmx", MapType.UNI_FLOOR2, "SpawnPoint2");
        } else if (eventName.equals("MoveToFirstFloor")) {
            // Go downstairs (spawn near the staircase trigger on the first floor)
            switchScreens(960, 640, "maps/first floor map 1/first floor.tmx", MapType.UNI_FLOOR1, "SpawnPoint1");
        } else if (eventName.equals("BuyableArea")) {
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
        } else if (eventName.equals("Study")) {
            // Start study quiz
            if (quizSystem.canStartQuiz()) {
                System.out.println("Starting study quiz...");
                quizSystem.startQuiz();
            } else {
                System.out.println("Study quiz already completed today (or already active).");
            }
        } else {
            System.out.println("Event name '" + eventName + "' does not match known events");
        }
    }

    public StudyQuizSystem getQuizSystem() {
        return quizSystem;
    }

    public void renderInteractionPrompt(float interactionRange, OrthographicCamera worldCamera, float screenWidth, float screenHeight) {
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
                } else if (eventName.equals("BuyableArea")) {
                    promptText = "Press E to buy beer";
                    descriptionText = "Buy beer for 5 coins";           //should be changed manually according to the beer price
                } else if (eventName.equals("exit")) {
                    promptText = "Press E to exit University";
                } else if (eventName.equals("Sleep")) {
                    promptText = "Press E to Sleep";
                    descriptionText = "Restore Energy & Mental Health (Next Day)";
                } else if (eventName.equals("MoveToSecondFloor")) {
                    promptText = "Press E to move to next floor";
                } else if (eventName.equals("MoveToFirstFloor")) {
                    promptText = "Press E to move to previous floor";
                } else if (eventName.equals("Study")) {
                    promptText = "Press E to Study";
                    if (quizSystem.canStartQuiz()) {
                        descriptionText = "Take Quiz (4 questions)";
                    } else {
                        descriptionText = "Quiz already completed today";
                    }
                }

                if (!promptText.isEmpty()) {
                    // Position text above the player, but render in screen space and clamp to screen bounds.
                    float playerCenterX = player.getX() + player.getWidth() / 2f;
                    float worldTextY = player.getY() + player.getHeight() + 35f;
                    Vector3 screenPos = worldCamera.project(new Vector3(playerCenterX, worldTextY, 0));

                    float marginX = 12f;
                    float marginY = 18f;
                    float x = MathUtils.clamp(screenPos.x, marginX, screenWidth - marginX);
                    float baseY = MathUtils.clamp(screenPos.y, marginY, screenHeight - marginY);
                    float lineHeight = 22f;

                    // Display interaction prompt above player (horizontally centered, using hud font for same style)
                    float y1 = MathUtils.clamp(baseY + lineHeight, marginY, screenHeight - marginY);
                    float y0 = MathUtils.clamp(baseY, marginY, screenHeight - marginY);
                    GameApp.drawTextHorizontallyCentered("hud", promptText, (int) x, (int) y1, "white");

                    if (!descriptionText.isEmpty()) {
                        GameApp.drawTextHorizontallyCentered("hud", descriptionText, (int) x, (int) y0, "yellow-500");
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

    public void switchScreens(int newWorldWidth, int newWorldHeight, String newMapPath, MapType mapType) {
        switchScreens(newWorldWidth, newWorldHeight, newMapPath, mapType, null);
    }

    public void switchScreens(int newWorldWidth, int newWorldHeight, String newMapPath, MapType mapType, String spawnObjectName) {
        //change main variables of the world
        YourGameScreen.setWorldWidth(newWorldWidth);
        YourGameScreen.setWorldHeight(newWorldHeight);
        YourGameScreen.worldWidth = newWorldWidth;
        YourGameScreen.worldHeight = newWorldHeight;
        world.setMap(newMapPath);
        world.setMapType(mapType);

        // Place player based on a named rectangle object (preferred), falling back to hardcoded defaults.
        String fallbackSpawnName = switch (mapType) {
            case UNI_FLOOR1 -> "SpawnPoint1";
            case UNI_FLOOR2 -> "SpawnPoint2";
            default -> null;
        };

        // Prefer named spawn rectangles in the Spawns layer (SpawnPoint1/2, uniout, etc.)
        boolean placed = tryPlacePlayerAtObject("Spawns", spawnObjectName)
                || tryPlacePlayerAtObject("Events", spawnObjectName)
                || (spawnObjectName == null && fallbackSpawnName != null && tryPlacePlayerAtObject("Spawns", fallbackSpawnName));

        if (!placed) {
            //place player in the correct position on the screen
            switch (mapType) {
                case OPEN_WORLD:
                    player.setX(200);
                    player.setY(YourGameScreen.worldHeight - player.entityHeight - 281);
                    break;
                case UNI_FLOOR1:
                    player.setX(466);
                    player.setY(YourGameScreen.worldHeight - player.entityHeight - 576);
                    break;
                case UNI_FLOOR2:
                    player.setX(466);
                    player.setY(YourGameScreen.worldHeight - player.entityHeight - 576);
                    break;
            }
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

    private boolean tryPlacePlayerAtObject(String layerName, String objectName) {
        if (objectName == null) return false;
        MapLayer layer = world.getObjectLayer(layerName);
        if (layer == null) return false;

        MapObjects objects = layer.getObjects();
        for (MapObject obj : objects) {
            if (obj instanceof RectangleMapObject rectObj
                    && obj.getName() != null
                    && objectName.equalsIgnoreCase(obj.getName())) {
                Rectangle rect = rectObj.getRectangle();
                // libGDX's TmxMapLoader already provides object coordinates in the game's coordinate system.
                // So we should NOT "flip Y" here; doing so would place spawns at the wrong side of the map.
                int spawnX = (int) (rect.x + (rect.width / 2f) - (player.getWidth() / 2f));
                int spawnY = (int) (rect.y + (rect.height / 2f) - (player.getHeight() / 2f));
                player.setX(spawnX);
                player.setY(spawnY);
                return true;
            }
        }
        return false;
    }

    public void setNpcSystem(NPCSystem npcSystem) {
        this.npcSystem = npcSystem;
        this.tutorialQuestChain = this.npcSystem.accessTutorialQuestChain();
    }
}
