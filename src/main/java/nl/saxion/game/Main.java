package nl.saxion.game;

import nl.saxion.game.yourgamename.game_managment.YourGameScreen;
import nl.saxion.game.yourgamename.menu.MainMenuScreen;
import nl.saxion.gameapp.GameApp;

public class Main {
    public static void main(String[] args) {
        // Add screens
        GameApp.addScreen("MainMenuScreen", new MainMenuScreen());
        GameApp.addScreen("YourGameScreen", new YourGameScreen());

        // Start game loop and show main menu screen
        GameApp.start("Survive the Semester", 1920, 1080, 60, false, "MainMenuScreen");
    }
}
