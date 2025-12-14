package nl.saxion.game.yourgamename.game_managment;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import nl.saxion.game.yourgamename.collision.CollisionManager;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.entities.Yapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EnemySpawner {
    private static final float MIN_SPAWN_INTERVAL = 0.5f; // Fastest spawn rate (low mental health)
    private static final float MAX_SPAWN_INTERVAL = 8.0f; // Slowest spawn rate (high mental health)

    private static final int MAX_ENEMIES = 2; // Maximum number of enemies alive at once
    private static final int MIN_DISTANCE_FROM_PLAYER = 100; // Minimum spawn distance from player (reduced for easier spawning)

    private float spawnTimer = 0.0f;
    private List<Yapper> enemies = new ArrayList<>();
    private List<Rectangle> spawnAreas = new ArrayList<>(); // YapperSpawn areas from map
    private Random random = new Random();

    private int worldWidth;
    private int worldHeight;


    public EnemySpawner(float worldWidth, float worldHeight) {
        this.worldWidth = (int) worldWidth;
        this.worldHeight = (int) worldHeight;
    }

    public void loadSpawnAreasFromMap(MapObjects mapObjects) {
        spawnAreas.clear();
        System.out.println("Loading spawn areas from " + mapObjects.getCount() + " map objects...");
        
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof RectangleMapObject rectObject) {
                String name = mapObject.getName();
                System.out.println("Found map object: name='" + name + "', type=" + mapObject.getClass().getSimpleName());
                
                // Load objects that start with "YapperSpawn" (case-insensitive)
                if (name != null && (name.startsWith("YapperSpawn") || name.startsWith("yapperspawn") || name.equalsIgnoreCase("YapperSpawn"))) {
                    Rectangle rect = rectObject.getRectangle();
                    spawnAreas.add(new Rectangle(rect.x, rect.y, rect.width, rect.height));
                    System.out.println("✓ Loaded YapperSpawn area: " + name + " at (" + rect.x + ", " + rect.y + ") size " + rect.width + "x" + rect.height);
                }
            } else {
                String name = mapObject.getName();
                System.out.println("Skipped map object (not RectangleMapObject): name='" + name + "', type=" + mapObject.getClass().getSimpleName());
            }
        }
        System.out.println("Total YapperSpawn areas loaded: " + spawnAreas.size());
    }

    //Updates the spawner logic, spawning enemies based on player's mental health

    public void update(float delta, Player player, float vL, float vR, float vT, float vB) {
        spawnTimer += delta;

        float spawnInterval = calculateSpawnInterval(player);

        // Spawn new enemy if interval passed and we haven't reached max enemies
        if (spawnTimer >= spawnInterval && enemies.size() < MAX_ENEMIES) {
            spawnEnemy(player, vL, vR, vT, vB);
            spawnTimer = 0.0f;
        }

        removeEnemyOutsideOfViewport(vL, vR, vT, vB);
    }

    private float calculateSpawnInterval(Player player) {
        int mentalHealth = player.accessStatSystem().getMentalHealth();

        // Mental health ranges from 0-100
        // Map: 0 mental health = MIN_SPAWN_INTERVAL, 100 mental health = MAX_SPAWN_INTERVAL
        float normalizedHealth = mentalHealth / 100.0f; // 0.0 to 1.0

        // Invert so low health = fast spawn, high health = slow spawn
        float invertedHealth = 1.0f - normalizedHealth;

        // Calculate spawn interval: low health (0) = MIN, high health (100) = MAX
        return MIN_SPAWN_INTERVAL + (invertedHealth * (MAX_SPAWN_INTERVAL - MIN_SPAWN_INTERVAL));
    }

    // Spawns a new enemy within YapperSpawn areas

    private void spawnEnemy(Player player, float vL, float vR, float vT, float vB) {
        // If no spawn areas defined, don't spawn
        if (spawnAreas.isEmpty()) {
            System.out.println("No YapperSpawn areas found - cannot spawn enemies");
            return;
        }

        int spawnX = 0;
        int spawnY = 0;
        int attempts = 0;
        int maxAttempts = 100; // Increased attempts
        int enemyWidth = 24; // Same as player width
        int enemyHeight = 32; // Same as player height
        boolean foundPosition = false;

        // Try to find a valid spawn position within a YapperSpawn area
        while (attempts < maxAttempts && !foundPosition) {
            // Pick a random spawn area
            Rectangle spawnArea = spawnAreas.get(random.nextInt(spawnAreas.size()));
            
            // Calculate spawnable area within the spawn zone (accounting for enemy size)
            float spawnableWidth = Math.max(0, spawnArea.width - enemyWidth);
            float spawnableHeight = Math.max(0, spawnArea.height - enemyHeight);
            
            // If area is too small (less than enemy size), skip it
            if (spawnArea.width < enemyWidth || spawnArea.height < enemyHeight) {
                attempts++;
                continue;
            }
            
            // Random position within the spawn area (clamp to ensure enemy fits)
            if (spawnableWidth > 0) {
                spawnX = (int) (spawnArea.x + random.nextFloat() * spawnableWidth);
            } else {
                spawnX = (int) spawnArea.x; // Center if area is exactly enemy size
            }
            
            if (spawnableHeight > 0) {
                spawnY = (int) (spawnArea.y + random.nextFloat() * spawnableHeight);
            } else {
                spawnY = (int) spawnArea.y; // Center if area is exactly enemy size
            }
            
            // Check if position is valid (not too close to player)
            if (!isTooCloseToPlayer(spawnX, spawnY, player)) {
                foundPosition = true;
            }
            attempts++;
        }

        // If found a valid position, spawn the enemy
        if (foundPosition) {
            System.out.println("Spawning Yapper at (" + spawnX + ", " + spawnY + ")");
            createEnemy(spawnX, spawnY);
        } else {
            System.out.println("Failed to find valid spawn position after " + maxAttempts + " attempts");
        }
    }


    //Creates a new enemy at the specified position

    private void createEnemy(int x, int y) {
        Yapper enemy = new Yapper("enemy", 100, 10, 1);
        enemy.setX(x);
        enemy.setY(y);

        enemies.add(enemy);
        CollisionManager.addEntity(enemy);
    }

    // Checks if spawn position is too close to player

    private boolean isTooCloseToPlayer(int x, int y, Player player) {
        int dx = x - player.getX();
        int dy = y - player.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < MIN_DISTANCE_FROM_PLAYER;
    }

    public List<Yapper> getEnemies() {
        return enemies;
    }

    public void deleteDeadEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Yapper yapper = enemies.get(i);
            if (yapper.isDead) {
                enemies.remove(i);

                // Remove from collision system
                CollisionManager.collidableEntities.remove(yapper);
                CollisionManager.dynamic.remove(yapper);
            }
        }
    }

    public void removeEnemyOutsideOfViewport(float vL, float vR, float vT, float vB) {
        int margin = 150; //Extra distance outside of viewport

        Iterator<Yapper> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Yapper yapper = iterator.next();

            if (yapper.getX() + yapper.getWidth() + margin < vL || yapper.getX() - margin > vR ||
                    yapper.getY() + yapper.getHeight() + margin < vB || yapper.getY() - margin > vT) {

                CollisionManager.collidableEntities.remove(yapper);
                CollisionManager.dynamic.remove(yapper);
                iterator.remove();
            }
        }
    }
}