package nl.saxion.game.yourgamename.movement;

import com.badlogic.gdx.Input;
import nl.saxion.game.yourgamename.entities.Direction;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.gameapp.GameApp;

public class PlayerMovement{

    public static void checkMovementKeyPressed(Player player, float delta){
        int moveAmount = Math.round(player.getMovementSpeed() * delta);
        if (GameApp.isKeyPressed(Input.Keys.RIGHT) || GameApp.isKeyPressed(Input.Keys.D)){
            player.position.setX(player.position.getX() + moveAmount);
            player.collisionBox.x += moveAmount;
            player.facing = Direction.RIGHT;

        } else if(GameApp.isKeyPressed(Input.Keys.LEFT) || GameApp.isKeyPressed(Input.Keys.A) ){
            player.position.setX(player.position.getX() - moveAmount);
            player.collisionBox.x -= moveAmount;
            player.facing = Direction.LEFT;
        }

        if (GameApp.isKeyPressed(Input.Keys.UP) || GameApp.isKeyPressed(Input.Keys.W)  ){
            player.position.setY(player.position.getY() + moveAmount);
            player.collisionBox.y += moveAmount;
            player.facing = Direction.UP;
        } else if(GameApp.isKeyPressed(Input.Keys.DOWN) || GameApp.isKeyPressed(Input.Keys.S) ) {
            player.position.setY(player.position.getY() - moveAmount);
            player.collisionBox.y -= moveAmount;
            player.facing = Direction.DOWN;
        }
    }
}