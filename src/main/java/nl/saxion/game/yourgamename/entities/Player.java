package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.movement.Vector2;
import nl.saxion.gameapp.GameApp;

public class Player extends CombatEntity implements Collidable {
    private int movementSpeed;
    public boolean attacking;
    public float attackTimer, attackDuration;
    public Direction facing;
    private int maxHP;
    private boolean isPushable = true;
    public boolean hasHitEnemy;
    StatSystem playerStats = new StatSystem();

    public Player(String name, int maxHealth, int damage, int movementSpeed) {
        super.name = name;
        super.entityWidth = 100;
        super.entityHeight = 100;
        super.position = new Vector2();
        super.hitbox = new Rectangle();
        this.facing = Direction.RIGHT;
        this.attackDamage = damage;
        this.attackTimer = 0f;          //can be used as duration of the animation of attack
        this.movementSpeed = movementSpeed;
        this.hp = maxHealth;
        this.attackDuration = 0.8f;     //set the cooldown for player attack
        this.hasHitEnemy = false;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getAttackDamage() {
        return this.attackDamage;
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
        return entityWidth;
    }

    @Override
    public int getHeight() {
        return entityHeight;
    }

    @Override
    public boolean isPushable() {
        return isPushable;
    }

    @Override
    public void setPushable(boolean pushable) {
        this.isPushable = pushable;
    }

    public StatSystem getPlayerStats() {
        return playerStats;
    }

}

