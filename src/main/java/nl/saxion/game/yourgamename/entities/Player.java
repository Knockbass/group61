package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.movement.Vector2;

public class Player extends Entity implements Collidable {
    private final int WIDTH = 100;
    private final int HEIGHT = 100;
    private int damage;
    private int attackSpeed;
    private int movementSpeed;
    private int health;
    private int maxHealth;
    private boolean isPushable = true;
    StatSystem playerStats = new StatSystem();
    public Vector2 position = new Vector2();

    public Player(String name, int maxHealth, int damage, int attackSpeed, int movementSpeed) {
        super(name);
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.movementSpeed = movementSpeed;
        this.health = maxHealth;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return this.damage;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public int getAttackSpeed() {
        return this.attackSpeed;
    }

    public void setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public int getMovementSpeed() {
        return this.movementSpeed;
    }

    @Override
    public int getX() {
        return position.getX();
    }

    @Override
    public void setX(int x) {
        position.setX(x);
    }

    @Override
    public int getY() {
        return position.getY();
    }

    @Override
    public void setY(int y) {
        position.setY(y);
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public boolean isPushable() {
        return isPushable;
    }

    @Override
    public void setPushable(boolean pushable){
        this.isPushable = pushable;
    }

    public StatSystem getPlayerStats() {
        return playerStats;
    }
}
