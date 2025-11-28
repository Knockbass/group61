package nl.saxion.game.yourgamename.collision;

import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.game_managment.YourGameScreen;
import nl.saxion.gameapp.GameApp;

public class Collision {
    public static void handleCollision(Collidable a, Collidable b) {
        if (!GameApp.rectOverlap(a.getX(), a.getY(), a.getWidth(), a.getHeight(),     //checks if two objects are colliding
                b.getX(), b.getY(), b.getWidth(), b.getHeight())) {
            return;
        }

        boolean aIsPlayer = a instanceof Player;
        boolean bIsPlayer = b instanceof Player;

        //pushable && unpushable vs player
        if (aIsPlayer) {
            if (b.isPushable()) {
                resolveObjectOverlap(b, a);
                return;
            } else {
                b.setPushable(true);
                resolveObjectOverlap(a, b);
                return;
            }
        }
        if (bIsPlayer) {
            if (a.isPushable()) {
                resolveObjectOverlap(a, b);
                return;
            } else {
                a.setPushable(true);
                resolveObjectOverlap(b, a);
                return;
            }
        }

        //pushable vs pushable
        if (CollisionManager.isCollidingWithPlayer(a)) {
            resolveObjectOverlap(b, a);
            return;
        } else if (CollisionManager.isCollidingWithPlayer(b)) {
            resolveObjectOverlap(a, b);
        } else if (a.isPushable() && b.isPushable()) {
            resolveObjectOverlap(b, a);
        }

        //unpushable vs unpushable
        if (!a.isPushable() && !b.isPushable() && CollisionManager.dynamic.contains(a)) {
            resolveObjectOverlap(a, b);
        } else{
            resolveObjectOverlap(b, a);
        }

        //pushable vs unpushable
        if (a.isPushable() && !b.isPushable()) {
            a.setPushable(false);
            resolveObjectOverlap(a, b);
            return;
        } else if (!a.isPushable() && b.isPushable()) {
            b.setPushable(false);
            resolveObjectOverlap(b, a);
            return;
        }

    }

    public static void resolveObjectOverlap(Collidable a, Collidable b) {  //in this method only the coordinates of {a} are changed
        //finds centers of objects
        int aCenterX = a.getX() + a.getWidth() / 2;
        int aCenterY = a.getY() + a.getHeight() / 2;
        int bCenterX = b.getX() + b.getWidth() / 2;
        int bCenterY = b.getY() + b.getHeight() / 2;

        //calculate overlap
        int overlapX = (a.getWidth() / 2 + b.getWidth() / 2) - Math.abs(aCenterX - bCenterX);
        int overlapY = (a.getHeight() / 2 + b.getHeight() / 2) - Math.abs(aCenterY - bCenterY);


        if (overlapX < overlapY) {
            if (aCenterX < bCenterX) {
                a.setX(a.getX() - overlapX);
            } else {
                a.setX(a.getX() + overlapX);
            }
        } else {
            if (aCenterY < bCenterY) {
                a.setY(a.getY() - overlapY);
            } else {
                a.setY(a.getY() + overlapY);
            }
        }
    }
}

