package org.example;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class HomeScreen {

    private StackPane root;
    private Main main;
    private Text titleText;
    private String fullTitle = "GravX : The Escape Room";

    public HomeScreen(Main main) {
        this.main = main;
        createUI();
        // playBackgroundMusic(); // COMMENTED - Add later when media works
    }

    private void createUI() {
        root = new StackPane();

        // Background image (pixel art sky)
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("/assets/background.jpeg"));
            ImageView background = new ImageView(bgImage);
            background.setFitWidth(Main.WIDTH);
            background.setFitHeight(Main.HEIGHT);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        } catch (Exception e) {
            System.out.println("Background image not found!");
            root.setStyle("-fx-background-color: #5b4e91;");
        }

        // Title text (will be typed one by one)
        titleText = new Text("");
        titleText.setFont(Font.font("Courier New", FontWeight.BOLD, 48));
        titleText.setFill(Color.WHITE);
        titleText.setStroke(Color.BLACK);
        titleText.setStrokeWidth(2);

        StackPane.setAlignment(titleText, Pos.CENTER);
        root.getChildren().add(titleText);

        // Fade in effect
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // Start typing animation after 500ms
        Timeline startDelay = new Timeline(new KeyFrame(Duration.millis(500), e -> startTypingAnimation()));
        startDelay.play();
    }

    /* UNCOMMENT WHEN MEDIA MODULE IS ADDED
    private void playBackgroundMusic() {
        try {
            Media sound = new Media(getClass().getResource("/assets/home_music.mp3").toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setVolume(0.5);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
            System.out.println("Home music playing...");
        } catch (Exception e) {
            System.out.println("Home music not found!");
        }
    }
    */

    private void startTypingAnimation() {
        // Fast typing - har character 100ms mein (total ~2.5 seconds)
        double typingSpeed = 100; // milliseconds per character

        Timeline typingTimeline = new Timeline();

        for (int i = 0; i <= fullTitle.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(typingSpeed * i),
                    event -> {
                        titleText.setText(fullTitle.substring(0, index));

                        // Jab pura type ho jaye, fade out and go to LEVEL SELECT
                        if (index == fullTitle.length()) {
                            Timeline waitAndTransition = new Timeline(new KeyFrame(
                                    Duration.seconds(0.5), // 0.5 second wait after typing
                                    e -> transitionToLevelSelect()
                            ));
                            waitAndTransition.play();
                        }
                    }
            );
            typingTimeline.getKeyFrames().add(keyFrame);
        }

        typingTimeline.play();
    }

    private void transitionToLevelSelect() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(800), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> main.showLevelSelect());
        fadeOut.play();
    }

    public StackPane getRoot() {
        return root;
    }
}