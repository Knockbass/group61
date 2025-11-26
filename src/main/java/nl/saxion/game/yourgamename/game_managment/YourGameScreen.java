package nl.saxion.game.yourgamename.game_managment;

import nl.saxion.game.yourgamename.collision.*;
import nl.saxion.game.yourgamename.movement.*;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;
import nl.saxion.game.yourgamename.entities.*;

import java.util.List;

public class YourGameScreen extends ScalableGameScreen {
    public static int worldWidth = 1280;
    public static int worldHeight = 720;
    Player player;
    Yapper yapper;
    Yapper yapper2;
    Box box;


    public YourGameScreen() {
        super(worldWidth, worldHeight);
    }

    @Override
    public void show() {
        GameApp.addTexture("player", "textures/bear.png");
        GameApp.addTexture("yapper2", "textures/crocodile.png");
        GameApp.addTexture("yapper", "textures/crocodile.png");
        GameApp.addTexture("box", "textures/rhino.png");
        player = new Player("test", 100, 10, 5, 300);
        yapper2 = new Yapper("testCollision", 100, 20, 5);
        yapper2.setX(700);
        yapper2.setY(300);
        box = new Box(200, 100);
        yapper = new Yapper("common", 100, 10, 100);
        CollisionManager.addEntity(player);
        CollisionManager.addEntity(yapper2);
        CollisionManager.addEntity(yapper);
        CollisionManager.addEntity(box);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        GameApp.clearScreen("teal-300");

        PlayerMovement.checkMovementKeyPressed(player, delta);
        WorldBorder.clampToWorldBounds(yapper2, worldWidth, worldHeight);
        WorldBorder.clampToWorldBounds(player, worldWidth, worldHeight);
        WorldBorder.clampToWorldBounds(yapper, worldWidth, worldHeight);


         for (int i = 0; i < CollisionManager.dynamic.size(); i++) {    //implement collision for pushable entity in the list
            Collidable a = CollisionManager.dynamic.get(i);

            for (int j = i + 1; j < CollisionManager.dynamic.size(); j++) {
                Collidable b = CollisionManager.dynamic.get(j);
                Collision.isColliding(a, b);
            }
        }

        for (Collidable a : CollisionManager.dynamic) {        //implement collision for unPushable entity in the list
            for (Collidable s : CollisionManager.solid) {
                Collision.isColliding(a, s);
            }
        }

        GameApp.startSpriteRendering();
        GameApp.drawTexture("box", box.getX(), box.getY());
        GameApp.drawTexture("player", player.position.getX(), player.position.getY(), player.getWidth(), player.getHeight());
        GameApp.drawTexture("yapper", yapper.position.getX(), yapper.position.getY(), yapper.getWidth(), yapper.getHeight());
        GameApp.drawTexture("yapper2", yapper2.position.getX(), yapper2.position.getY(), yapper2.getWidth(), yapper2.getHeight());
        GameApp.endSpriteRendering();
    }

    @Override
    public void hide() {
        GameApp.disposeTexture("player");
        GameApp.disposeTexture("crocodile");
        GameApp.disposeTexture("yapper2");
        GameApp.disposeTexture("box");
    }
}
