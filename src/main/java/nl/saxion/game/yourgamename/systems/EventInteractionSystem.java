package nl.saxion.game.yourgamename.systems;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;
import java.util.List;

public class EventInteractionSystem {
    private List<EventArea> eventAreas;
    private Player player;

    public EventInteractionSystem(Player player) {
        this.eventAreas = new ArrayList<>();
        this.player = player;
    }

    public void loadEventsFromMap(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof RectangleMapObject rectObject) {
                String name = mapObject.getName();
                if (name != null) {
                    Rectangle rect = rectObject.getRectangle();
                    EventArea event = new EventArea(name, rect.x, rect.y, rect.width, rect.height);
                    eventAreas.add(event);
                }
            }
        }
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
            handleEventInteraction(nearbyEvent);
            return true;
        }
        return false;
    }

    private void handleEventInteraction(EventArea event) {
        String eventName = event.getName();
        
        if (eventName.equals("UniEntrance")) {
            // Study at university - gives knowledge and decreases energy
            player.accessStatSystem().studyAtUniversity();
            System.out.println("Studied at University! Knowledge +15, Energy -10");
        }
    }

    public void renderInteractionPrompt(float interactionRange) {
        try {
            EventArea nearbyEvent = getNearbyEvent(interactionRange);
            if (nearbyEvent != null) {
                String eventName = nearbyEvent.getName();
                String promptText = "";
                String descriptionText = "";
                
                if (eventName.equals("UniEntrance")) {
                    promptText = "Press E to study at University";
                    descriptionText = "Gain Knowledge, Lose Energy";
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
}
