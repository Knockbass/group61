package nl.saxion.game.yourgamename.movement;

import com.badlogic.gdx.Input;
import nl.saxion.game.yourgamename.entities.LivingEntity;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.entities.Yapper;
import nl.saxion.gameapp.GameApp;

public class PlayerMovement{

    public static void checkMovementKeyPressed(Player player, float delta){
        if (GameApp.isKeyPressed(Input.Keys.RIGHT) || GameApp.isKeyPressed(Input.Keys.D)){
            player.position.setX(player.position.getX() + Math.round(player.getMovementSpeed() * delta));  //by multiplying on delta the movement speed of player is same when different fps
        } else if(GameApp.isKeyPressed(Input.Keys.LEFT) || GameApp.isKeyPressed(Input.Keys.A) ){
            player.position.setX(player.position.getX() - Math.round(player.getMovementSpeed() * delta));
        }

        if (GameApp.isKeyPressed(Input.Keys.UP) || GameApp.isKeyPressed(Input.Keys.W)  ){
            player.position.setY(player.position.getY() + Math.round(player.getMovementSpeed() * delta));
        } else if(GameApp.isKeyPressed(Input.Keys.DOWN) || GameApp.isKeyPressed(Input.Keys.S) ) {
            player.position.setY(player.position.getY() - Math.round(player.getMovementSpeed() * delta));
        }
    }
}
