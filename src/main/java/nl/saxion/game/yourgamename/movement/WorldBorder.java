package nl.saxion.game.yourgamename.movement;

import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.entities.Yapper;
import nl.saxion.gameapp.GameApp;

public class WorldBorder {
    public static void clampToWorldBounds(Player player, int worldWidth, int worldHeight){
        player.position.setX((int) GameApp.clamp(player.position.getX(), 0, worldWidth - player.getWidth()));
        player.position.setY((int) GameApp.clamp(player.position.getY(), 0, worldHeight - player.getHeight()));
    }

    public static void clampToWorldBounds(Yapper yapper, int worldWidth, int worldHeight){
        yapper.position.setX((int) GameApp.clamp(yapper.position.getX(), 0, worldWidth - yapper.getWidth()));
        yapper.position.setY((int) GameApp.clamp(yapper.position.getY(), 0, worldHeight - yapper.getHeight()));
    }

}
