package nl.saxion.game.yourgamename.quest_logic;

import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.game_managment.StatModifier;
import nl.saxion.game.yourgamename.systems.StatSystem;

public class Quest {
    public enum QuestState {
        NOT_STARTED,  // Quest not accepted yet
        ACTIVE,       // Quest accepted and tracking progress
        COMPLETED     // Quest completed
    }

    private String description;
    private QuestState state;
    private Reward reward;
    private QuestObjective objective;
    private int progress;
    private int initialStatValue; // Track stat value when quest was accepted

    public Quest(){}

    public Quest(String description, Reward reward) {
        this.description = description;
        this.state = QuestState.NOT_STARTED;
        this.reward = reward != null ? reward : new Reward();
        this.objective = null;
        this.progress = 0;
        this.initialStatValue = 0;
    }

    public Quest(String description) {
        this(description, new Reward());
    }

    public Quest(String description, Reward reward, QuestObjective objective) {
        this.description = description;
        this.state = QuestState.NOT_STARTED;
        this.reward = reward != null ? reward : new Reward();
        this.objective = objective;
        this.progress = 0;
        this.initialStatValue = 0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return state == QuestState.COMPLETED;
    }

    public boolean isActive() {
        return state == QuestState.ACTIVE;
    }

    public boolean isNotStarted() {
        return state == QuestState.NOT_STARTED;
    }

    public QuestState getState() {
        return state;
    }

    public void setState(QuestState state) {
        this.state = state;
    }

    public void acceptQuest(Player player) {
        if (state != QuestState.NOT_STARTED) {
            return; // Can only accept if not started
        }
        
        state = QuestState.ACTIVE;
        
        // Record initial stat value when quest is accepted
        if (objective != null) {
            StatSystem stats = player.accessStatSystem();
            switch (objective.getType()) {
                case KNOWLEDGE:
                    initialStatValue = stats.getKnowldge();
                    break;
                case MENTAL_HEALTH:
                    initialStatValue = stats.getMentalHealth();
                    break;
                case ENERGY:
                    initialStatValue = stats.getEnergy();
                    break;
                case MONEY:
                    initialStatValue = stats.getMoney();
                    break;
                case HP:
                    initialStatValue = stats.getHP();
                    break;
                case BEER:
                    initialStatValue = stats.getBeerCount();
                    break;
                case YAPPERS_COUNT:
                    initialStatValue = stats.getKilledYappersAmount();
                    break;
                case ENTER_UNIVERSITY:
                    initialStatValue = 0;
                    break;
            }
            progress = 0;
        }
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public QuestObjective getObjective() {
        return objective;
    }

    public void setObjective(QuestObjective objective) {
        this.objective = objective;
        this.progress = 0;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getProgressText() {
        if (objective == null) {
            return "";
        }
        return progress + "/" + objective.getTargetAmount();
    }

    public boolean checkObjective(Player player) {
        if (objective == null || state != QuestState.ACTIVE) {
            return false; // Only check if quest is active
        }

        StatSystem stats = player.accessStatSystem();
        int currentValue = 0;

        switch (objective.getType()) {
            case KNOWLEDGE:
                currentValue = stats.getKnowldge();
                break;
            case MENTAL_HEALTH:
                currentValue = stats.getMentalHealth();
                break;
            case ENERGY:
                currentValue = stats.getEnergy();
                break;
            case MONEY:
                currentValue = stats.getMoney();
                break;
            case HP:
                currentValue = stats.getHP();
                break;
            case BEER:
                currentValue = stats.getBeerCount();
                break;
            case YAPPERS_COUNT:
                currentValue = stats.getKilledYappersAmount();
                break;
            case ENTER_UNIVERSITY:        //it is chacked in other methods
                currentValue = progress;
                break;
        }

        // Calculate progress from the initial value when quest was accepted
        int statChange = currentValue - initialStatValue;
        progress = Math.min(statChange, objective.getTargetAmount());
        
        return progress >= objective.getTargetAmount();
    }

    public void applyRewards(Player player) {
        if (state == QuestState.COMPLETED || reward == null) {
            return;
        }

        // Check if objective is met before applying rewards
        if (objective != null && !checkObjective(player)) {
            return;
        }

        StatSystem statSystem = player.accessStatSystem();

        // Apply stat rewards using StatModifier system
        if (reward.getKnowledgeReward() != 0) {
            statSystem.getKnowledgeStat().setCurrentValue(
                new StatModifier(StatModifier.Type.ADDING, reward.getKnowledgeReward())
                    .getModifiedValue(statSystem.getKnowledgeStat())
            );
        }
        if (reward.getMentalHealthReward() != 0) {
            statSystem.getMentalHealthStat().setCurrentValue(
                new StatModifier(StatModifier.Type.ADDING, reward.getMentalHealthReward())
                    .getModifiedValue(statSystem.getMentalHealthStat())
            );
            statSystem.getMentalHealthStat().applyMaxValueBound();
        }
        if (reward.getEnergyReward() != 0) {
            statSystem.getEnergyStat().setCurrentValue(
                new StatModifier(StatModifier.Type.ADDING, reward.getEnergyReward())
                    .getModifiedValue(statSystem.getEnergyStat())
            );
            statSystem.getEnergyStat().applyMaxValueBound();
        }
        if (reward.getMoneyReward() != 0) {
            statSystem.getMoneyStat().setCurrentValue(
                new StatModifier(StatModifier.Type.ADDING, reward.getMoneyReward())
                    .getModifiedValue(statSystem.getMoneyStat())
            );
        }
        if (reward.getHpReward() != 0) {
            statSystem.getHPStat().setCurrentValue(
                new StatModifier(StatModifier.Type.ADDING, reward.getHpReward())
                    .getModifiedValue(statSystem.getHPStat())
            );
            statSystem.getHPStat().applyMaxValueBound();
        }
        if (reward.getBeerReward() != 0) {
            statSystem.getBeerCountStat().setCurrentValue(
                new StatModifier(StatModifier.Type.ADDING, reward.getBeerReward())
                    .getModifiedValue(statSystem.getBeerCountStat())
            );
        }

        state = QuestState.COMPLETED;
    }


    public static class Reward {
        private int knowledgeReward = 0;
        private int mentalHealthReward = 0;
        private int energyReward = 0;
        private int moneyReward = 0;
        private int hpReward = 0;
        private int beerReward = 0;

        public Reward() {
        }

        public Reward(int knowledge, int mentalHealth, int energy, int money, int hp, int beer) {
            this.knowledgeReward = knowledge;
            this.mentalHealthReward = mentalHealth;
            this.energyReward = energy;
            this.moneyReward = money;
            this.hpReward = hp;
            this.beerReward = beer;
        }

        public int getKnowledgeReward() {
            return knowledgeReward;
        }

        public void setKnowledgeReward(int knowledgeReward) {
            this.knowledgeReward = knowledgeReward;
        }

        public int getMentalHealthReward() {
            return mentalHealthReward;
        }

        public void setMentalHealthReward(int mentalHealthReward) {
            this.mentalHealthReward = mentalHealthReward;
        }

        public int getEnergyReward() {
            return energyReward;
        }

        public void setEnergyReward(int energyReward) {
            this.energyReward = energyReward;
        }

        public int getMoneyReward() {
            return moneyReward;
        }

        public void setMoneyReward(int moneyReward) {
            this.moneyReward = moneyReward;
        }

        public int getHpReward() {
            return hpReward;
        }

        public void setHpReward(int hpReward) {
            this.hpReward = hpReward;
        }

        public int getBeerReward() {
            return beerReward;
        }

        public void setBeerReward(int beerReward) {
            this.beerReward = beerReward;
        }
    }

    public static class QuestObjective {
        public enum ObjectiveType {
            KNOWLEDGE,
            MENTAL_HEALTH,
            ENERGY,
            MONEY,
            HP,
            BEER,
            YAPPERS_COUNT,
            ENTER_UNIVERSITY,
            FINISH_EXAM
        }

        private ObjectiveType type;
        private int targetAmount;

        public QuestObjective(){}

        public QuestObjective(ObjectiveType type, int targetAmount) {
            this.type = type;
            this.targetAmount = targetAmount;
        }

        public ObjectiveType getType() {
            return type;
        }

        public void setType(ObjectiveType type) {
            this.type = type;
        }

        public int getTargetAmount() {
            return targetAmount;
        }

        public void setTargetAmount(int targetAmount) {
            this.targetAmount = targetAmount;
        }

        public String getTypeName() {
            switch (type) {
                case KNOWLEDGE:
                    return "Knowledge";
                case MENTAL_HEALTH:
                    return "Mental Health";
                case ENERGY:
                    return "Energy";
                case MONEY:
                    return "Money";
                case HP:
                    return "HP";
                case BEER:
                    return "Beer";
                case YAPPERS_COUNT:
                    return "Killed Yappers";
                case ENTER_UNIVERSITY:
                    return "Story Event";
                default:
                    return "Unknown";
            }
        }
    }
}
