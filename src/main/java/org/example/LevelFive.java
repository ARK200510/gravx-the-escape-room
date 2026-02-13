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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LevelFive {

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

    // ===== LEVEL 5 MAP - ULTIMATE CHALLENGE =====
    private String[] MAP = {
            "####################",
            "#P^   C   ^   C   E#",
            "#  #### # #### # ###",
            "#    #  ^  #   ^   #",
            "###  #  ##    ###  #",
            "#  ^ # ## C ####  ^#",
            "#   C  ^     ^  C  #",
            "####################"
    };

    public LevelFive(Main app) {
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
        gc.fillText("Gravity: " + gravityText + " | " + groundText + " | Level: 5 (FINAL)", 20, HEIGHT - 35);
    }

    // ===== FINAL VICTORY DIALOG =====
    private void showVictoryDialog() {
        gameLoop.stop();

        Platform.runLater(() -> {
            Stage victoryStage = new Stage();
            victoryStage.initModality(Modality.APPLICATION_MODAL);
            victoryStage.initStyle(StageStyle.UNDECORATED);
            victoryStage.setTitle("GAME COMPLETE!");

            VBox vbox = new VBox(20);
            vbox.setAlignment(Pos.CENTER);
            vbox.setStyle("-fx-background-color: linear-gradient(to bottom, #1a0033, #0b0616); " +
                    "-fx-border-color: #FFD700; -fx-border-width: 5; -fx-padding: 50;");

            Label titleLabel = new Label("🏆 GAME COMPLETE! 🏆");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 42));
            titleLabel.setTextFill(Color.GOLD);
            titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.9), 20, 0.7, 0, 0);");

            Label congratsLabel = new Label("LEGENDARY!");
            congratsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            congratsLabel.setTextFill(Color.LIGHTGREEN);

            Label levelLabel = new Label("You have conquered ALL 5 levels!");
            levelLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            levelLabel.setTextFill(Color.WHITE);

            Label coinsLabel = new Label("Final Level Coins: " + coinsCollected + "/" + totalCoins);
            coinsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            coinsLabel.setTextFill(Color.YELLOW);

            Label achievementLabel = new Label("🌟 GRAVITY MASTER 🌟");
            achievementLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            achievementLabel.setTextFill(Color.ORANGE);
            achievementLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,165,0,0.8), 10, 0.5, 0, 0);");

            Label thanksLabel = new Label("Thank you for playing!");
            thanksLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            thanksLabel.setTextFill(Color.LIGHTBLUE);

            Button restartBtn = new Button("🔄 Restart Level 5");
            restartBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            restartBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                    "-fx-padding: 12 28; -fx-background-radius: 8;");
            restartBtn.setOnMouseEntered(e ->
                    restartBtn.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; " +
                            "-fx-padding: 12 28; -fx-background-radius: 8;"));
            restartBtn.setOnMouseExited(e ->
                    restartBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                            "-fx-padding: 12 28; -fx-background-radius: 8;"));
            restartBtn.setOnAction(e -> {
                victoryStage.close();
                resetLevel();
                levelCompleted = false;
                gameLoop.start();
            });

            Button menuBtn = new Button("🏠 Main Menu");
            menuBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            menuBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                    "-fx-padding: 12 28; -fx-background-radius: 8;");
            menuBtn.setOnMouseEntered(e ->
                    menuBtn.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; " +
                            "-fx-padding: 12 28; -fx-background-radius: 8;"));
            menuBtn.setOnMouseExited(e ->
                    menuBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                            "-fx-padding: 12 28; -fx-background-radius: 8;"));
            menuBtn.setOnAction(e -> {
                victoryStage.close();
                app.showLevelSelect();
            });

            Button exitBtn = new Button("❌ Exit Game");
            exitBtn.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            exitBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                    "-fx-padding: 10 25; -fx-background-radius: 8;");
            exitBtn.setOnMouseEntered(e ->
                    exitBtn.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; " +
                            "-fx-padding: 10 25; -fx-background-radius: 8;"));
            exitBtn.setOnMouseExited(e ->
                    exitBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                            "-fx-padding: 10 25; -fx-background-radius: 8;"));
            exitBtn.setOnAction(e -> System.exit(0));

            VBox buttonBox = new VBox(12);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().addAll(restartBtn, menuBtn, exitBtn);

            vbox.getChildren().addAll(titleLabel, congratsLabel, levelLabel, coinsLabel, achievementLabel, thanksLabel, buttonBox);

            Scene scene = new Scene(vbox, 550, 500);
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