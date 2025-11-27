package nl.saxion.game.yourgamename.entities;

public abstract class LivingEntity {
    protected String name;
    protected int health;
    protected int maxHealth;

    public LivingEntity(String name, int maxHealth){
        this.name = name;
        this.maxHealth = maxHealth;
    }
}

