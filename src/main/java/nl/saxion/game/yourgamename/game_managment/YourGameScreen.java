package nl.saxion.game.yourgamename.game_managment;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import nl.saxion.game.yourgamename.movement.PlayerMovement;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;
import nl.saxion.game.yourgamename.entities.*;

public class YourGameScreen extends ScalableGameScreen {
    final int PLAYER_SIZE = 150;
    public static int worldWidth = 1280;
    public static int worldHeight = 720;
    Player player;

    public YourGameScreen() {
        super(worldWidth, worldHeight);
    }

    @Override
    public void show() {
        GameApp.addTexture("player", "textures/bear.png");
        player = new Player("test", 100, 10, 5, 300);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        GameApp.clearScreen("teal-300");

        PlayerMovement.checkMovementKeyPressed(player, delta);
        PlayerMovement.setPositionBorder(player, worldWidth, worldHeight);

        GameApp.startSpriteRendering();
        GameApp.drawTexture("player", player.position.x, player.position.y, PLAYER_SIZE, PLAYER_SIZE);
        GameApp.endSpriteRendering();
    }

    @Override
    public void hide() {
        GameApp.disposeTexture("player");
    }
}
