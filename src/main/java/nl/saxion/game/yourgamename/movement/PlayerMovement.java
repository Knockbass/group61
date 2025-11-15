package nl.saxion.game.yourgamename.movement;

import com.badlogic.gdx.Input;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.gameapp.GameApp;

public class PlayerMovement{

    public static void checkMovementKeyPressed(Player player, float delta){
        if (GameApp.isKeyPressed(Input.Keys.RIGHT) || GameApp.isKeyPressed(Input.Keys.D)){
            player.position.x = player.position.x + Math.round(player.getMovementSpeed() * delta);  //by multiplying on delta the movement speed of player is same when different fps
        } else if(GameApp.isKeyPressed(Input.Keys.LEFT) || GameApp.isKeyPressed(Input.Keys.A) ){
            player.position.x = player.position.x - Math.round(player.getMovementSpeed() * delta);
        }

        if (GameApp.isKeyPressed(Input.Keys.UP) || GameApp.isKeyPressed(Input.Keys.W)  ){
            player.position.y = player.position.y + Math.round(player.getMovementSpeed() * delta);
        } else if(GameApp.isKeyPressed(Input.Keys.DOWN) || GameApp.isKeyPressed(Input.Keys.S) ) {
            player.position.y = player.position.y - Math.round(player.getMovementSpeed() * delta);
        }
    }

    public static void setPositionBorder(Player player, int worldWidth, int worldHeight){
        player.position.x = (int) GameApp.clamp(player.position.x, 0, worldWidth - 150); // 150 is the size of a player entity
        player.position.y = (int) GameApp.clamp(player.position.y, 0, worldHeight - 150);// 150 has to be changed later
    }
}
