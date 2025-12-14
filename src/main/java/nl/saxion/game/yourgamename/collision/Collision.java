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
        if (aIsPlayer && CollisionManager.isCollidingWithPlayer(b)) {
            if (b.isPushable()) {
                resolvePlayerOverlap((Player) a, b);
                return;
            } else {
                b.setPushable(true);
                resolvePlayerOverlap((Player) a, b);
                return;
            }
        }
        if (bIsPlayer && CollisionManager.isCollidingWithPlayer(a)) {
            if (a.isPushable()) {
                resolvePlayerOverlap((Player) b, a);
                return;
            } else {
                a.setPushable(true);
                resolvePlayerOverlap((Player) b, a);
                return;
            }
        }
        if (aIsPlayer || bIsPlayer) {
            return;
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
        } else {
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

    public static void resolvePlayerOverlap(Player player, Collidable object) {
        // Find centers of player's collision box and object
        int playerCenterX = player.collisionBox.x + player.collisionBox.width / 2;
        int playerCenterY = player.collisionBox.y + player.collisionBox.height / 2;
        int objectCenterX = object.getX() + object.getWidth() / 2;
        int objectCenterY = object.getY() + object.getHeight() / 2;

        // Calculate overlap
        int overlapX = (player.collisionBox.width / 2 + object.getWidth() / 2) - Math.abs(playerCenterX - objectCenterX);
        int overlapY = (player.collisionBox.height / 2 + object.getHeight() / 2) - Math.abs(playerCenterY - objectCenterY);

        // If object is NOT pushable - move the player
        if (!object.isPushable()) {
            if (overlapX < overlapY) {
                // Horizontal resolution
                if (playerCenterX < objectCenterX) {
                    player.collisionBox.x -= overlapX;  // Player on left - push left
                } else {
                    player.collisionBox.x += overlapX;  // Player on right - push right
                }
            } else {
                // Vertical resolution
                if (playerCenterY < objectCenterY) {
                    player.collisionBox.y -= overlapY;  // Player below - push down
                } else {
                    player.collisionBox.y += overlapY;  // Player above - push up
                }
            }
        }
        // If object IS pushable - move the object
        else {
            if (overlapX < overlapY) {
                // Horizontal resolution
                if (playerCenterX < objectCenterX) {
                    object.setX(object.getX() + overlapX);  // Push object right
                } else {
                    object.setX(object.getX() - overlapX);  // Push object left
                }
            } else {
                // Vertical resolution
                if (playerCenterY < objectCenterY) {
                    object.setY(object.getY() + overlapY);  // Push object up
                } else {
                    object.setY(object.getY() - overlapY);  // Push object down
                }
            }
        }

        // Update player position from collision box
        player.setX(player.collisionBox.x);
        player.setY(player.collisionBox.y);
    }
}

