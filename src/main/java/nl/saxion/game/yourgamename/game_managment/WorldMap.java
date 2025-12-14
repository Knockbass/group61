package nl.saxion.game.yourgamename.game_managment;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class WorldMap {

    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    public WorldMap(String mapPath) {
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1f);
    }

    private final int[] layersBelowPlayer = {
            0, // grass
            1, // water
            2, // island_ground
            4, // walls
            5, // road
            6, // grass_decor
            8, // floor
            9,  // uni_borders
            10,  // buildings
            11 // decorations
    };

    private final int[] layersAbovePlayer = {
            3,  // trees
            7,  // trees_overlap
            12, // roofs
            13  // shadows
    };

    public void dispose() {
        map.dispose();
        renderer.dispose();
    }

    public MapLayer getObjectLayer(String layerName){
        MapLayer collisionLayer;
        return collisionLayer = map.getLayers().get(layerName);
    }


    public int[] getLayersBelowPlayer(){
        return layersBelowPlayer;
    }

    public int[] getLayersAbovePlayer(){
        return layersAbovePlayer;
    }

    public TiledMap getMap(){
        return this.map;
    }

    public OrthogonalTiledMapRenderer getRenderer(){
        return renderer;
    }
}
