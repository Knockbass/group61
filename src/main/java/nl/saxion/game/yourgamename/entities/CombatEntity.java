package nl.saxion.game.yourgamename.entities;

public class CombatEntity extends Entity{
    public int hp;
    public Rectangle hitbox;
    public int attackDamage;

    public CombatEntity(){
    }

    public CombatEntity(String name){
        super(name);
    }
}
