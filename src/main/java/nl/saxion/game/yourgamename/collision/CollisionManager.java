package nl.saxion.game.yourgamename.collision;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import nl.saxion.game.yourgamename.entities.Box;
import nl.saxion.game.yourgamename.entities.CombatEntity;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.screens.YourGameScreen;
import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;
import java.util.List;

public class CollisionManager {
    public static List<Collidable> collidableEntities = new ArrayList<>();
    public static List<Collidable> dynamic = new ArrayList<>();

    public static void addEntity(Collidable entity) {
        if (entity.isPushable()) {
            dynamic.add(entity);
        }
        collidableEntities.add(entity);
    }

    public static void addMapObjects(MapObjects objects){
        for (MapObject object: objects){

            if (object instanceof RectangleMapObject rectObject){
                Rectangle rectangle = rectObject.getRectangle();    //Rectanlge is libDGX class
                Box rect = new Box(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                CollisionManager.addEntity(rect);
            }
        }
    }

    public static void checkCollision(float vL, float vR, float vT, float vB) {//left,right,top,bottom coordinates of viewport
        List<Collidable> visibleEntities = new ArrayList<>();

        for (Collidable entity : collidableEntities) {     //checks if entity is visible inside of viewport and adds it to the list if yes
            if (!(entity.getX() + entity.getWidth() < vL || entity.getX() > vR ||
                    entity.getY() + entity.getHeight() < vB || entity.getY() > vT)) {
                visibleEntities.add(entity);
            }
        }

        for (int i = 0; i < visibleEntities.size(); i++) { //checks collision of visible entities
            for (int j = i + 1; j < visibleEntities.size(); j++) {
                Collidable a = visibleEntities.get(i);
                Collidable b = visibleEntities.get(j);
                Collision.handleCollision(a, b);
            }
        }
    }


    public static boolean isCollidingWithPlayer(Collidable a) {
        Player player = YourGameScreen.player;
        return GameApp.rectOverlap(player.collisionBox.x, player.collisionBox.y, player.collisionBox.width, player.collisionBox.height,
                a.getX(), a.getY(), a.getWidth(), a.getHeight());
    }

    public static boolean isCollidingWithHitbox(CombatEntity attacker, CombatEntity receiver) {
        return GameApp.rectOverlap(attacker.hitbox.x, attacker.hitbox.y, attacker.hitbox.width, attacker.hitbox.height,
                receiver.position.getX(), receiver.position.getY(), receiver.entityWidth, receiver.entityHeight);
    }
}

