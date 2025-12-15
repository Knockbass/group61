package nl.saxion.game.yourgamename.systems;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import nl.saxion.game.yourgamename.entities.NPC;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.game_managment.Quest;
import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;
import java.util.List;

public class NPCSystem {
    private List<NPC> npcs;
    private Player player;
    private Quest activeQuest; // Track the currently active quest
    private Quest completedQuest; // Track the last completed quest to show rewards
    private float completedQuestDisplayTime = 0f; // Timer to auto-clear completed quest display
    private static final float COMPLETED_QUEST_DISPLAY_DURATION = 5f; // Show for 5 seconds

    public NPCSystem(Player player) {
        this.npcs = new ArrayList<>();
        this.player = player;
        this.activeQuest = null;
        this.completedQuest = null;
        this.completedQuestDisplayTime = 0f;
    }

    public void loadNPCsFromMap(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof RectangleMapObject rectObject) {
                String name = mapObject.getName();
                // Only load objects that start with "NPC"
                if (name != null && name.startsWith("NPC")) {
                    NPC npc = NPC.fromMapObject(rectObject);
                    // Set up default quest and reward for NPC1 as an example
                    if (name.equals("NPC1")) {
                        Quest.QuestObjective objective = new Quest.QuestObjective(
                            Quest.QuestObjective.ObjectiveType.KNOWLEDGE, 20
                        );
                        Quest.Reward reward = new Quest.Reward(10, 20, 15, 50, 25, 1);
                        Quest quest = new Quest("Get 20 Knowledge to complete this quest!", reward, objective);
                        npc.setQuest(quest);
                    }
                    npcs.add(npc);
                }
            }
        }
    }

    public NPC getNearbyNPC(float interactionRange) {
        float playerCenterX = player.getX() + player.getWidth() / 2f;
        float playerCenterY = player.getY() + player.getHeight() / 2f;

        for (NPC npc : npcs) {
            Rectangle area = npc.getInteractionArea();
            float npcCenterX = area.x + area.width / 2f;
            float npcCenterY = area.y + area.height / 2f;

            // Check if player is within interaction range
            float distance = (float) Math.sqrt(
                    Math.pow(playerCenterX - npcCenterX, 2) + 
                    Math.pow(playerCenterY - npcCenterY, 2)
            );

            if (distance <= interactionRange) {
                return npc;
            }
        }
        return null;
    }

    public boolean interactWithNearbyNPC(float interactionRange) {
        NPC nearbyNPC = getNearbyNPC(interactionRange);
        if (nearbyNPC != null && nearbyNPC.hasQuest()) {
            handleQuestInteraction(nearbyNPC);
            return true;
        }
        return false;
    }

    private void handleQuestInteraction(NPC npc) {
        Quest quest = npc.getQuest();
        if (quest == null) {
            System.out.println(npc.getName() + ": No quest available!");
            return;
        }

        if (quest.isNotStarted()) {
            // Accept the quest
            quest.acceptQuest(player);
            activeQuest = quest;
            System.out.println(npc.getName() + ": Quest accepted!");
            System.out.println("Quest: " + quest.getDescription());
            if (quest.getObjective() != null) {
                System.out.println("Objective: Get " + quest.getObjective().getTargetAmount() + 
                                  " " + quest.getObjective().getTypeName());
            }
        } else if (quest.isActive()) {
            // Check if quest can be completed
            boolean objectiveMet = quest.checkObjective(player);
            String progressText = quest.getProgressText();
            System.out.println("Quest Progress: " + progressText);
            
            if (objectiveMet) {
                // Complete the quest
                quest.applyRewards(player);
                completedQuest = quest; // Store completed quest to show rewards
                completedQuestDisplayTime = COMPLETED_QUEST_DISPLAY_DURATION; // Start timer
                activeQuest = null; // Clear active quest
                
                Quest.Reward reward = quest.getReward();
                if (reward != null) {
                    System.out.println("Quest completed! Rewards applied.");
                    System.out.println("Knowledge: +" + reward.getKnowledgeReward() + 
                                      ", Mental Health: +" + reward.getMentalHealthReward() +
                                      ", Energy: +" + reward.getEnergyReward() +
                                      ", Money: +" + reward.getMoneyReward() +
                                      ", HP: +" + reward.getHpReward() +
                                      ", Beer: +" + reward.getBeerReward());
                }
            } else {
                System.out.println("You need " + quest.getObjective().getTargetAmount() + 
                                  " " + quest.getObjective().getTypeName() + 
                                  ". Progress: " + progressText);
            }
        } else if (quest.isCompleted()) {
            System.out.println(npc.getName() + ": Quest already completed!");
        }
    }

    public Quest getActiveQuest() {
        return activeQuest;
    }

    public Quest getCompletedQuest() {
        return completedQuest;
    }

    public void clearCompletedQuest() {
        completedQuest = null;
        completedQuestDisplayTime = 0f;
    }

    public void update(float delta) {
        // Auto-clear completed quest display after timer expires
        if (completedQuest != null && completedQuestDisplayTime > 0) {
            completedQuestDisplayTime -= delta;
            if (completedQuestDisplayTime <= 0) {
                clearCompletedQuest();
            }
        }
    }

    public NPC getNPCForQuest(Quest quest) {
        if (quest == null) return null;
        for (NPC npc : npcs) {
            if (npc.hasQuest() && npc.getQuest() == quest) {
                return npc;
            }
        }
        return null;
    }

    public List<NPC> getNPCs() {
        return npcs;
    }

    public void renderInteractionPrompt(float interactionRange) {
        try {
            NPC nearbyNPC = getNearbyNPC(interactionRange);
            if (nearbyNPC != null && nearbyNPC.hasQuest()) {
                Quest quest = nearbyNPC.getQuest();
                if (quest == null) return;
                
                // Position text above the player (in world coordinates)
                float playerCenterX = player.getX() + player.getWidth() / 2f;
                float textY = player.getY() + player.getHeight() + 35f;
                float lineHeight = 22f;
                
                // Note: startSpriteRendering/endSpriteRendering should be called by the caller
                // to avoid conflicts with other rendering code
                
                // Display interaction prompt above player (horizontally centered, using hud font for same style)
                String promptText = "Press E to interact with NPC";
                GameApp.drawTextHorizontallyCentered("hud", promptText, playerCenterX, textY + (lineHeight * 2), "white");
                
                if (quest.isNotStarted()) {
                    // Show quest description if not started
                    String questText = quest.getDescription();
                    if (questText != null && !questText.isEmpty()) {
                        // Wrap long text if needed
                        if (questText.length() > 30) {
                            int midPoint = questText.length() / 2;
                            int spaceIndex = questText.lastIndexOf(' ', midPoint);
                            if (spaceIndex > 0) midPoint = spaceIndex;
                            String part1 = questText.substring(0, midPoint);
                            String part2 = questText.substring(midPoint).trim();
                            GameApp.drawTextHorizontallyCentered("hud", part1, playerCenterX, textY + lineHeight, "yellow-500");
                            GameApp.drawTextHorizontallyCentered("hud", part2, playerCenterX, textY, "yellow-500");
                        } else {
                            GameApp.drawTextHorizontallyCentered("hud", questText, playerCenterX, textY + lineHeight, "yellow-500");
                            GameApp.drawTextHorizontallyCentered("hud", "Press E to accept quest", playerCenterX, textY, "cyan-400");
                        }
                    }
                } else if (quest.isActive()) {
                    // Update and show progress if quest is active
                    quest.checkObjective(player);
                    String progressText = "Progress: " + quest.getProgressText();
                    GameApp.drawTextHorizontallyCentered("hud", progressText, playerCenterX, textY + lineHeight, "cyan-400");
                    if (quest.checkObjective(player)) {
                        GameApp.drawTextHorizontallyCentered("hud", "Press E to complete quest", playerCenterX, textY, "green-400");
                    } else {
                        String objectiveText = "Need " + quest.getObjective().getTargetAmount() + " " + quest.getObjective().getTypeName();
                        GameApp.drawTextHorizontallyCentered("hud", objectiveText, playerCenterX, textY, "white");
                    }
                } else if (quest.isCompleted()) {
                    GameApp.drawTextHorizontallyCentered("hud", "Quest completed!", playerCenterX, textY + lineHeight, "green-400");
                }
            }
        } catch (Exception e) {
            // Silently handle any rendering errors to prevent crashes
            System.err.println("Error rendering NPC interaction prompt: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

