package org.example;

import javafx.animation.PauseTransition;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class SplashScreen {

    private Pane root = new Pane();

    public SplashScreen(Main app) {

        root.setPrefSize(Main.WIDTH, Main.HEIGHT);
        root.setStyle("-fx-background-color: #0b0616;");

        Label title = new Label("GRAVITY REVERSE");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Verdana", 48));
        title.setLayoutX(260);
        title.setLayoutY(300);

        root.getChildren().add(title);

        // ⏱ SPLASH TIMER (SAFE)
        PauseTransition wait = new PauseTransition(Duration.seconds(5));
        wait.setOnFinished(e -> {
            if (app.isSplashActive()) {
                System.out.println("SPLASH FINISHED → GO LEVEL SELECT");
                app.showLevelSelect();
            }
        });
        wait.play();
    }

    public Pane getRoot() {
        return root;
    }
}
