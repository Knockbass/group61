package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.movement.Vector2;

public class Yapper extends LivingEntity implements Collidable {
    private final int WIDTH = 100;
    private final int HEIGHT = 100;
    private int damage;
    private int attackSpeed;
    private int movementSpeed;
    private float detectionRadius;
    public Vector2 position = new Vector2(500, 500);

    public Yapper(String name, int maxHealth, int damage, int attackSpeed, int movementSpeed, float detectionRadius) {
        super(name, maxHealth);
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.movementSpeed = movementSpeed;
        this.detectionRadius = detectionRadius;
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

    public int getMovementSpeed() {
        return this.movementSpeed;
    }

    public void setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public float getDetectionRadius() {
        return detectionRadius;
    }

    public void setDetectionRadius(float detectionRadius) {
        this.detectionRadius = detectionRadius;
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

    /**
     * Moves the enemy towards the player if the player is within the detection radius
     */
    public void moveTowardsPlayer(Player player, float delta) {
        // Calculate distance between enemy and player
        int dx = player.getX() - this.getX();
        int dy = player.getY() - this.getY();
        
        // Calculate distance using Pythagorean theorem
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Check if player is within detection radius
        if (distance <= detectionRadius && distance > 0) {
            // Calculate movement amount based on speed and delta time
            int moveAmount = Math.round(movementSpeed * delta);
            
            // Normalize direction vector and apply movement
            double normalizedDx = dx / distance;
            double normalizedDy = dy / distance;
            
            // Move enemy towards player
            position.setX(position.getX() + (int)(normalizedDx * moveAmount));
            position.setY(position.getY() + (int)(normalizedDy * moveAmount));
        }
    }

}
