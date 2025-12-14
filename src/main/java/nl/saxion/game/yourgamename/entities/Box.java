package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.movement.Vector2;

import java.util.ArrayList;

public class Box extends Entity implements Collidable {
    private final float WIDTH;
    private final float HEIGHT;
    Vector2 position = new Vector2();
    private final boolean isPushable = false;

    public Box(float width, float height){
        WIDTH = width;
        HEIGHT = height;
    }

    public Box(float x, float y, float width, float height){
        setX((int) x);
        setY((int) y);
        this.HEIGHT = height;
        this.WIDTH = width;
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
        return (int) WIDTH;
    }

    @Override
    public int getHeight() {
        return (int) HEIGHT;
    }

    @Override
    public boolean isPushable() {
        return isPushable;
    }

    @Override
    public void setPushable(boolean pushable){
    }
}
