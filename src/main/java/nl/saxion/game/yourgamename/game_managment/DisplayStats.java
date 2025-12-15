package nl.saxion.game.yourgamename.game_managment;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import nl.saxion.game.yourgamename.systems.StatSystem;
import nl.saxion.gameapp.GameApp;
import nl.saxion.game.yourgamename.entities.Player;

public class DisplayStats {    //this class is used to display the stats of the player
    
    private static OrthographicCamera hudCamera;
    
    public static void render(Player player, float screenWidth, float screenHeight, OrthographicCamera worldCamera) {
        if (player == null) return;
        
        StatSystem stats = player.accessStatSystem();
        
        // Get actual screen dimensions in pixels for pixel-perfect rendering
        float actualScreenWidth = com.badlogic.gdx.Gdx.graphics.getWidth();
        float actualScreenHeight = com.badlogic.gdx.Gdx.graphics.getHeight();
        
        // Create a fixed HUD camera using actual screen pixels
        if (hudCamera == null) {
            hudCamera = new OrthographicCamera();
            hudCamera.setToOrtho(false, actualScreenWidth, actualScreenHeight);
        }
        
        // Update HUD camera to match actual screen size
        hudCamera.viewportWidth = actualScreenWidth;
        hudCamera.viewportHeight = actualScreenHeight;
        hudCamera.update();
        
        // Save the current projection matrix
        SpriteBatch batch = GameApp.getSpriteBatch();
        com.badlogic.gdx.math.Matrix4 oldProjection = batch.getProjectionMatrix().cpy();
        
        // Switch to HUD camera for crisp text rendering
        batch.setProjectionMatrix(hudCamera.combined);
        
        // Position in screen coordinates (top-left corner, using actual screen pixels)
        float startX = 10;
        float startY = actualScreenHeight - 40; // Moved lower (was 10, now 40 pixels from top)
        float lineHeight = 24; // Increased for larger font
        
        GameApp.startSpriteRendering();
        
        // Display all stats with better formatting
        float y = startY;
        
        // Day (at the top)
        GameApp.drawText("hud", "Day: " + stats.getCurrentDay(), startX, y, "purple-500");
        y -= lineHeight;
        
        // HP (Health Points)
        GameApp.drawText("hud", "HP: " + stats.getHP() + "/" + stats.getMaxHP(), startX, y, "red-500");
        y -= lineHeight;
        
        // Mental Health
        GameApp.drawText("hud", "Mental Health: " + stats.getMentalHealth() + "/" + stats.getMaxMentalHealth(), startX, y, "blue-500");
        y -= lineHeight;
        
        // Energy
        GameApp.drawText("hud", "Energy: " + stats.getEnergy() + "/" + stats.getMaxEnergy(), startX, y, "yellow-500");
        y -= lineHeight;
        
        // Knowledge
        GameApp.drawText("hud", "Knowledge: " + stats.getKnowldge() + "/" + stats.getMaxKnowledge(), startX, y, "green-500");
        y -= lineHeight;
        
        // Money
        GameApp.drawText("hud", "Money: " + stats.getMoney(), startX, y, "orange-500");
        y -= lineHeight;
        
        // Beer Count
        GameApp.drawText("hud", "Beer: " + stats.getBeerCount(), startX, y, "orange-400");
        
        GameApp.endSpriteRendering();
        
        // Restore the original projection matrix
        batch.setProjectionMatrix(oldProjection);
    }

    public static void renderQuestProgress(Quest activeQuest, Quest completedQuest, Player player, float screenWidth, float screenHeight, OrthographicCamera worldCamera) {
        Quest questToDisplay = null;
        boolean isCompleted = false;
        
        // Prioritize showing active quest, but also show completed quest with rewards
        if (activeQuest != null && activeQuest.isActive()) {
            questToDisplay = activeQuest;
            isCompleted = false;
        } else if (completedQuest != null && completedQuest.isCompleted()) {
            questToDisplay = completedQuest;
            isCompleted = true;
        } else {
            return; // No quest to display
        }
        
        Quest quest = questToDisplay;

        // Get actual screen dimensions in pixels for pixel-perfect rendering
        float actualScreenWidth = com.badlogic.gdx.Gdx.graphics.getWidth();
        float actualScreenHeight = com.badlogic.gdx.Gdx.graphics.getHeight();
        
        // Create a fixed HUD camera using actual screen pixels
        if (hudCamera == null) {
            hudCamera = new OrthographicCamera();
            hudCamera.setToOrtho(false, actualScreenWidth, actualScreenHeight);
        }
        
        // Update HUD camera to match actual screen size
        hudCamera.viewportWidth = actualScreenWidth;
        hudCamera.viewportHeight = actualScreenHeight;
        hudCamera.update();
        
        // Save the current projection matrix
        SpriteBatch batch = GameApp.getSpriteBatch();
        com.badlogic.gdx.math.Matrix4 oldProjection = batch.getProjectionMatrix().cpy();
        
        // Switch to HUD camera for crisp text rendering
        batch.setProjectionMatrix(hudCamera.combined);
        
        // Position on right side of screen (using actual screen pixels)
        float startX = actualScreenWidth - 200; // Right side with margin (increased for larger text)
        float startY = actualScreenHeight - 40; // Aligned with stats (same Y position)
        float lineHeight = 24; // Increased for larger font
        
        GameApp.startSpriteRendering();
        
        // Display quest info
        float y = startY;
        GameApp.drawText("hud", "Quest:", startX, y, "yellow-500");
        y -= lineHeight;
        
        // Quest description (wrap if needed - better word wrapping)
        String description = quest.getDescription();
        if (description.length() > 28) {
            // Split long descriptions at word boundaries
            int midPoint = description.length() / 2;
            int spaceIndex = description.lastIndexOf(' ', midPoint);
            if (spaceIndex > 0 && spaceIndex < description.length() - 5) {
                midPoint = spaceIndex;
            }
            String part1 = description.substring(0, midPoint).trim();
            String part2 = description.substring(midPoint).trim();
            GameApp.drawText("hud", part1, startX, y, "white");
            y -= lineHeight;
            GameApp.drawText("hud", part2, startX, y, "white");
        } else {
            GameApp.drawText("hud", description, startX, y, "white");
        }
        y -= lineHeight;
        
        // Show quest status and rewards
        if (isCompleted) {
            // Quest is completed - show rewards
            Quest.Reward reward = quest.getReward();
            if (reward != null) {
                GameApp.drawText("hud", "Quest Completed!", startX, y, "green-400");
                y -= lineHeight;
                GameApp.drawText("hud", "Rewards:", startX, y, "yellow-500");
                y -= lineHeight;
                
                // Display each reward that was given
                if (reward.getKnowledgeReward() != 0) {
                    GameApp.drawText("hud", "Knowledge: +" + reward.getKnowledgeReward(), startX, y, "green-500");
                    y -= lineHeight;
                }
                if (reward.getMentalHealthReward() != 0) {
                    GameApp.drawText("hud", "Mental Health: +" + reward.getMentalHealthReward(), startX, y, "blue-500");
                    y -= lineHeight;
                }
                if (reward.getEnergyReward() != 0) {
                    GameApp.drawText("hud", "Energy: +" + reward.getEnergyReward(), startX, y, "yellow-500");
                    y -= lineHeight;
                }
                if (reward.getMoneyReward() != 0) {
                    GameApp.drawText("hud", "Money: +" + reward.getMoneyReward(), startX, y, "orange-500");
                    y -= lineHeight;
                }
                if (reward.getHpReward() != 0) {
                    GameApp.drawText("hud", "HP: +" + reward.getHpReward(), startX, y, "red-500");
                    y -= lineHeight;
                }
                if (reward.getBeerReward() != 0) {
                    GameApp.drawText("hud", "Beer: +" + reward.getBeerReward(), startX, y, "orange-400");
                }
            }
        } else if (quest.getObjective() != null && player != null) {
            // Quest is active - show only progress
            boolean objectiveMet = quest.checkObjective(player);
            
            if (objectiveMet) {
                // Quest objective is complete - notify player to return to NPC (no rewards yet)
                GameApp.drawText("hud", "Quest Complete!", startX, y, "green-400");
                y -= lineHeight;
                GameApp.drawText("hud", "Return to NPC", startX, y, "yellow-500");
            } else {
                // Show progress only
                String progressText = "Progress: " + quest.getProgressText();
                GameApp.drawText("hud", progressText, startX, y, "cyan-400");
            }
        }
        
        GameApp.endSpriteRendering();
        
        // Restore the original projection matrix
        batch.setProjectionMatrix(oldProjection);
    }
}
