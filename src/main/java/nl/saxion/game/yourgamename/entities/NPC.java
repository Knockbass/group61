package nl.saxion.game.yourgamename.entities;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import nl.saxion.game.yourgamename.game_managment.Quest;

public class NPC {
    private String name;
    private Rectangle interactionArea;
    private Quest quest;

    public NPC(String name, float x, float y, float width, float height) {
        this.name = name;
        this.interactionArea = new Rectangle(x, y, width, height);
        this.quest = null; // Quest will be set separately
    }

    public static NPC fromMapObject(RectangleMapObject mapObject) {
        Rectangle rect = mapObject.getRectangle();
        String name = mapObject.getName() != null ? mapObject.getName() : "Unknown";
        return new NPC(name, rect.x, rect.y, rect.width, rect.height);
    }

    public String getName() {
        return name;
    }

    public Rectangle getInteractionArea() {
        return interactionArea;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public boolean hasQuest() {
        return quest != null;
    }

    public boolean isQuestCompleted() {
        return quest != null && quest.isCompleted();
    }
}

