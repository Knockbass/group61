package nl.saxion.game.yourgamename.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.collision.CollisionManager;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.game_managment.Quest;

import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    public Player player;
    public NPCSystem npcSystem;

    public DataStorage(){}

    public DataStorage(Player player, NPCSystem npcSystem){
        this.player = player;
        this.npcSystem = npcSystem;
    }
}
