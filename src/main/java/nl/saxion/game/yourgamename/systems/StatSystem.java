package nl.saxion.game.yourgamename.systems;

import nl.saxion.game.yourgamename.game_managment.Stat;
import nl.saxion.game.yourgamename.game_managment.StatModifier;

public class StatSystem {

    //this class has 4 main stats that only Player has
    private Stat knowledge = new Stat(10);      //used as damage
    private Stat mentalHealth = new Stat(100);  //used for spawning certain amount of enemies
    private Stat money = new Stat(0);           //used to buy beer
    private Stat energy = new Stat(100);        //idk
    private Stat hp = new Stat(100);            //player's hp
    private Stat beerCount = new Stat(1);
    private Stat killedYappersAmount = new Stat(0);
    private int currentDay = 1;                 // Track current day (increments when sleeping)

    //increase mental health and energy
    public void sleep(){
        this.mentalHealth.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +25).getModifiedValue(mentalHealth));
        this.energy.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +25).getModifiedValue(energy));
        mentalHealth.applyMaxValueBound();
        energy.applyMaxValueBound();
        // Increment day when sleeping
        currentDay++;
    }
    
    public int getCurrentDay() {
        return currentDay;
    }

    //increase knowledge, decrease mental health and energy
    public void study(){
        this.mentalHealth.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, -10).getModifiedValue(mentalHealth));
        this.knowledge.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +15).getModifiedValue(knowledge));
        this.energy.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, -10).getModifiedValue(energy));
        mentalHealth.applyMaxValueBound();
        energy.applyMaxValueBound();
    }

    //study at university - gives knowledge and decreases energy
    public void studyAtUniversity(){
        this.knowledge.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +15).getModifiedValue(knowledge));
        this.energy.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, -10).getModifiedValue(energy));
        energy.applyMaxValueBound();
    }

    //increase mentalHealth, Health, and energy, decrease knowledge
    public void dringBeer(){
        if (this.beerCount.get() > 0){
        this.beerCount.setCurrentValue((new StatModifier(StatModifier.Type.ADDING,-1).getModifiedValue(beerCount)));
        this.knowledge.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, -5).getModifiedValue(knowledge));
        this.mentalHealth.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +20).getModifiedValue(mentalHealth));
        this.energy.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +20).getModifiedValue(energy));
        this.hp.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +25).getModifiedValue(hp));
        mentalHealth.applyMaxValueBound();
        energy.applyMaxValueBound();
        hp.applyMaxValueBound();}
        else {
            // add err msg for you don't have beer !
        }
    }

    public int getMentalHealth() {
        return this.mentalHealth.get();
    }

    public int getKnowldge(){
        return this.knowledge.get();
    }

    public int getMoney(){
        return this.money.get();
    }

    public int getEnergy(){
        return this.energy.get();
    }

    public int getHP(){
        return this.hp.get();
    }

    public void setMaxHP(int maxHP){
        this.hp.setBaseValue(maxHP);
    }

    public void setHP(int hp){
        this.hp.setCurrentValue(hp);
    }

    public int getMaxHP(){
        return this.hp.getBaseValue();
    }

    public int getMaxMentalHealth(){
        return this.mentalHealth.getBaseValue();
    }

    public int getMaxEnergy(){
        return this.energy.getBaseValue();
    }

    public int getMaxKnowledge(){
        return this.knowledge.getBaseValue();
    }

    public int getKilledYappersAmount(){return this.killedYappersAmount.getBaseValue();}

    // Getters for Stat objects (used by NPCSystem for rewards)
    public Stat getKnowledgeStat() {
        return knowledge;
    }

    public Stat getMentalHealthStat() {
        return mentalHealth;
    }

    public Stat getEnergyStat() {
        return energy;
    }

    public Stat getMoneyStat() {
        return money;
    }

    public Stat getHPStat() {
        return hp;
    }


    public Stat getBeerCountStat() {
        return beerCount;
    }


    public int getBeerCount() {
        return beerCount.get();
    }

    public void setMoney(int money){
        this.money.setCurrentValue(money);
    }

    public void setBeerCount(int beerCount){
        this.beerCount.setCurrentValue(beerCount);
    }

    public void setKilledYappersAmount(int killedYappersAmount){this.killedYappersAmount.setCurrentValue(killedYappersAmount);}

}
