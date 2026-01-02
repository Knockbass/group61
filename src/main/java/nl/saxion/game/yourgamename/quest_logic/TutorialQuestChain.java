package nl.saxion.game.yourgamename.quest_logic;

import nl.saxion.game.yourgamename.entities.NPC;
import nl.saxion.game.yourgamename.entities.Player;

import java.util.ArrayList;
import java.util.List;

//Contains a chain of tutorial quests, that teach core game mechanics
public class TutorialQuestChain {

    private List<Quest> questChain;
    private int currentQuestIndex = 0;
    private Player player;

    public TutorialQuestChain() {
    }

    public TutorialQuestChain(Player player) {
        this.player = player;
        this.questChain = new ArrayList<>();
        initializeQuestChain();
    }

    private void initializeQuestChain() {
        // QUEST 1: find the university
        Quest quest1 = createMovementQuest();
        questChain.add(quest1);

        // QUEST 2: buy bear
        Quest quest2 = createBeerQuest();
        questChain.add(quest2);

        // QUEST 3: study in the university
        Quest quest3 = createStudyQuest();
        questChain.add(quest3);

        // QUEST 4: kill 2 yappers
        Quest quest4 = createCombatQuest();
        questChain.add(quest4);

        // QUEST 5: sleep and restore energy
        Quest quest5 = createSleepQuest();
        questChain.add(quest5);

        // QUEST 6: drink beer
        Quest quest6 = createBeerUsageQuest();
        questChain.add(quest6);

        // QUEST 7
        Quest quest7 = createFinalQuest();
        questChain.add(quest7);
    }

    private Quest createMovementQuest() {
        String description = "Find and enter the University! Move around using WASD or Arrow keys!";

        Quest.QuestObjective objective = new Quest.QuestObjective(
                Quest.QuestObjective.ObjectiveType.ENTER_UNIVERSITY,
                1
        );


        Quest.Reward reward = new Quest.Reward(10, 15, 10, 5, 0, 0);

        return new Quest(description, reward, objective);
    }

    private Quest createBeerQuest() {
        String description = "Visit the buyable area and purchase beer";

        Quest.QuestObjective objective = new Quest.QuestObjective(
                Quest.QuestObjective.ObjectiveType.BEER,
                1
        );


        Quest.Reward reward = new Quest.Reward(5, 10, 5, 10, 0, 0);

        return new Quest(description, reward, objective);
    }


    private Quest createStudyQuest() {
        String description = "Go to University and complete the study quiz to gain knowledge";

        Quest.QuestObjective objective = new Quest.QuestObjective(
                Quest.QuestObjective.ObjectiveType.KNOWLEDGE,
                5
        );


        Quest.Reward reward = new Quest.Reward(15, 20, 15, 15, 10, 1);

        return new Quest(description, reward, objective);
    }


    private Quest createCombatQuest() {
        String description = "Defeat 2 Yappers! Use LEFT MOUSE to attack enemies";

        Quest.QuestObjective objective = new Quest.QuestObjective(
                Quest.QuestObjective.ObjectiveType.YAPPERS_COUNT,
                2
        );


        Quest.Reward reward = new Quest.Reward(20, 10, 5, 25, 30, 1);

        return new Quest(description, reward, objective);
    }


    private Quest createSleepQuest() {
        String description = "Rest at your home! Find the Sleep area near your starting house";


        Quest.QuestObjective objective = new Quest.QuestObjective(
                Quest.QuestObjective.ObjectiveType.ENERGY,
                80
        );


        Quest.Reward reward = new Quest.Reward(10, 25, 20, 5, 20, 1);

        return new Quest(description, reward, objective);
    }

    private Quest createBeerUsageQuest() {
        String description = "Use beer to restore your stats! Press R to drink beer";

        Quest.QuestObjective objective = new Quest.QuestObjective(
                Quest.QuestObjective.ObjectiveType.MENTAL_HEALTH,
                90
        );


        Quest.Reward reward = new Quest.Reward(15, 15, 15, 10, 15, 2);

        return new Quest(description, reward, objective);
    }


    private Quest createFinalQuest() {
        String description = "Master the semester! Pass the exam!";


        Quest.QuestObjective objective = new Quest.QuestObjective(
                Quest.QuestObjective.ObjectiveType.FINISH_EXAM,
                1
        );


        Quest.Reward reward = new Quest.Reward(30, 30, 25, 100, 40, 3);

        return new Quest(description, reward, objective);
    }


    public Quest getCurrentQuest() {
        if (currentQuestIndex >= questChain.size()) {
            return null;
        }
        return questChain.get(currentQuestIndex);
    }


    public void advanceToNextQuest() {
        currentQuestIndex++;
    }

    public boolean isChainComplete() {
        return currentQuestIndex >= questChain.size();
    }


    public String getChainProgress() {
        int completed = Math.min(currentQuestIndex, questChain.size());
        return completed + "/" + questChain.size() + " tutorial quests completed";
    }


    public void assignCurrentQuestToNPC(NPC npc) {
        Quest current = getCurrentQuest();
        if (current != null && npc != null) {
            npc.setQuest(current);
        }
    }


    public void update() {
        Quest current = getCurrentQuest();
        if (current != null && current.isCompleted()) {
            advanceToNextQuest();
            System.out.println("Tutorial quest completed! " + getChainProgress());

            Quest next = getCurrentQuest();
            if (next != null) {
                System.out.println("New tutorial quest available: " + next.getDescription());
            } else {
                System.out.println("Tutorial complete! You've mastered all game mechanics!");
            }
        }
    }

    public List<Quest> getAllQuests() {
        return new ArrayList<>(questChain);
    }


    public void reset() {
        currentQuestIndex = 0;
        questChain.clear();
        initializeQuestChain();
    }

    public int getCurrentQuestNumber() {
        return currentQuestIndex + 1;
    }

    public int getTotalQuestsCount() {
        return questChain.size();
    }
}