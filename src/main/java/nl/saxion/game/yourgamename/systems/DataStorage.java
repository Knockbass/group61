package nl.saxion.game.yourgamename.systems;

import nl.saxion.game.yourgamename.entities.Player;

public class DataStorage {
    public Player player;
    public NPCSystem npcSystem;

    public DataStorage(){}

    public DataStorage(Player player, NPCSystem npcSystem){
        this.player = player;
        this.npcSystem = npcSystem;
    }
}
