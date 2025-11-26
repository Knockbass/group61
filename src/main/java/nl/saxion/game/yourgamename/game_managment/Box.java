package nl.saxion.game.yourgamename.game_managment;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.movement.Vector2;

public class Box implements Collidable {
    private final int WIDTH = 100;
    private final int HEIGHT = 100;
    Vector2 position = new Vector2();

    public Box(){
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
        return false;
    }
}
