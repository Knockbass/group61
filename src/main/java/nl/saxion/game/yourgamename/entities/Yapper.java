package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.movement.Vector2;

public class Yapper extends Character {
    private int damage;
    private int attackSpeed;
    public Vector2 position = new Vector2();

    public Yapper(String name, int maxHealth, int damage, int attackSpeed){
        super(name, maxHealth);
        this.damage = damage;
        this.attackSpeed = attackSpeed;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public int getDamage(){
        return this.damage;
    }

    public int getAttackSpeed(){
        return this.attackSpeed;
    }
}
