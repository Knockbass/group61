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

        if (!b.isPushable()) {
            resolveObjectOverlap(a, b);
            return;
        }

        boolean isABlocked = CollisionManager.isBlocked(a);
        boolean isBBlocked = CollisionManager.isBlocked(b);

        boolean aIsPlayer = a instanceof Player;
        boolean bIsPlayer = b instanceof Player;

        if (aIsPlayer) {
            if (isBBlocked) {
                resolveObjectOverlap(b, CollisionManager.solidBlock);
                resolveObjectOverlap(a, b);

                return;
            } else {
                resolveObjectOverlap(b, a);
            }
            return;
        }

        if (bIsPlayer) {
            if (isABlocked) {
                resolveObjectOverlap(a, CollisionManager.solidBlock);
                return;
            } else {
                resolveObjectOverlap(a, b);
            }
            return;
        }

        if (isABlocked && !isBBlocked) {
            resolveObjectOverlap(b, a);
        } else if (isBBlocked && !isABlocked) {
            resolveObjectOverlap(a, b);
        } else if (isABlocked) {
            resolveObjectOverlap(a, CollisionManager.solidBlock);
            resolveObjectOverlap(b, CollisionManager.solidBlock);
        } else {
            resolveObjectOverlap(a, b);
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

