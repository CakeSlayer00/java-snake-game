package com.example.snakegame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;

public class Main extends Application {
    int WIDTH = 600;
    int HEIGHT = 600;
    final int UNIT_SIZE = 25;
    final int GAME_UNITS = (WIDTH * HEIGHT) / UNIT_SIZE;
    int[] x = new int[GAME_UNITS];
    int[] y = new int[GAME_UNITS];
    int delay = 75;
    int bodyParts = 4;
    int score;
    int highScore;
    int appleX;
    int appleY;
    boolean isRunning = false;
    Direction direction = Direction.RIGHT;
    Random random = new Random();
    AnimationTimer timer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        newApple();
        isRunning = true;

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Button button = new Button();
        button.setVisible(false);

        Group root = new Group();
        root.getChildren().addAll(canvas, button);

        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= delay * 1000000L) {
                    gc.clearRect(0, 0, WIDTH, HEIGHT);
                    checkForApple();
                    move();
                    intersects();
                    draw(gc, button);
                    lastUpdate = now;
                }
            }
        };

        Scene scene = new Scene(root, Color.BLACK);
        update(scene);

        stage.setTitle("Snake");
        stage.setScene(scene);
        stage.show();
    }

    private void update(Scene scene) {
        timer.start();

        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    if (direction != Direction.DOWN) {
                        direction = Direction.UP;
                    }
                    break;
                case A:
                    if (direction != Direction.RIGHT) {
                        direction = Direction.LEFT;
                    }
                    break;
                case S:
                    if (direction != Direction.UP) {
                        direction = Direction.DOWN;
                    }
                    break;
                case D:
                    if (direction != Direction.LEFT) {
                        direction = Direction.RIGHT;
                    }
                    break;
            }
        });
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case UP:
                y[0] -= UNIT_SIZE;
                break;
            case DOWN:
                y[0] += UNIT_SIZE;
                break;
            case LEFT:
                x[0] -= UNIT_SIZE;
                break;
            case RIGHT:
                x[0] += UNIT_SIZE;
                break;
        }
    }

    public void checkForApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            score++;
            newApple();
        }
    }

    private void intersects() {
        for (int i = 1; i < bodyParts; i++) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                isRunning = false;
                break;
            }
        }

        x[0] = (x[0] + WIDTH) % WIDTH;
        y[0] = (y[0] + HEIGHT) % HEIGHT;

        if (!isRunning) {
            timer.stop();
        }
    }

    private void newApple() {
        appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    private void draw(GraphicsContext gc, Button button) {
        if (isRunning) {
            drawApple(gc);
            drawSnake(gc);
            drawScore(gc);
        } else {
            drawGameOverMenu(gc, button);
        }

    }

    private void drawGrid(GraphicsContext gc) {
        for (int i = 0; i < HEIGHT / UNIT_SIZE; i++) {
            gc.setStroke(Color.GRAY);
            gc.strokeLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, HEIGHT);
            gc.strokeLine(0, i * UNIT_SIZE, WIDTH, i * UNIT_SIZE);
        }
    }

    private void drawDelay(GraphicsContext gc) {
        gc.setFill(Color.rgb(223, 255, 0));
        gc.setFont(Font.font("ArcadeClassic", 50));
        gc.fillText("Delay: " + delay, 200, 570);
    }

    private void drawScore(GraphicsContext gc) {
        gc.setFill(Color.rgb(223, 255, 0));
        gc.setFont(Font.font("ArcadeClassic", 50));
        gc.fillText("Score: " + score, 200, 540);
    }

    private void drawSnake(GraphicsContext gc) {
        Color rainbow = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                gc.setFill(Color.rgb(147, 112, 219));
            } else {
                if (score % 2 == 0) {
                    gc.setFill(Color.rgb(138, 43, 226));
                } else {
                    gc.setFill(rainbow);
                }

            }
            gc.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }
    }

    private void drawApple(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
    }

    private void drawGameOverMenu(GraphicsContext gc, Button button) {
        highScore = Math.max(highScore, score);

        gc.setFill(Color.rgb(253, 103, 58));
        gc.setFont(Font.font("ArcadeClassic", 100));
        gc.fillText("Game Over", 75, (double) HEIGHT / 2);

        gc.setFill(Color.rgb(202, 52, 51));
        gc.setFont(Font.font("ArcadeClassic", 50));
        gc.fillText("HIGH SCORE: " + highScore, 160, (double) HEIGHT / 2 + 30);

        callRestartButton(button);
    }

    private void callRestartButton(Button button) {
        setUpButton(button);

        button.setOnAction(e -> {
            restart();
            button.setVisible(false);

        });

    }

    private void restart() {
        isRunning = true;
        bodyParts = 4;
        score = 0;
        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];
        direction = Direction.RIGHT;
        timer.start();
    }

    private void setUpButton(Button button) {
        button.setVisible(true);
        button.setLayoutX(180);
        button.setLayoutY((double) HEIGHT / 2 + 7);
        button.setText("RESTART");
        button.setStyle("-fx-background-color: rgba(0 , 0 , 0 ,0 ); -fx-text-fill: rgb(223, 255, 0);");
        button.setFont(Font.font("ArcadeClassic", 50));
    }
}