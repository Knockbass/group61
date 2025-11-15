package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.movement.Vector2;

public class Player extends Character {
    private int damage;
    private int attackSpeed;
    private int movementSpeed;
    StatSystem playerStats = new StatSystem();
    public Vector2 position = new Vector2();

    public Player(String name, int maxHealth, int damage, int attackSpeed, int movementSpeed){
        super(name, maxHealth);
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.movementSpeed = movementSpeed;
        this.health = maxHealth;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage(){
        return this.damage;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public int getAttackSpeed(){
        return this.attackSpeed;
    }

    public void setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public int getMovementSpeed(){
        return this.movementSpeed;
    }
}
