package nl.saxion.game.yourgamename.collision;

import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;
import java.util.List;

public class CollisionManager {
    public static List<Collidable> solid = new ArrayList<>();
    public static List<Collidable> dynamic = new ArrayList<>();
    public static Collidable solidBlock;

    public static void addEntity(Collidable entity) {
        if (entity.isPushable()) {
            dynamic.add(entity);
        } else {
            solid.add(entity);
        }
    }

    public static boolean isBlocked(Collidable a) {
        for (Collidable b : solid) {
            if (GameApp.rectOverlap(a.getX(), a.getY(), a.getWidth(), a.getHeight(),
                    b.getX(), b.getY(), b.getWidth(), b.getHeight())) {
                solidBlock = b;
                return true;
            }
        }

        solidBlock = null;
        return false;
    }

}
