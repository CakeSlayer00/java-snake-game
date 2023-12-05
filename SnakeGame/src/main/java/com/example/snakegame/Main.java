package com.example.snakegame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;

public class Main extends Application {
    final int WIDTH = 600;
    final int HEIGHT = 600;
    final int UNIT_SIZE = 25;
    final int GAME_UNITS = (WIDTH * HEIGHT) / UNIT_SIZE;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int delay = 100;
    int bodyParts = 4;
    int score;
    int appleX;
    int appleY;
    boolean isRunning = false;
    Direction currentDirection = Direction.RIGHT;
    Random random;
    AnimationTimer timer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        random = new Random();
        newApple();
        isRunning = true;

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Group root = new Group();
        root.getChildren().add(canvas);

        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= delay * 1000000L) {
                    gc.clearRect(0, 0, WIDTH, HEIGHT);
                    move();
                    checkForApple();
                    intersects();
                    draw(gc);
                    lastUpdate = now;
                }
            }
        };

        Scene scene = new Scene(root, Color.BLACK);
        update(scene);

        stage.setResizable(false);
        stage.setTitle("Snake");
        stage.setScene(scene);
        stage.show();
    }

    private void update(Scene scene) {
        timer.start();

        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    if (currentDirection != Direction.DOWN) {
                        currentDirection = Direction.UP;
                    }
                    break;
                case A:
                    if (currentDirection != Direction.RIGHT) {
                        currentDirection = Direction.LEFT;
                    }
                    break;
                case S:
                    if (currentDirection != Direction.UP) {
                        currentDirection = Direction.DOWN;
                    }
                    break;
                case D:
                    if (currentDirection != Direction.LEFT) {
                        currentDirection = Direction.RIGHT;
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

        switch (currentDirection) {
            case UP:
                y[0] = y[0] - UNIT_SIZE;
                break;
            case DOWN:
                y[0] = y[0] + UNIT_SIZE;
                break;
            case LEFT:
                x[0] = x[0] - UNIT_SIZE;
                break;
            case RIGHT:
                x[0] = x[0] + UNIT_SIZE;
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

        if (x[0] < 0 || y[0] < 0 || x[0] >= WIDTH || y[0] >= HEIGHT) {
            isRunning = false;
        }

        if (!isRunning) {
            timer.stop();
        }
    }

    private void newApple() {
        appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    private void draw(GraphicsContext gc) {
        if (isRunning) {
            drawGrid(gc);
            drawApple(gc);
            drawSnake(gc);
            drawScore(gc);
            drawDelay(gc);
        } else {
            drawGameOverMenu(gc);
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
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                gc.setFill(Color.GREEN);
            } else {
                gc.setFill(Color.DARKGREEN);
            }
            gc.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }
    }

    private void drawApple(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
    }

    private void drawGameOverMenu(GraphicsContext gc) {
        gc.setFill(Color.CADETBLUE);
        gc.setFont(Font.font("ArcadeClassic", 100));
        gc.fillText("Game Over", 75, (double) HEIGHT / 2);
    }
}