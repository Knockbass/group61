package nl.saxion.game.yourgamename.game_managment;

public class StatSystem {

    //this class has 4 main stats that only Player has
    private Stat knowledge = new Stat(10);      //used as damage
    private Stat mentalHealth = new Stat(100);  //used for spawning certain amount of enemies
    private Stat money = new Stat(0);           //used to buy beer
    private Stat energy = new Stat(100);        //idk
    private Stat hp = new Stat(100);            //player's hp


    //increase mental health and energy
    public void sleep(){
        this.mentalHealth.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +25).getModifiedValue(mentalHealth));
        this.energy.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +25).getModifiedValue(energy));
        mentalHealth.applyMaxValueBound();
        energy.applyMaxValueBound();
    }

    //increase knowledge, decrease mental health and energy
    public void study(){
        this.mentalHealth.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, -10).getModifiedValue(mentalHealth));
        this.knowledge.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +15).getModifiedValue(knowledge));
        this.energy.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, -10).getModifiedValue(energy));
        mentalHealth.applyMaxValueBound();
        energy.applyMaxValueBound();
    }

    //increase mentalHealth, Health, and energy, decrease knowledge
    public void dringBeer(){
        this.knowledge.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, -5).getModifiedValue(knowledge));
        this.mentalHealth.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +20).getModifiedValue(mentalHealth));
        this.energy.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +20).getModifiedValue(energy));
        this.hp.setCurrentValue(new StatModifier(StatModifier.Type.ADDING, +25).getModifiedValue(hp));
        mentalHealth.applyMaxValueBound();
        energy.applyMaxValueBound();
        hp.applyMaxValueBound();
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
}
