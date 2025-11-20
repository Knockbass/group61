package nl.saxion.game.yourgamename.entities;

public abstract class Character {
    protected String name;
    protected int health;
    protected int maxHealth;

    public Character(String name, int maxHealth){
        this.name = name;
        this.maxHealth = maxHealth;
    }
}

