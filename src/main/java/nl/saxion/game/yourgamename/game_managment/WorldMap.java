package nl.saxion.game.yourgamename.game_managment;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.IntArray;

public class WorldMap {
    public enum MapType{
        OPEN_WORLD,
        UNI_FLOOR1,
        UNI_FLOOR2
    }

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private MapType mapType;
    private int[] allTileLayerIndices;

    public WorldMap(String mapPath) {
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1f);
        mapType = MapType.OPEN_WORLD;
        refreshTileLayerIndices();
    }

    private final int[][] layersBelowPlayer = {
            {0, 1, 2, 3, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15}, // open world
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},               // first floor (legacy indices)
            {}                                                // second floor (legacy indices)
    };

    private final int[][] layersAbovePlayer = {
            { 4, 8, 16, 17 },  // open world
            {               }, // first floor (no layers above)
            {               }  // second floor (no layers above)
    };

    public void dispose() {
        map.dispose();
        renderer.dispose();
    }

    public MapLayer getObjectLayer(String layerName) {
        return map.getLayers().get(layerName);
    }


    public int[] getLayersBelowPlayer() {   // 0 for open world
        // 1 for first floor
        // 2 for second floor
        return switch (mapType) {
            case OPEN_WORLD -> layersBelowPlayer[0];
            case UNI_FLOOR1 -> allTileLayerIndices; // render all tile layers for interior floors
            case UNI_FLOOR2 -> allTileLayerIndices; // render all tile layers for interior floors
        };
    }

    public int[] getLayersAbovePlayer() {    // 0 for open world
        // 1 for first floor
        // 2 for second floor
        return switch (mapType) {
            case OPEN_WORLD -> layersAbovePlayer[0];
            case UNI_FLOOR1 -> layersAbovePlayer[1];
            case UNI_FLOOR2 -> layersAbovePlayer[2];
            };
    }

    public TiledMap getMap() {
        return this.map;
    }

    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }

    public void setMap(String mapPath) {
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1f);
        refreshTileLayerIndices();
    }

    public void setMapType(MapType mapType) {
        this.mapType = mapType;
    }

    private void refreshTileLayerIndices() {
        IntArray indices = new IntArray();
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            MapLayer layer = map.getLayers().get(i);
            if (layer instanceof TiledMapTileLayer) {
                indices.add(i);
            }
        }
        allTileLayerIndices = indices.toArray();
    }
}
