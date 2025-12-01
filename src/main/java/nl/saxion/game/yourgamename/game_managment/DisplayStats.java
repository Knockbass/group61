package nl.saxion.game.yourgamename.game_managment;

import nl.saxion.gameapp.GameApp;
import nl.saxion.game.yourgamename.entities.Player;

public class DisplayStats {    //this class is used to display the stats of the player
    
    public static void render(Player player, float hudWidth, float hudHeight) {
        if (player == null) return;
        
        StatSystem stats = player.accessStatSystem();
        
        // Starting position for stats (top-left corner)
        float startX = 20;
        float startY = hudHeight - 20;
        float lineHeight = 30;
        
        GameApp.startSpriteRendering();
        
        // Display all stats
        float y = startY;
        GameApp.drawText("default", "HP: " + stats.getHP() + "/" + stats.getMaxHP(), startX, y, "red-500");
        y -= lineHeight;
        
        GameApp.drawText("default", "Mental Health: " + stats.getMentalHealth() + "/" + stats.getMaxMentalHealth(), startX, y, "blue-500");
        y -= lineHeight;
        
        GameApp.drawText("default", "Energy: " + stats.getEnergy() + "/" + stats.getMaxEnergy(), startX, y, "yellow-500");
        y -= lineHeight;
        
        GameApp.drawText("default", "Knowledge: " + stats.getKnowldge(), startX, y, "green-500");
        y -= lineHeight;
        
        GameApp.drawText("default", "Money: " + stats.getMoney(), startX, y, "orange-500");
        
        GameApp.endSpriteRendering();
    }
}
