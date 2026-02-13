package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LevelSelect {

    private StackPane root;
    private Main app;
    private static int unlockedLevel = 1; // Static variable to track progress

    public LevelSelect(Main app) {
        this.app = app;

        root = new StackPane();

        // Background image
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("/assets/background1.jpeg"));
            ImageView background = new ImageView(bgImage);
            background.setFitWidth(Main.WIDTH);
            background.setFitHeight(Main.HEIGHT);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        } catch (Exception e) {
            System.out.println("Background image not found!");
            root.setStyle("-fx-background-color: linear-gradient(to bottom, #ff9a9e, #fad0c4);");
        }

        // Main content VBox
        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));

        // Title with glow effect
        Label titleLabel = new Label("SELECT LEVEL");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 45));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle(
                "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.9), 20, 0.8, 0, 0);" +
                        "-fx-background-color: rgba(0,0,0,0.3);" +
                        "-fx-padding: 10 30;" +
                        "-fx-background-radius: 15;"
        );

        // Grid for level buttons
        GridPane levelGrid = new GridPane();
        levelGrid.setHgap(25);
        levelGrid.setVgap(25);
        levelGrid.setAlignment(Pos.CENTER);

        // Create level buttons (5 levels in 3-2 layout)
        for (int i = 1; i <= 5; i++) {
            Button levelBtn = createLevelButton(i);
            int col = (i - 1) % 3;
            int row = (i - 1) / 3;
            levelGrid.add(levelBtn, col, row);
        }

        // Back button (optional - since you removed menu)
        // Comment out if you don't want back button
        /*
        Button backBtn = new Button("⬅ BACK");
        backBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        backBtn.setPrefWidth(150);
        backBtn.setStyle(
            "-fx-background-color: rgba(244,67,54,0.9); -fx-text-fill: white; " +
            "-fx-padding: 12 25; -fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0.5, 0, 2);"
        );
        backBtn.setOnMouseEntered(e ->
            backBtn.setStyle(
                "-fx-background-color: rgba(218,25,11,1); -fx-text-fill: white; " +
                "-fx-padding: 12 25; -fx-background-radius: 10;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 8, 0.5, 0, 3);"
            )
        );
        backBtn.setOnMouseExited(e ->
            backBtn.setStyle(
                "-fx-background-color: rgba(244,67,54,0.9); -fx-text-fill: white; " +
                "-fx-padding: 12 25; -fx-background-radius: 10;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0.5, 0, 2);"
            )
        );
        backBtn.setOnAction(e -> app.showMenuScene());
        */

        content.getChildren().addAll(titleLabel, levelGrid);
        root.getChildren().add(content);
    }

    private Button createLevelButton(int levelNum) {
        Button btn = new Button();
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        btn.setPrefSize(180, 120);

        boolean isLocked = levelNum > unlockedLevel;

        if (isLocked) {
            // Locked level - Purple/pink theme matching background
            btn.setText("🔒\nLEVEL " + levelNum);
            btn.setStyle(
                    "-fx-background-color: rgba(139,69,139,0.7);" + // Purple
                            "-fx-text-fill: rgba(255,255,255,0.5);" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-color: rgba(255,255,255,0.3);" +
                            "-fx-border-width: 3;" +
                            "-fx-border-radius: 15;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 5, 0.5, 0, 2);"
            );
            btn.setDisable(true);
        } else {
            // Unlocked level - Glowing button with pink/purple gradient
            btn.setText("⭐\nLEVEL " + levelNum);
            btn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #ff6b9d, #c86dd7);" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 3;" +
                            "-fx-border-radius: 15;" +
                            "-fx-effect: dropshadow(gaussian, rgba(255,107,157,0.8), 15, 0.7, 0, 0);"
            );

            btn.setOnMouseEntered(e ->
                    btn.setStyle(
                            "-fx-background-color: linear-gradient(to bottom, #ff85ab, #d688e3);" +
                                    "-fx-text-fill: white;" +
                                    "-fx-background-radius: 15;" +
                                    "-fx-border-color: white;" +
                                    "-fx-border-width: 4;" +
                                    "-fx-border-radius: 15;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(255,107,157,1.0), 20, 0.9, 0, 0);" +
                                    "-fx-scale-x: 1.05;" +
                                    "-fx-scale-y: 1.05;"
                    )
            );

            btn.setOnMouseExited(e ->
                    btn.setStyle(
                            "-fx-background-color: linear-gradient(to bottom, #ff6b9d, #c86dd7);" +
                                    "-fx-text-fill: white;" +
                                    "-fx-background-radius: 15;" +
                                    "-fx-border-color: white;" +
                                    "-fx-border-width: 3;" +
                                    "-fx-border-radius: 15;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(255,107,157,0.8), 15, 0.7, 0, 0);"
                    )
            );

            btn.setOnAction(e -> {
                switch (levelNum) {
                    case 1:
                        app.showLevelOne();
                        break;
                    case 2:
                        app.loadLevel2();
                        break;
                    case 3:
                        app.loadLevel3();
                        break;
                    case 4:
                        app.loadLevel4();
                        break;
                    case 5:
                        app.loadLevel5();
                        break;
                }
            });
        }

        return btn;
    }

    // Call this method when a level is completed to unlock next level
    public static void unlockNextLevel() {
        if (unlockedLevel < 5) {
            unlockedLevel++;
            System.out.println("Level " + unlockedLevel + " unlocked!");
        }
    }

    // Reset progress (for testing)
    public static void resetProgress() {
        unlockedLevel = 1;
    }

    public StackPane getRoot() {
        return root;
    }
}