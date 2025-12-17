package nl.saxion.game.yourgamename.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import nl.saxion.game.yourgamename.collision.Collidable;
import nl.saxion.game.yourgamename.entities.*;
import nl.saxion.game.yourgamename.entities.Box;
import nl.saxion.game.yourgamename.game_managment.Quest;
import nl.saxion.game.yourgamename.game_managment.Stat;
import nl.saxion.game.yourgamename.movement.Vector2;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SaveLoadSystem {
    DataStorage dataStorage;

    public SaveLoadSystem(DataStorage dataStorage){
        this.dataStorage = dataStorage;
    }

    public void saveGame(){
        saveToBinary(dataStorage,"gamesave.bin");
    }


    public DataStorage loadGame(){
        dataStorage = (DataStorage) loadFromBinary("gamesave.bin");
        return dataStorage;
    }

    private void saveToBinary(Object object, String fileName){
        Kryo kryo = registerKryo();

        FileHandle file = Gdx.files.local(fileName);
        Output output = new Output(file.write(false));
        kryo.writeClassAndObject(output, object);

        output.close();
    }

    private Object loadFromBinary(String fileName) {
        Kryo kryo = registerKryo();
        FileHandle file = Gdx.files.local(fileName);
        if (!file.exists()) {
            throw new IllegalArgumentException(fileName + " does not exist.");
        }

        // Read file and return object.
        try (Input input = new Input(file.read())) {
            return kryo.readClassAndObject(input);
        }
    }

    private Kryo registerKryo(){
        Kryo kryo = new Kryo();

        kryo.register(Player.class);
        kryo.register(Rectangle.class);
        kryo.register(NPCSystem.class);
        kryo.register(Direction.class);
        kryo.register(Vector2.class);
        kryo.register(StatSystem.class);
        kryo.register(Stat.class);
        kryo.register(ArrayList.class);
        kryo.register(NPC.class);
        kryo.register(Quest.class);
        kryo.register(Quest.QuestObjective.class);
        kryo.register(Quest.Reward.class);
        kryo.register(Quest.QuestState.class);
        kryo.register(Quest.QuestObjective.ObjectiveType.class);
        kryo.register(com.badlogic.gdx.math.Rectangle.class);
        kryo.register(DataStorage.class);
        kryo.register(List.class);
        kryo.register(Box.class);
        kryo.register(Collidable.class);
        kryo.register(Yapper.class);

        return kryo;
    }
}
