package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.movement.Vector2;

public class Yapper extends LivingEntity implements Collidable {
    private final int WIDTH = 100;
    private final int HEIGHT = 100;
    private int damage;
    private int attackSpeed;
    public Vector2 position = new Vector2(500, 500);

    public Yapper(String name, int maxHealth, int damage, int attackSpeed) {
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

    public int getDamage() {
        return this.damage;
    }

    public int getAttackSpeed() {
        return this.attackSpeed;
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
        return true;
    }

}
