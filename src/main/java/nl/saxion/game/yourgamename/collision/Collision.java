package nl.saxion.game.yourgamename.collision;

import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.game_managment.YourGameScreen;
import nl.saxion.gameapp.GameApp;

public class Collision {
    public static void isColliding(Collidable a, Collidable b) {
        if (!GameApp.rectOverlap(a.getX(), a.getY(), a.getWidth(), a.getHeight(),     //checks if two objects are colliding
                b.getX(), b.getY(), b.getWidth(), b.getHeight())) {
            return;
        }

        if (b.isPushable()) {//{a} is always pushable
            boolean isABlocked = CollisionManager.isBlocked(a);
            boolean isBBlocked = CollisionManager.isBlocked(b);
            if (isABlocked) {
                resolveObjectOverlap(a, CollisionManager.solidBlock);
                resolveObjectOverlap(b, a);
            } else if (isBBlocked) {
                resolveObjectOverlap(b, CollisionManager.solidBlock);
                resolveObjectOverlap(a, b);
            }

            if (a instanceof Player && !isBBlocked) {
                resolveObjectOverlap(b, a);
            } else if (a instanceof Player) {
                resolveObjectOverlap(a, b);
            } else if (b instanceof Player && !isABlocked) {
                resolveObjectOverlap(a, b);
            } else if (b instanceof Player) {
                resolveObjectOverlap(b, a);
            } else {
                resolveObjectOverlap(a, b);
            }
        } else {        //{a} is pushable, but {b} is not
            if (CollisionManager.isBlocked(a)) {
                resolveObjectOverlap(a, b);
            }

            if (a instanceof Player) {
                resolveObjectOverlap(a, b);
            }
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

