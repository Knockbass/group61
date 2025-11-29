package nl.saxion.game.yourgamename.game_managment;

import nl.saxion.game.yourgamename.collision.Collidable;
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
    private static final int MAX_ENEMIES = 3; // Maximum number of enemies
    private static final int SPAWN_DISTANCE_FROM_PLAYER = 400; // Minimum distance from player to spawn

    private float spawnTimer = 0.0f;
    private List<Yapper> enemies = new ArrayList<>();
    private Random random = new Random();
    private int worldWidth;
    private int worldHeight;

    public EnemySpawner(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void update(float delta, Player player) {
        spawnTimer += delta;

        // Calculate spawn interval based on player's mental health
        float spawnInterval = calculateSpawnInterval(player);

        // Spawn new enemy if interval passed and we haven't reached max enemies
        if (spawnTimer >= spawnInterval && enemies.size() < MAX_ENEMIES) {
            spawnEnemy(player);
            spawnTimer = 0.0f;
        }
    }

    private float calculateSpawnInterval(Player player) {
        int mentalHealth = player.getPlayerStats().getMentalHealth();

        // Lower mental health = faster spawning (more stress = more enemies)
        // Higher mental health = slower spawning (less stress = fewer enemies)

        // Mental health ranges from 0-100
        // Map: 0 mental health = MIN_SPAWN_INTERVAL, 100 mental health = MAX_SPAWN_INTERVAL
        float normalizedHealth = mentalHealth / 100.0f; // 0.0 to 1.0

        // Invert so low health = fast spawn, high health = slow spawn
        float invertedHealth = 1.0f - normalizedHealth;

        // Calculate spawn interval: low health (0) = MIN, high health (100) = MAX
        float spawnInterval = MIN_SPAWN_INTERVAL + (invertedHealth * (MAX_SPAWN_INTERVAL - MIN_SPAWN_INTERVAL));

        return spawnInterval;
    }

    private void spawnEnemy(Player player) {
        // Find a valid spawn position away from player
        int spawnX, spawnY;
        int attempts = 0;
        int maxAttempts = 20;

        do {
            spawnX = random.nextInt(worldWidth - 100); // Account for enemy width
            spawnY = random.nextInt(worldHeight - 100); // Account for enemy height
            attempts++;
        } while (isTooCloseToPlayer(spawnX, spawnY, player) && attempts < maxAttempts);

        // Create new enemy
        Yapper enemy = new Yapper("enemy", 100, 10, 1);
        enemy.setX(spawnX);
        enemy.setY(spawnY);

        enemies.add(enemy);
        CollisionManager.addEntity(enemy);
    }

    private boolean isTooCloseToPlayer(int x, int y, Player player) {
        int dx = x - player.getX();
        int dy = y - player.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < SPAWN_DISTANCE_FROM_PLAYER;
    }

    public List<Yapper> getEnemies() {
        return enemies;
    }

    public void deleteDeadEnemies() {
        Iterator<Yapper> iterator = enemies.iterator();
        while (iterator.hasNext()){
            Yapper yapper = iterator.next();

            if(yapper.isDead){
                CollisionManager.dynamic.remove(yapper);
                CollisionManager.collidableEntities.remove(yapper);

                iterator.remove();
            }
        }


    }
}

