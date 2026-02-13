package org.example;

import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuScene {

    private Pane root = new Pane();

    public MenuScene(Main app) {

        root.setPrefSize(960, 672);
        root.setStyle("-fx-background-color: #0b0616;");

        Label title = new Label("GRAVITY REVERSE");
        title.setLayoutX(260);
        title.setLayoutY(200);
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Verdana", 48));

        Button startBtn = new Button("GET STARTED");
        startBtn.setLayoutX(390);
        startBtn.setLayoutY(320);
        startBtn.setPrefWidth(180);
        startBtn.setPrefHeight(50);

        // 🔥 THIS MUST FIRE
        startBtn.setOnAction(e -> {
            System.out.println("GET STARTED CLICKED");
            app.showLevelOne();
        });

        root.getChildren().addAll(title, startBtn);
    }

    public Pane getRoot() {
        return root;
    }
}
