package nl.saxion.game.yourgamename.movement;

import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.entities.Yapper;

import java.util.List;

public class EnemyMovement {
    private static final int DETECTION_RADIUS = 300; // Radius in which enemy detects player
    private static final int MOVEMENT_SPEED = 150; // Pixels per second
    
    public static void updateEnemies(List<Yapper> enemies, Player player, float delta, int worldWidth, int worldHeight) {
        for (Yapper enemy : enemies) {
            if (isPlayerInRange(enemy, player)) {
                moveTowardsPlayer(enemy, player, delta);
            }
            
            // Keep enemy within world bounds
            WorldBorder.clampToWorldBounds(enemy, worldWidth, worldHeight);
        }
    }
    
    private static boolean isPlayerInRange(Yapper enemy, Player player) {
        int dx = player.getX() - enemy.getX();
        int dy = player.getY() - enemy.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= DETECTION_RADIUS;
    }
    
    private static void moveTowardsPlayer(Yapper enemy, Player player, float delta) {
        int dx = player.getX() - enemy.getX();
        int dy = player.getY() - enemy.getY();
        
        // Calculate distance
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Avoid division by zero
        if (distance > 0) {
            // Normalize direction vector
            double normalizedX = dx / distance;
            double normalizedY = dy / distance;
            
            // Calculate movement amount
            int moveAmount = Math.round(MOVEMENT_SPEED * delta);
            
            // Move enemy towards player
            enemy.position.setX(enemy.position.getX() + (int)(normalizedX * moveAmount));
            enemy.position.setY(enemy.position.getY() + (int)(normalizedY * moveAmount));
        }
    }
}

