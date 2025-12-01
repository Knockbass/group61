package nl.saxion.game.yourgamename.game_managment;

public class Stat {

    private int baseValue;
    private int currentValue;

    public Stat(int baseValue){
        this.baseValue = baseValue;
        this.currentValue = baseValue;
    }

    public int get(){
        return this.currentValue;
    }

    public int getBaseValue(){
        return this.baseValue;
    }

    public void setCurrentValue(int value){
        this.currentValue = value;
    }
    public void reset() {
        currentValue = baseValue;
    }

    public void setBaseValue(int baseValue){
        this.baseValue = baseValue;
    }

    //change current value to the baseValue(MaxValue) if current one is bigger
    public void applyMaxValueBound(){
        if(currentValue > baseValue){
            currentValue = baseValue;
        }
    }
}
