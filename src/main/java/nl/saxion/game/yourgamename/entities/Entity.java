package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.movement.Vector2;

public abstract class Entity {
    public int entityWidth;
    public int entityHeight;
    protected String name;
    public Vector2 position;



    public Entity(){
        name = "Nameless";
    };
    public Entity(String name){
        this.name = name;
    }
}

