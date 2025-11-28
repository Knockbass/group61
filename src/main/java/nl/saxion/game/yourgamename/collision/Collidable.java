package nl.saxion.game.yourgamename.collision;

import java.util.ArrayList;

public interface Collidable {
    int getX();
    int getY();
    void setX(int x);
    void setY(int y);
    int getWidth();
    int getHeight();
    boolean isPushable();
    void setPushable(boolean pushable);
}
