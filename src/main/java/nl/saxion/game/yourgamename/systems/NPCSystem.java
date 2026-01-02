package nl.saxion.game.yourgamename.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import nl.saxion.game.yourgamename.entities.NPC;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.quest_logic.Quest;
import nl.saxion.game.yourgamename.quest_logic.TutorialQuestChain;
import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;
import java.util.List;

public class NPCSystem {
    private List<NPC> npcs;
    private Player player;
    private Quest activeQuest;
    private Quest completedQuest;
    private float completedQuestDisplayTime = 0f;
    private static final float COMPLETED_QUEST_DISPLAY_DURATION = 5f;

    // Tutorial Quest Chain
    private TutorialQuestChain tutorialChain;
    private boolean tutorialMode = true; // Whether tutorial mode is active

    public NPCSystem() {
    }

    public NPCSystem(Player player) {
        this.npcs = new ArrayList<>();
        this.player = player;
        this.activeQuest = null;
        this.completedQuest = null;
        this.completedQuestDisplayTime = 0f;
        this.tutorialChain = new TutorialQuestChain(player);
    }

    public void loadNPCsFromMap(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof RectangleMapObject rectObject) {
                String name = mapObject.getName();
                if (name != null && name.startsWith("NPC")) {
                    NPC npc = NPC.fromMapObject(rectObject);

                    // Assign the tutorial quest to the first NPC
                    if (name.equals("NPC1") && tutorialMode) {
                        // Assign the current quest from the tutorial chain
                        Quest currentTutorialQuest = tutorialChain.getCurrentQuest();
                        if (currentTutorialQuest != null) {
                            npc.setQuest(currentTutorialQuest);
                            System.out.println("Tutorial Quest " + tutorialChain.getCurrentQuestNumber() +
                                    " assigned to NPC1: " + currentTutorialQuest.getDescription());
                        }
                    }
                    // Other NPCs can have their own quests (after the tutorial is finished)
                    else if (name.equals("NPC2") && !tutorialMode) {
                        Quest.QuestObjective objective = new Quest.QuestObjective(
                                Quest.QuestObjective.ObjectiveType.MONEY, 50
                        );
                        Quest.Reward reward = new Quest.Reward(20, 30, 25, 100, 35, 2);
                        Quest quest = new Quest("Earn 50 money to help me!", reward, objective);
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
            quest.acceptQuest(player);
            activeQuest = quest;
            System.out.println(npc.getName() + ": Quest accepted!");
            System.out.println("Quest: " + quest.getDescription());
            if (quest.getObjective() != null) {
                System.out.println("Objective: Get " + quest.getObjective().getTargetAmount() +
                        " " + quest.getObjective().getTypeName());
            }

            // If this is a tutorial quest, show progress
            if (tutorialMode && !tutorialChain.isChainComplete()) {
                System.out.println("Tutorial Progress: " + tutorialChain.getChainProgress());
            }
        } else if (quest.isActive()) {
            boolean objectiveMet;
            String progressText;


            objectiveMet = quest.checkObjective(player);
            progressText = quest.getProgressText();


            System.out.println("Quest Progress: " + progressText);

            if (objectiveMet) {
                quest.applyRewards(player);
                completedQuest = quest;
                completedQuestDisplayTime = COMPLETED_QUEST_DISPLAY_DURATION;
                activeQuest = null;

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

                // If this is a tutorial quest, move to the next one
                if (tutorialMode && !tutorialChain.isChainComplete()) {
                    tutorialChain.advanceToNextQuest();
                    System.out.println("Tutorial Progress: " + tutorialChain.getChainProgress());

                    // Assign the next tutorial quest to NPC1
                    Quest nextQuest = tutorialChain.getCurrentQuest();
                    if (nextQuest != null) {
                        npc.setQuest(nextQuest);
                        System.out.println("Next tutorial quest: " + nextQuest.getDescription());
                    } else {
                        // Tutorial is finished
                        tutorialMode = false;
                        System.out.println("🎉 TUTORIAL COMPLETE! You've mastered all game mechanics!");
                        System.out.println("You can now explore the world freely!");
                    }
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
        // Auto-clear completed quest display
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

    public void renderInteractionPrompt(float interactionRange, OrthographicCamera worldCamera, float screenWidth, float screenHeight) {
        try {
            NPC nearbyNPC = getNearbyNPC(interactionRange);
            if (nearbyNPC != null && nearbyNPC.hasQuest()) {
                Quest quest = nearbyNPC.getQuest();
                if (quest == null) return;

                float playerCenterX = player.getX() + player.getWidth() / 2f;
                float worldTextY = player.getY() + player.getHeight() + 35f;
                Vector3 screenPos = worldCamera.project(new Vector3(playerCenterX, worldTextY, 0));

                float marginX = 12f;
                float marginY = 18f;
                float x = MathUtils.clamp(screenPos.x, marginX, screenWidth - marginX);
                float baseY = MathUtils.clamp(screenPos.y, marginY, screenHeight - marginY);
                float lineHeight = 22f;

                String promptText = "Press E to interact with NPC";
                float y2 = MathUtils.clamp(baseY + (lineHeight * 2), marginY, screenHeight - marginY);
                GameApp.drawTextHorizontallyCentered("hud", promptText, (int) x, (int) y2, "white");

                // Tutorial mode indicator
                if (tutorialMode && !tutorialChain.isChainComplete()) {
                    float tutorialY = MathUtils.clamp(baseY + (lineHeight * 3), marginY, screenHeight - marginY);
                    String tutorialProgress = "[Tutorial " + tutorialChain.getCurrentQuestNumber() +
                            "/" + tutorialChain.getTotalQuestsCount() + "]";
                    GameApp.drawTextHorizontallyCentered("hud", tutorialProgress, (int) x, (int) tutorialY, "cyan-400");
                }

                if (quest.isNotStarted()) {
                    String questText = quest.getDescription();
                    if (questText != null && !questText.isEmpty()) {
                        if (questText.length() > 30) {
                            int midPoint = questText.length() / 2;
                            int spaceIndex = questText.lastIndexOf(' ', midPoint);
                            if (spaceIndex > 0) midPoint = spaceIndex;
                            String part1 = questText.substring(0, midPoint);
                            String part2 = questText.substring(midPoint).trim();
                            float y1 = MathUtils.clamp(baseY + lineHeight, marginY, screenHeight - marginY);
                            float y0 = MathUtils.clamp(baseY, marginY, screenHeight - marginY);
                            GameApp.drawTextHorizontallyCentered("hud", part1, (int) x, (int) y1, "yellow-500");
                            GameApp.drawTextHorizontallyCentered("hud", part2, (int) x, (int) y0, "yellow-500");
                        } else {
                            float y1 = MathUtils.clamp(baseY + lineHeight, marginY, screenHeight - marginY);
                            float y0 = MathUtils.clamp(baseY, marginY, screenHeight - marginY);
                            GameApp.drawTextHorizontallyCentered("hud", questText, (int) x, (int) y1, "yellow-500");
                            GameApp.drawTextHorizontallyCentered("hud", "Press E to accept quest", (int) x, (int) y0, "cyan-400");
                        }
                    }
                } else if (quest.isActive()) {
                    quest.checkObjective(player);
                    String progressText = "Progress: " + quest.getProgressText();


                    float y1 = MathUtils.clamp(baseY + lineHeight, marginY, screenHeight - marginY);
                    float y0 = MathUtils.clamp(baseY, marginY, screenHeight - marginY);
                    GameApp.drawTextHorizontallyCentered("hud", progressText, (int) x, (int) y1, "cyan-400");

                    boolean isComplete;

                    isComplete = quest.checkObjective(player);


                    if (isComplete) {
                        GameApp.drawTextHorizontallyCentered("hud", "Press E to complete quest", (int) x, (int) y0, "green-400");
                    } else {
                        String objectiveText = "Need " + quest.getObjective().getTargetAmount() + " " + quest.getObjective().getTypeName();
                        GameApp.drawTextHorizontallyCentered("hud", objectiveText, (int) x, (int) y0, "white");
                    }
                } else if (quest.isCompleted()) {
                    float y1 = MathUtils.clamp(baseY + lineHeight, marginY, screenHeight - marginY);
                    GameApp.drawTextHorizontallyCentered("hud", "Quest completed!", (int) x, (int) y1, "green-400");
                }
            }
        } catch (Exception e) {
            System.err.println("Error rendering NPC interaction prompt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public TutorialQuestChain accessTutorialQuestChain(){
        return this.tutorialChain;
    }
}