package nl.saxion.game.yourgamename.collision;

import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.game_managment.YourGameScreen;
import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;
import java.util.List;

public class CollisionManager {
    public static List<Collidable> collidableEntities = new ArrayList<>();
    public static List<Collidable> dynamic = new ArrayList<>();

    public static void addEntity(Collidable entity) {
        if (entity.isPushable()){
            dynamic.add(entity);
        }
        collidableEntities.add(entity);
    }

    public static void checkCollision() {
        for (Collidable a : collidableEntities) {
            for (Collidable b : collidableEntities) {
                if (!(a == b)) {
                    Collision.handleCollision(a, b);
                }
            }
        }
    }

    public static boolean isCollidingWithPlayer(Collidable a) {
        Player player = YourGameScreen.player;

        return GameApp.rectOverlap(player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                a.getX(), a.getY(), a.getWidth(), a.getHeight());
    }
}

