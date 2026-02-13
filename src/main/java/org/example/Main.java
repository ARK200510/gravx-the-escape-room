package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int WIDTH = 960;
    public static final int HEIGHT = 384;

    private Stage primaryStage;
    private boolean splashActive = false; // No splash needed

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        stage.setTitle("GravX: The Escape Room");
        stage.setResizable(false);

        // Start DIRECTLY with Home Screen (typing animation wala)
        showHomeScreen();

        stage.show();
    }

    // Show Home Screen (First screen with typing animation)
    public void showHomeScreen() {
        HomeScreen homeScreen = new HomeScreen(this);
        Scene scene = new Scene(homeScreen.getRoot(), WIDTH, HEIGHT);
        primaryStage.setScene(scene);
    }

    // Show Level Select (DIRECTLY after HomeScreen)
    public void showLevelSelect() {
        splashActive = false;
        LevelSelect levelSelect = new LevelSelect(this);
        Scene scene = new Scene(levelSelect.getRoot(), WIDTH, HEIGHT);
        primaryStage.setScene(scene);
    }

    // Show Menu Scene (Agar kahin aur use ho toh)
    public void showMenuScene() {
        splashActive = false;
        MenuScene menu = new MenuScene(this);
        Scene scene = new Scene(menu.getRoot(), WIDTH, HEIGHT);
        primaryStage.setScene(scene);
    }

    // Show Level One (for menu/level select compatibility)
    public void showLevelOne() {
        loadLevel1();
    }

    // Load Level 1
    public void loadLevel1() {
        splashActive = false;
        LevelOne level1 = new LevelOne(this);
        Scene scene = new Scene(level1.getRoot(), WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        level1.getRoot().requestFocus();
    }

    // Load Level 2
    public void loadLevel2() {
        LevelTwo level2 = new LevelTwo(this);
        Scene scene = new Scene(level2.getRoot(), WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        level2.getRoot().requestFocus();
    }

    // Load Level 3
    public void loadLevel3() {
        LevelThree level3 = new LevelThree(this);
        Scene scene = new Scene(level3.getRoot(), WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        level3.getRoot().requestFocus();
    }

    // Load Level 4
    public void loadLevel4() {
        LevelFour level4 = new LevelFour(this);
        Scene scene = new Scene(level4.getRoot(), WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        level4.getRoot().requestFocus();
    }

    // Load Level 5
    public void loadLevel5() {
        LevelFive level5 = new LevelFive(this);
        Scene scene = new Scene(level5.getRoot(), WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        level5.getRoot().requestFocus();
    }

    // Check if splash is active
    public boolean isSplashActive() {
        return splashActive;
    }

    public static void main(String[] args) {
        launch(args);
    }
}