package nl.saxion.game.yourgamename.game_managment;

public class StatModifier {
    //contains two types of stat modifying
    public enum Type {
        MULTIPLYING,    // x2, x0.5
        ADDING          //+20, -30
    }

    private Type type;
    private float modifyByValue;  //stores how much the value should be changed

    public StatModifier(Type type, float modifyByValue) {
        this.type = type;
        this.modifyByValue = modifyByValue;
    }

    //Modifies value and returns it
    public int getModifiedValue(Stat stat){
        return switch (this.type){
            case ADDING ->  (int) (stat.get() + modifyByValue);
            case MULTIPLYING -> (int) (stat.get() * modifyByValue);
        };
    }
}
