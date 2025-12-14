package nl.saxion.game.yourgamename.game_managment;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.GameScreen;

public abstract class BaseGameScreen extends GameScreen {
    protected OrthographicCamera camera;
    protected Viewport viewport;
    private final float worldWidth, worldHeight;
    private float targetX, targetY;

    public BaseGameScreen(float viewportWidth, float viewportHeight,
                          float worldWidth, float worldHeight){
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(viewportWidth,viewportHeight, camera);
    }


    public void setCameraTarget(float x, float y) {
        this.targetX = x;
        this.targetY = y;
    }

    public void setCameraTargetInstantly(float x, float y) {
        this.targetX = x;
        this.targetY = y;
        snapCameraToTarget();
    }


    private void updateCamera(float delta) {
        float camX = camera.position.x;
        float camY = camera.position.y;

        // Smoothly interpolate to target
        camX += (targetX - camX);
        camY += (targetY - camY);

        // Clamp to world bounds
        float halfViewportWidth = viewport.getWorldWidth() / 2f * camera.zoom;
        float halfViewportHeight = viewport.getWorldHeight() / 2f * camera.zoom;

        camX = MathUtils.clamp(camX, halfViewportWidth, worldWidth - halfViewportWidth);
        camY = MathUtils.clamp(camY, halfViewportHeight, worldHeight - halfViewportHeight);

        camera.position.set(camX, camY, 0);

        camera.update();
    }

    @Override
    public void show(){

    }

    @Override
    public void render(float delta) {
        updateCamera(delta);


        viewport.apply();
        GameApp.getSpriteBatch().setProjectionMatrix(camera.combined);
        GameApp.getShapeRenderer().setProjectionMatrix(camera.combined);
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }


    public OrthographicCamera getCamera(){
        return this.camera;
    }

    private void snapCameraToTarget() {
        float halfViewportWidth = (viewport.getWorldWidth() / 2f) * camera.zoom;
        float halfViewportHeight = (viewport.getWorldHeight() / 2f) * camera.zoom;

        float camX = MathUtils.clamp(targetX, halfViewportWidth, worldWidth - halfViewportWidth);
        float camY = MathUtils.clamp(targetY, halfViewportHeight, worldHeight - halfViewportHeight);

        camera.position.set(camX, camY, 0);
        camera.update();
    }

    public float getViewportLeft() {
        return camera.position.x - (viewport.getWorldWidth() / 2f) * camera.zoom;
    }


    public float getViewportRight() {
        return camera.position.x + (viewport.getWorldWidth() / 2f) * camera.zoom;
    }


    public float getViewportBottom() {
        return camera.position.y - (viewport.getWorldHeight() / 2f) * camera.zoom;
    }


    public float getViewportTop() {
        return camera.position.y + (viewport.getWorldHeight() / 2f) * camera.zoom;
    }

    public float getHUDWidth() {
        return viewport.getWorldWidth();
    }

    public float getHUDHeight() {
        return viewport.getWorldHeight();
    }

    public float getScreenWidth() {
        return com.badlogic.gdx.Gdx.graphics.getWidth();
    }

    public float getScreenHeight() {
        return com.badlogic.gdx.Gdx.graphics.getHeight();
    }
}
