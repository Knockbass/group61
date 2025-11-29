package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.movement.Vector2;

public class Yapper extends CombatEntity implements Collidable {
    public float attackCooldown;
    public boolean attacking;
    public float attackTimer;
    private int maxHealth;
    private boolean isPushable = true;
    public boolean isDead;


    public Yapper(String name, int maxHealth, int damage, int attackCooldown) {
        super.name = name;
        super.entityWidth = 100;
        super.entityHeight = 100;
        super.position = new Vector2(500, 500);
        super.setHp(maxHealth);
        super.setAttackDamage(damage);
        this.isDead = false;
        this.attackTimer = 0f;
        this.attackCooldown = attackCooldown;
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

}
