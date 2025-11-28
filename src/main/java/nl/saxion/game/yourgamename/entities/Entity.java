package nl.saxion.game.yourgamename.entities;

import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.movement.Vector2;

public abstract class Entity {
    protected String name;
    protected boolean isPushable;

    public Entity(){
        name = "Nameless";
    };
    public Entity(String name){
        this.name = name;
    }
}

