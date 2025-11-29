package nl.saxion.game.yourgamename.movement;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.entities.Yapper;
import nl.saxion.gameapp.GameApp;

public class WorldBorder {
    public static void clampToWorldBounds(Collidable entity, int worldWidth, int worldHeight){
        entity.setX((int) GameApp.clamp(entity.getX(), 0, worldWidth - entity.getWidth()));
        entity.setY((int) GameApp.clamp(entity.getY(), 0, worldHeight - entity.getHeight()));
    }
}
