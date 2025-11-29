package nl.saxion.game.yourgamename.collision;

import nl.saxion.game.yourgamename.entities.CombatEntity;
import nl.saxion.game.yourgamename.entities.Entity;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.entities.Yapper;
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
        for (int i = 0; i < collidableEntities.size(); i++) {
            for (int j = i + 1; j < collidableEntities.size(); j++) { // ← j = i + 1
                Collidable a = collidableEntities.get(i);
                Collidable b = collidableEntities.get(j);
                Collision.handleCollision(a, b);
            }
        }
    }

    public static boolean isCollidingWithPlayer(Collidable a) {
        Player player = YourGameScreen.player;

        return GameApp.rectOverlap(player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                a.getX(), a.getY(), a.getWidth(), a.getHeight());
    }

    public static boolean isCollidingWithHitbox(CombatEntity attacker, CombatEntity receiver){
        return GameApp.rectOverlap(attacker.hitbox.x,attacker.hitbox.y, attacker.hitbox.width, attacker.hitbox.height,
                                   receiver.position.getX(), receiver.position.getY(), receiver.entityWidth, receiver.entityHeight);
    }
}

