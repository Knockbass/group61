package nl.saxion.game.yourgamename.screens;

import com.badlogic.gdx.Input;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;

public class MainMenuScreen extends ScalableGameScreen {
    public MainMenuScreen() {
        super(1920, 1080);
    }
    boolean fadingIn = true;
    boolean fadingOut = true;

    boolean inputLocked = false;

    boolean changeScreens = false;

    boolean choiceMade = false;
    int whichChoice = 0;

    final int animStart1 = 100;
    final int animEnd1 = 150;
    final int animStart2 = 500;
    final int animEnd2 = 460;
    final int animArrow = 100;

    boolean canPlayStart = false;
    boolean canPlaySleep = false;

    // Get world size
    float width = getWorldWidth();
    float height = getWorldHeight();

    final float textPos1 = width - 450;
    final float textPos2 = height - 250;
    final float textPos3 = height / 2 - 100;
    final float textPos4 = height / 2 - 300;

    float limitButtonStartX1 = width / 2 + 75;
    float limitButtonStartX2 = width / 2 + 735;
    float limitButtonStartY1 = height / 2 - 140;
    float limitButtonStartY2 = height / 2 - 70;
    float limitButtonSleepX1 = width / 2 + 120;
    float limitButtonSleepX2 = width / 2 + 690;
    float limitButtonSleepY1 = height / 2 - 340;
    float limitButtonSleepY2 = height / 2 - 270;

    // Variable for fullscreen
    boolean fullScreen = false;

    @Override
    public void show() {
        GameApp.enableTransparency();
        GameApp.addFont("pixel", "fonts/Jersey10-Regular.ttf", 200);
        GameApp.addFont("pixel2", "fonts/Jersey10-Regular.ttf", 100);
        GameApp.addTexture("Background", "textures/bg3.png");
        GameApp.addMusic("BGMusic1", "audio/morning-traffic-60458.mp3");
        GameApp.addMusic("BGMusic2", "audio/urban-morning-birds-4-am-soundscape-366500.mp3");
        GameApp.addSound("blimp", "audio/blimp.mp3");
        GameApp.addSound("choice", "audio/choice.mp3");
        GameApp.addInterpolator("fadeIn1", height + animStart1, height - animEnd1, 1.5f, "fade");
        GameApp.addInterpolator("fadeIn2", width + animStart2, width - animEnd2, 5f, "fade");
        GameApp.addInterpolator("fadeIn3", width + animStart2, width - animEnd2, 6f, "fade");
        GameApp.addInterpolator("fadeIn4", width + animStart2, width - animEnd2, 7f, "fade");
        GameApp.addInterpolator("fadeOut1", height - animEnd1, height + animStart1, 1.5f, "fade");
        GameApp.addInterpolator("fadeOut2", width - animEnd2, width + animStart2, 3f, "fade");
        GameApp.addInterpolator("fadeOut3", width - animEnd2, width + animStart2, 4f, "fade");
        GameApp.addInterpolator("fadeOut4", width - animEnd2, width + animStart2, 5f, "fade");
        GameApp.addInterpolator("arrow", width + animStart2, width - animArrow, 1f, "fade");
        GameApp.addSpriteSheet("sleepingCharacter", "textures/testanim.png", 1317, 816);
        GameApp.addAnimationFromSpritesheet("sleepingAnim", "sleepingCharacter", 0.7f, true);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        float textSurvive = GameApp.updateInterpolator("fadeIn1");
        float textTheSemester = GameApp.updateInterpolator("fadeIn2");
        float textStartYJ = GameApp.updateInterpolator("fadeIn3");
        float textGoToSleep = GameApp.updateInterpolator("fadeIn4");
        float textSurviveOut = GameApp.updateInterpolator("fadeOut1");
        float textTheSemesterOut = GameApp.updateInterpolator("fadeOut2");
        float textStartYJOut = GameApp.updateInterpolator("fadeOut3");
        float textGoToSleepOut = GameApp.updateInterpolator("fadeOut4");
        float textArrow = GameApp.updateInterpolator("arrow");

        // Get mouse position
        float mouseX = getMouseX();
        float mouseY = getMouseY();

        // Render the main menu
        GameApp.clearScreen("black");
        GameApp.playMusic("BGMusic1", true);
        GameApp.playMusic("BGMusic2", true);

        if (GameApp.isKeyJustPressed(Input.Keys.F11) && !fullScreen) {
            GameApp.switchToFullscreen();
            fullScreen = true;
        } else if (GameApp.isKeyJustPressed(Input.Keys.F11) && fullScreen) {
            GameApp.switchToWindowedMode(1920, 1080);
            fullScreen = false;
        }

        if (!choiceMade) {
            GameApp.startSpriteRendering();
            GameApp.drawTexture("Background", 0, 0, width, height);
            GameApp.updateAnimation("sleepingAnim");
            GameApp.drawAnimation("sleepingAnim", 0, +110, 900, 500);
            GameApp.drawTextCentered("pixel", "SURVIVE", textPos1, textSurvive, "orange-100");
            GameApp.drawTextCentered("pixel2", "the SEMESTER", textTheSemester, textPos2, "orange-100");
            GameApp.drawTextCentered("pixel2", "Start your journey", textStartYJ, textPos3, "orange-100");
            GameApp.drawTextCentered("pixel2", "Go back to sleep", textGoToSleep, textPos4, "orange-100");
            GameApp.endSpriteRendering();
        } else {
            GameApp.startSpriteRendering();
            GameApp.drawTexture("Background", 0, 0, width, height);
            GameApp.drawTextCentered("pixel2", "Go back to sleep", textGoToSleepOut, textPos4, "orange-100");
            GameApp.drawTextCentered("pixel2", "Start your journey", textStartYJOut, textPos3, "orange-100");
            GameApp.drawTextCentered("pixel2", "the SEMESTER", textTheSemesterOut, textPos2, "orange-100");
            GameApp.drawTextCentered("pixel", "SURVIVE", textPos1, textSurviveOut, "orange-100");
            GameApp.endSpriteRendering();
            if (whichChoice == 1) {
                if (GameApp.isInterpolatorFinished("fadeOut4")) {
                    GameApp.clearScreen();
                    GameApp.switchScreen("YourGameScreen");
                    changeScreens = true;
                    choiceMade = false;
                    whichChoice = 0;
                    inputLocked = false;
                }
            }
            if (whichChoice == 2) {
                GameApp.updateAnimation("sleepingAnim");
                GameApp.drawAnimation("sleepingAnim", 0, +110, 900, 500);
                if (GameApp.isInterpolatorFinished("fadeOut4")) {
                    GameApp.quit();
                }
            }
        }

        // Check if mouse is inside Start option
        boolean hoverStart = mouseX >= limitButtonStartX1 && mouseX <= limitButtonStartX2 &&
                mouseY >= limitButtonStartY1 && mouseY <= limitButtonStartY2;

        // Check if mouse is inside Sleep option
        boolean hoverSleep = mouseX >= limitButtonSleepX1 && mouseX <= limitButtonSleepX2 &&
                mouseY >= limitButtonSleepY1 && mouseY <= limitButtonSleepY2;

        // --- Start option ---
        if (!changeScreens) {
            if (hoverStart && whichChoice == 0) {
                if (GameApp.isInterpolatorFinished("fadeIn4")) {
                    GameApp.startSpriteRendering();
                    GameApp.drawTextCentered("pixel2", "<", textArrow, textPos3, "orange-100");
                    GameApp.endSpriteRendering();
                    if (canPlayStart) {
                        GameApp.playSound("blimp", 0.7f);
                        canPlayStart = false; // lock until cursor leaves
                    }
                    if (!inputLocked && GameApp.isButtonJustPressed(Input.Buttons.LEFT)) {
                        GameApp.resetInterpolator("fadeIn4");
                        GameApp.resetInterpolator("fadeOut1");
                        GameApp.resetInterpolator("fadeOut2");
                        GameApp.resetInterpolator("fadeOut3");
                        GameApp.resetInterpolator("fadeOut4");
                        choiceMade = true;
                        inputLocked = true;
                        GameApp.playSound("choice");
                        whichChoice = 1;
                    }
                }
            } else {
                canPlayStart = true; // reset when cursor leaves
            }

            // --- Sleep option ---
            if (hoverSleep) {
                if (GameApp.isInterpolatorFinished("fadeIn4")) {
                    GameApp.startSpriteRendering();
                    GameApp.drawTextCentered("pixel2", "<", textArrow, textPos4, "orange-100");
                    GameApp.endSpriteRendering();
                    if (canPlaySleep) {
                        GameApp.playSound("blimp", 0.7f);
                        canPlaySleep = false;
                    }
                    if (!inputLocked && GameApp.isButtonJustPressed(Input.Buttons.LEFT)) {
                        GameApp.resetInterpolator("fadeIn4");
                        GameApp.resetInterpolator("fadeOut1");
                        GameApp.resetInterpolator("fadeOut2");
                        GameApp.resetInterpolator("fadeOut3");
                        GameApp.resetInterpolator("fadeOut4");
                        choiceMade = true;
                        inputLocked = true;
                        GameApp.playSound("choice");
                        whichChoice = 2;
                    }
                }
            } else {
                canPlaySleep = true;
            }

        } else {
            changeScreens = false;
        }
    }

    @Override
    public void hide() {
        GameApp.stopAllAudio();
        GameApp.disposeInterpolator("fadeIn1");
        GameApp.disposeInterpolator("fadeIn2");
        GameApp.disposeInterpolator("fadeIn3");
        GameApp.disposeInterpolator("fadeIn4");
        GameApp.disposeInterpolator("fadeOut1");
        GameApp.disposeInterpolator("fadeOut2");
        GameApp.disposeInterpolator("fadeOut3");
        GameApp.disposeInterpolator("fadeOut4");
        GameApp.disposeInterpolator("arrow");
        GameApp.disposeSpritesheet("sleepingCharacter");
        GameApp.disposeFont("pixel");
        GameApp.disposeFont("pixel2");
        GameApp.disposeTexture("Background");
        GameApp.disposeMusic("BGMusic1");
        GameApp.disposeMusic("BGMusic2");
        GameApp.disposeSound("blimp");
        GameApp.disposeSound("choice");
    }
}

