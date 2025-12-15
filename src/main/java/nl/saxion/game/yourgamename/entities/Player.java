package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.game_managment.StatSystem;
import nl.saxion.game.yourgamename.game_managment.YourGameScreen;
import nl.saxion.game.yourgamename.movement.Vector2;

public class Player extends CombatEntity implements Collidable {
    private int movementSpeed;
    public boolean attacking;
    public float attackTimer, attackDuration;
    public Direction facing;
    public Rectangle collisionBox;
    private boolean isPushable = true;
    public boolean hasHitEnemy;
    private StatSystem statSystem = new StatSystem();

    public Player(String name, int movementSpeed) {
        super.name = name;
        super.entityWidth = 16;  // Match map tile width (16px)
        super.entityHeight = 24; // 1.5x tile height for better proportions
        super.position = new Vector2(200, YourGameScreen.worldHeight - entityHeight - 281);
        super.hitbox = new Rectangle();
        this.collisionBox = new Rectangle(getX() + 2,getY(),12,12);  // Adjusted for smaller player
        this.facing = Direction.RIGHT;
        this.attackTimer = 0f;          //can be used as duration of the animation of attack
        this.movementSpeed = movementSpeed;
        this.attackDuration = 0.8f;     //set the cooldown for player attack
        this.hasHitEnemy = false;
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

    @Override
    public int getHp(){
        return statSystem.getHP();
    }

    @Override
    public void setHp(int hp){
        this.statSystem.setHP(hp);
    }

    @Override
    public int getAttackDamage() {
        return this.statSystem.getKnowldge();
    }

    public StatSystem accessStatSystem() {
        return statSystem;
    }
}

