package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LevelOne {

    private Pane root = new Pane();
    private Main app;

    // ===== CONSTANTS =====
    private static final int TILE_SIZE = 48;
    private static final int WIDTH = Main.WIDTH;
    private static final int HEIGHT = Main.HEIGHT;

    // ===== PLAYER =====
    private double playerX, playerY;
    private double velocityY = 0;
    private boolean gravityDown = true;
    private static final int PLAYER_SIZE = 32;
    private static final double JUMP_STRENGTH = 12;
    private static final double GRAVITY = 0.6;
    private static final double MOVE_SPEED = 4;
    private boolean isOnGround = false;

    private boolean moveLeft = false;
    private boolean moveRight = false;

    // ===== GAME STATE =====
    private int coinsCollected = 0;
    private int totalCoins = 0;
    private boolean gateOpen = false;
    private boolean levelCompleted = false;
    private AnimationTimer gameLoop;

    // ===== IMAGES =====
    private Image brick, coin, spike, gateClosed, gateOpenImg, playerImg;

    // ===== MAP =====
    private String[] MAP = {
            "####################",
            "#   C        C   E #",
            "#   ######    ### #",
            "#          ^      #",
            "#####   ########  #",
            "#       #         #",
            "#   C   # P ^ ####  #",
            "####################"
    };

    public LevelOne(Main app) {
        this.app = app;

        root.setPrefSize(WIDTH, HEIGHT);
        root.setStyle("-fx-background-color: #0b0616;");
        root.setFocusTraversable(true);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        canvas.setFocusTraversable(false);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // ===== LOAD IMAGES =====
        brick = load("brick.png");
        coin = load("coin.png");
        spike = load("spike.png");
        gateClosed = load("gate_closed.png");
        gateOpenImg = load("gate_open.png");
        playerImg = load("player.png");

        findPlayerAndCoins();

        // ===== INPUT =====
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    if (levelCompleted) return;

                    if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                        moveLeft = true;
                    }
                    if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                        moveRight = true;
                    }

                    if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.W) {
                        if (gravityDown && isOnGround) {
                            velocityY = -JUMP_STRENGTH;
                        } else if (!gravityDown && isOnGround) {
                            velocityY = JUMP_STRENGTH;
                        }
                    }

                    if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) {
                        gravityDown = !gravityDown;
                        velocityY = 0;
                    }
                });

                newScene.setOnKeyReleased(e -> {
                    if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                        moveLeft = false;
                    }
                    if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                        moveRight = false;
                    }
                });

                root.requestFocus();
            }
        });

        // ===== GAME LOOP =====
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw(gc);
            }
        };
        gameLoop.start();
    }

    private Image load(String name) {
        return new Image(getClass().getResource("/assets/" + name).toExternalForm());
    }

    private void findPlayerAndCoins() {
        totalCoins = 0;
        for (int r = 0; r < MAP.length; r++) {
            for (int c = 0; c < MAP[r].length(); c++) {
                char ch = MAP[r].charAt(c);
                if (ch == 'P') {
                    playerX = c * TILE_SIZE + 8;
                    playerY = r * TILE_SIZE + 8;
                }
                if (ch == 'C') totalCoins++;
            }
        }
    }

    private void update() {
        if (levelCompleted) return;

        // ===== HORIZONTAL MOVEMENT =====
        if (moveLeft) {
            double nextX = playerX - MOVE_SPEED;
            if (!isSolid((int) nextX, (int) (playerY + PLAYER_SIZE / 2))) {
                playerX = nextX;
            }
        }
        if (moveRight) {
            double nextX = playerX + MOVE_SPEED;
            if (!isSolid((int) (nextX + PLAYER_SIZE), (int) (playerY + PLAYER_SIZE / 2))) {
                playerX = nextX;
            }
        }

        // ===== VERTICAL MOVEMENT =====
        if (gravityDown) {
            velocityY += GRAVITY;
        } else {
            velocityY -= GRAVITY;
        }

        if (velocityY > 15) velocityY = 15;
        if (velocityY < -15) velocityY = -15;

        double nextY = playerY + velocityY;
        isOnGround = false;

        if (gravityDown) {
            int bottomY = (int) (nextY + PLAYER_SIZE);
            int leftX = (int) (playerX + 4);
            int rightX = (int) (playerX + PLAYER_SIZE - 4);

            if (isSolid(leftX, bottomY) || isSolid(rightX, bottomY)) {
                velocityY = 0;
                playerY = ((bottomY / TILE_SIZE)) * TILE_SIZE - PLAYER_SIZE;
                isOnGround = true;
            } else {
                playerY = nextY;
            }

            int topY = (int) nextY;
            if (velocityY < 0 && (isSolid(leftX, topY) || isSolid(rightX, topY))) {
                velocityY = 0;
                playerY = ((topY / TILE_SIZE) + 1) * TILE_SIZE;
            }

        } else {
            int topY = (int) nextY;
            int leftX = (int) (playerX + 4);
            int rightX = (int) (playerX + PLAYER_SIZE - 4);

            if (isSolid(leftX, topY) || isSolid(rightX, topY)) {
                velocityY = 0;
                playerY = ((topY / TILE_SIZE) + 1) * TILE_SIZE;
                isOnGround = true;
            } else {
                playerY = nextY;
            }

            int bottomY = (int) (nextY + PLAYER_SIZE);
            if (velocityY > 0 && (isSolid(leftX, bottomY) || isSolid(rightX, bottomY))) {
                velocityY = 0;
                playerY = ((bottomY / TILE_SIZE)) * TILE_SIZE - PLAYER_SIZE;
            }
        }
    }

    private void draw(GraphicsContext gc) {

        gc.setFill(Color.web("#0b0616"));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        for (int r = 0; r < MAP.length; r++) {
            for (int c = 0; c < MAP[r].length(); c++) {

                char ch = MAP[r].charAt(c);
                int x = c * TILE_SIZE;
                int y = r * TILE_SIZE;

                if (ch == '#') {
                    gc.drawImage(brick, x, y, TILE_SIZE, TILE_SIZE);
                }

                if (ch == 'C') {
                    gc.drawImage(coin, x + 10, y + 10, 28, 28);
                    if (collide(x, y)) {
                        MAP[r] = MAP[r].substring(0, c) + ' ' + MAP[r].substring(c + 1);
                        coinsCollected++;
                        if (coinsCollected == totalCoins) {
                            gateOpen = true;
                        }
                    }
                }

                if (ch == '^') {
                    gc.drawImage(spike, x + 16, y + 16, 16, 16);
                    if (collide(x, y)) {
                        resetLevel();
                    }
                }

                if (ch == 'E') {
                    Image gate = gateOpen ? gateOpenImg : gateClosed;
                    gc.drawImage(gate, x, y, TILE_SIZE, TILE_SIZE);

                    // CHECK IF PLAYER ENTERS OPENED GATE
                    if (gateOpen && collide(x, y) && !levelCompleted) {
                        levelCompleted = true;
                        showVictoryDialog();
                    }
                }
            }
        }

        // Draw player
        if (gravityDown) {
            gc.drawImage(playerImg, playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
        } else {
            gc.save();
            gc.translate(playerX + PLAYER_SIZE / 2.0, playerY + PLAYER_SIZE / 2.0);
            gc.scale(1, -1);
            gc.drawImage(playerImg, -PLAYER_SIZE / 2.0, -PLAYER_SIZE / 2.0, PLAYER_SIZE, PLAYER_SIZE);
            gc.restore();
        }

        // ===== UI =====
        gc.setFill(Color.web("#12091f"));
        gc.fillRect(0, HEIGHT - 50, WIDTH, 50);
        gc.setFill(Color.WHITE);
        gc.fillText("Coins: " + coinsCollected + "/" + totalCoins + " | Controls: ←→ Move, ↑/SPACE Jump, ↓ Flip Gravity", 20, HEIGHT - 20);

        String gravityText = gravityDown ? "↓ DOWN" : "↑ UP";
        String groundText = isOnGround ? "GROUNDED" : "IN AIR";
        gc.fillText("Gravity: " + gravityText + " | " + groundText + " | Level: 1", 20, HEIGHT - 35);
    }

    // ===== VICTORY DIALOG =====
    private void showVictoryDialog() {
        gameLoop.stop();

        // Unlock next level
        LevelSelect.unlockNextLevel();

        Platform.runLater(() -> {
            // Create custom victory window
            Stage victoryStage = new Stage();
            victoryStage.initModality(Modality.APPLICATION_MODAL);
            victoryStage.initStyle(StageStyle.UNDECORATED);
            victoryStage.setTitle("Victory!");

            // Root StackPane for background image
            StackPane stackPane = new StackPane();

            // Background image
            try {
                Image bgImage = new Image(getClass().getResourceAsStream("/assets/background3.jpeg"));
                ImageView background = new ImageView(bgImage);
                background.setFitWidth(550);
                background.setFitHeight(500);
                background.setPreserveRatio(false);
                background.setOpacity(0.9); // Slight transparency
                stackPane.getChildren().add(background);
            } catch (Exception e) {
                // Fallback purple gradient
                stackPane.setStyle("-fx-background-color: linear-gradient(to bottom, #ff9a9e, #fad0c4);");
            }

            // Content VBox with semi-transparent background
            VBox vbox = new VBox(20);
            vbox.setAlignment(Pos.CENTER);
            vbox.setStyle(
                    "-fx-background-color: rgba(139, 69, 139, 0.75);" + // Purple with transparency
                            "-fx-border-color: rgba(255, 182, 193, 0.9);" + // Light pink border
                            "-fx-border-width: 4;" +
                            "-fx-border-radius: 20;" +
                            "-fx-background-radius: 20;" +
                            "-fx-padding: 40;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0.5, 0, 0);"
            );
            vbox.setMaxWidth(450);

            // Victory Title with glow
            Label titleLabel = new Label("🎉 VICTORY! 🎉");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 42));
            titleLabel.setTextFill(Color.web("#FFE5EC")); // Light pink
            titleLabel.setStyle(
                    "-fx-effect: dropshadow(gaussian, rgba(255,182,193,1), 15, 0.8, 0, 0);"
            );

            // Congratulations message
            Label congratsLabel = new Label("Level 1 Complete!");
            congratsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
            congratsLabel.setTextFill(Color.web("#FFC4D6")); // Soft pink

            // Coins collected with icon
            Label coinsLabel = new Label("💰 Coins: " + coinsCollected + "/" + totalCoins);
            coinsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            coinsLabel.setTextFill(Color.web("#FFD700")); // Gold
            coinsLabel.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.2);" +
                            "-fx-padding: 10 20;" +
                            "-fx-background-radius: 15;"
            );

            // Achievement stars
            Label starsLabel = new Label("⭐ ⭐ ⭐");
            starsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
            starsLabel.setTextFill(Color.web("#FFE66D"));

            // Next Level Button - Pink gradient matching background
            Button nextLevelBtn = new Button("➡  NEXT LEVEL  ➡");
            nextLevelBtn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            nextLevelBtn.setPrefWidth(300);
            nextLevelBtn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #ff6b9d, #c86dd7);" +
                            "-fx-text-fill: white;" +
                            "-fx-padding: 15 35;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 12;" +
                            "-fx-effect: dropshadow(gaussian, rgba(255,107,157,0.8), 10, 0.6, 0, 3);"
            );
            nextLevelBtn.setOnMouseEntered(e ->
                    nextLevelBtn.setStyle(
                            "-fx-background-color: linear-gradient(to right, #ff85ab, #d688e3);" +
                                    "-fx-text-fill: white;" +
                                    "-fx-padding: 15 35;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-border-color: white;" +
                                    "-fx-border-width: 3;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(255,107,157,1), 15, 0.8, 0, 4);" +
                                    "-fx-scale-x: 1.05;" +
                                    "-fx-scale-y: 1.05;"
                    )
            );
            nextLevelBtn.setOnMouseExited(e ->
                    nextLevelBtn.setStyle(
                            "-fx-background-color: linear-gradient(to right, #ff6b9d, #c86dd7);" +
                                    "-fx-text-fill: white;" +
                                    "-fx-padding: 15 35;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-border-color: white;" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(255,107,157,0.8), 10, 0.6, 0, 3);"
                    )
            );
            nextLevelBtn.setOnAction(e -> {
                victoryStage.close();
                app.loadLevel2(); // Load Level 2
            });

            // Restart Button - Lighter purple
            Button restartBtn = new Button("🔄 Restart");
            restartBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            restartBtn.setPrefWidth(200);
            restartBtn.setStyle(
                    "-fx-background-color: rgba(186, 85, 211, 0.9);" + // Medium orchid
                            "-fx-text-fill: white;" +
                            "-fx-padding: 12 28;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: rgba(255,255,255,0.6);" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 5, 0.5, 0, 2);"
            );
            restartBtn.setOnMouseEntered(e ->
                    restartBtn.setStyle(
                            "-fx-background-color: rgba(206, 105, 231, 1);" +
                                    "-fx-text-fill: white;" +
                                    "-fx-padding: 12 28;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-border-color: white;" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 10;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 8, 0.6, 0, 3);"
                    )
            );
            restartBtn.setOnMouseExited(e ->
                    restartBtn.setStyle(
                            "-fx-background-color: rgba(186, 85, 211, 0.9);" +
                                    "-fx-text-fill: white;" +
                                    "-fx-padding: 12 28;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-border-color: rgba(255,255,255,0.6);" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 10;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 5, 0.5, 0, 2);"
                    )
            );
            restartBtn.setOnAction(e -> {
                victoryStage.close();
                resetLevel();
                levelCompleted = false;
                gameLoop.start();
            });

            // Level Select Button - Purple shade
            Button levelSelectBtn = new Button("📋 Level Select");
            levelSelectBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            levelSelectBtn.setPrefWidth(200);
            levelSelectBtn.setStyle(
                    "-fx-background-color: rgba(123, 104, 238, 0.9);" + // Medium slate blue
                            "-fx-text-fill: white;" +
                            "-fx-padding: 12 28;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: rgba(255,255,255,0.6);" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 5, 0.5, 0, 2);"
            );
            levelSelectBtn.setOnMouseEntered(e ->
                    levelSelectBtn.setStyle(
                            "-fx-background-color: rgba(143, 124, 255, 1);" +
                                    "-fx-text-fill: white;" +
                                    "-fx-padding: 12 28;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-border-color: white;" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 10;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 8, 0.6, 0, 3);"
                    )
            );
            levelSelectBtn.setOnMouseExited(e ->
                    levelSelectBtn.setStyle(
                            "-fx-background-color: rgba(123, 104, 238, 0.9);" +
                                    "-fx-text-fill: white;" +
                                    "-fx-padding: 12 28;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-border-color: rgba(255,255,255,0.6);" +
                                    "-fx-border-width: 2;" +
                                    "-fx-border-radius: 10;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 5, 0.5, 0, 2);"
                    )
            );
            levelSelectBtn.setOnAction(e -> {
                victoryStage.close();
                app.showLevelSelect();
            });

            // Button container
            VBox buttonBox = new VBox(15);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().addAll(nextLevelBtn, restartBtn, levelSelectBtn);

            vbox.getChildren().addAll(
                    titleLabel,
                    congratsLabel,
                    starsLabel,
                    coinsLabel,
                    buttonBox
            );

            stackPane.getChildren().add(vbox);

            Scene scene = new Scene(stackPane, 550, 500);
            scene.setFill(Color.TRANSPARENT);
            victoryStage.setScene(scene);
            victoryStage.showAndWait();
        });
    }

    private boolean isSolid(int px, int py) {
        int col = px / TILE_SIZE;
        int row = py / TILE_SIZE;
        if (row < 0 || row >= MAP.length || col < 0 || col >= MAP[0].length()) {
            return true;
        }
        return MAP[row].charAt(col) == '#';
    }

    private boolean collide(int x, int y) {
        return Math.abs(playerX - x) < 28 && Math.abs(playerY - y) < 28;
    }

    private void resetLevel() {
        coinsCollected = 0;
        gateOpen = false;
        velocityY = 0;
        gravityDown = true;
        findPlayerAndCoins();
    }

    public Pane getRoot() {
        return root;
    }
}