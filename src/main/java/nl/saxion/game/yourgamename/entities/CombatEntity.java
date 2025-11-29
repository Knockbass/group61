package nl.saxion.game.yourgamename.entities;

public class CombatEntity extends Entity{
    private int hp;
    public Rectangle hitbox;
    private int attackDamage;

    public CombatEntity(){
    }

    public CombatEntity(String name){
        super(name);
    }

    public int getHp() {
        return this.hp;
    }

    public int getAttackDamage() {
        return this.attackDamage;
    }

    public void setHp(int hp){
        this.hp = hp;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }
}
