package nl.saxion.game.yourgamename.game_managment;

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

    private static final int MAX_ENEMIES = 3; // Maximum number of enemies alive at once
    private static final int MIN_DISTANCE_FROM_PLAYER = 200; // Minimum spawn distance from player

    private float spawnTimer = 0.0f;
    private List<Yapper> enemies = new ArrayList<>();
    private Random random = new Random();

    private int worldWidth;
    private int worldHeight;


    public EnemySpawner(float worldWidth, float worldHeight) {
        this.worldWidth = (int) worldWidth;
        this.worldHeight = (int) worldHeight;
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

    // Spawns a new enemy within the viewport, respecting world boundaries

    private void spawnEnemy(Player player, float vL, float vR, float vT, float vB) {
        int spawnX, spawnY;
        int attempts = 0;
        int maxAttempts = 20;
        int enemySize = 100; // Enemy width and height

        // Clamp viewport boundaries to world boundaries
        // This prevents spawning outside the world when player is near edges
        float clampedVL = Math.max(vL, 0);
        float clampedVR = Math.min(vR, worldWidth);
        float clampedVT = Math.min(vT, worldHeight);
        float clampedVB = Math.max(vB, 0);

        // Adjust for enemy size to prevent spawning partially outside world
        float spawnableWidth = clampedVR - clampedVL - enemySize;
        float spawnableHeight = clampedVT - clampedVB - enemySize;

        if (spawnableWidth <= 0 || spawnableHeight <= 0) {
            spawnEnemyInWorld(player);
            return;
        }

        // Try to find a valid spawn position within viewport
        do {
            spawnX = (int) (clampedVL + random.nextFloat() * spawnableWidth);
            spawnY = (int) (clampedVB + random.nextFloat() * spawnableHeight);
            attempts++;
        } while (isTooCloseToPlayer(spawnX, spawnY, player) && attempts < maxAttempts);

        // If failed to find a good position in viewport, spawn just outside it
        if (attempts >= maxAttempts) {
            spawnEnemyOffscreen(player, clampedVL, clampedVR, clampedVT, clampedVB);
            return;
        }

        createEnemy(spawnX, spawnY);
    }

    //Spawns enemy anywhere in the world, away from player

    private void spawnEnemyInWorld(Player player) {
        int spawnX, spawnY;
        int attempts = 0;
        int maxAttempts = 20;
        int enemySize = 100;

        do {
            spawnX = random.nextInt(worldWidth - enemySize);
            spawnY = random.nextInt(worldHeight - enemySize);
            attempts++;
        } while (isTooCloseToPlayer(spawnX, spawnY, player) && attempts < maxAttempts);

        createEnemy(spawnX, spawnY);
    }

    //Spawns enemy just outside the visible viewport

    private void spawnEnemyOffscreen(Player player, float vL, float vR, float vT, float vB) {
        int spawnX, spawnY;
        int margin = 150; // Distance outside viewport
        int enemySize = 100;

        // Choose random side (0=left, 1=right, 2=top, 3=bottom)
        int side = random.nextInt(4);

        switch (side) {
            case 0: // Left side
                spawnX = Math.max(0, (int) (vL - margin));
                spawnY = (int) (vB + random.nextFloat() * (vT - vB - enemySize));
                break;
            case 1: // Right side
                spawnX = Math.min(worldWidth - enemySize, (int) (vR + margin));
                spawnY = (int) (vB + random.nextFloat() * (vT - vB - enemySize));
                break;
            case 2: // Top side
                spawnX = (int) (vL + random.nextFloat() * (vR - vL - enemySize));
                spawnY = Math.min(worldHeight - enemySize, (int) (vT + margin));
                break;
            case 3: // Bottom side
                spawnX = (int) (vL + random.nextFloat() * (vR - vL - enemySize));
                spawnY = Math.max(0, (int) (vB - margin));
                break;
            default:
                spawnX = (int) vL;
                spawnY = (int) vB;
        }

        // Ensure spawn position is within world boundaries
        spawnX = Math.max(0, Math.min(worldWidth - enemySize, spawnX));
        spawnY = Math.max(0, Math.min(worldHeight - enemySize, spawnY));

        createEnemy(spawnX, spawnY);
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