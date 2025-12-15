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
            3, // island_decoration
            5, // walls
            6, // road
            7, // grass_decor
            9, // floor
            10,  // uni_borders
            11,  // buildings
            12, // decorations
            13, // ships
            14, // ships_flag
            15 // market_area
    };

    private final int[] layersAbovePlayer = {
            4,  // trees
            8,  // trees_overlap
            16, // roofs
            17  // shadows
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
