package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.movement.Vector2;

import java.util.ArrayList;

public class Box extends Entity implements Collidable {
    private final int WIDTH = 100;
    private final int HEIGHT = 100;
    Vector2 position = new Vector2();
    private final boolean isPushable = false;

    public Box(){
        super("Nameless");
    }

    public Box(int x, int y){
        setX(x);
        setY(y);
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
    }
}
