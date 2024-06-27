package com.simulation.simulation;

import com.simulation.simulation.model.GameEntity;
import com.simulation.simulation.model.GamePosition;
import com.simulation.simulation.model.LivingEntity;
import com.simulation.simulation.model.game.entities.*;
import kotlin.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * Třída Game představuje hlavní herní logiku a řízení hry.
 * Obsahuje metody pro spuštění a zastavení hry, řízení herního běhu
 * a generování herních entit.
 */

public class Game {

    /**
     * The game state.
     */
    @Getter
    @Setter
    private static GameState gameState;
    /**
     * The game board.
     */
    @Getter
    @Setter
    private static GameBoard gameBoard;

    /**
     * The interval in milliseconds for each iteration of the game loop.
     */
    private final int TICK_INTERVAL = 100;
    /**
     * Indicates whether the game is stopped.
     */
    @Getter
    static boolean stopGame = false;
    /**
     * The time of the last iteration of the game loop.
     */
    private long lastTickTime = 0;







    private static Iterator<GamePosition> positionIterator;

    public Game(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        initializePositionIterator();
    }

    /**
     * Initializes the position iterator with all possible positions respecting the 60x60 pixel size.
     * The iterator is used to generate positions for the game entities.
     * The positions are shuffled to ensure randomness.
     */
    private void initializePositionIterator() {
        int width = gameBoard.getWidth();
        int height = gameBoard.getHeight();
        int entitySize = 60; // 60x60 pixels
        List<GamePosition> positions = new ArrayList<>();

        for (int x = 0; x <= width - entitySize; x += entitySize) {
            for (int y = 0; y <= height - entitySize; y += entitySize) {
                positions.add(new GamePosition(x, y));
            }
        }

        Collections.shuffle(positions);
        this.positionIterator = positions.iterator();
    }

    /**
     * Starts the game.
     * Initializes the game state and generates the game entities.
     * Starts the threads for all game entities.
     */
    public void startGame() {
        gameState = new GameState(gameBoard, this.generateEntities(HelloApplication.foxCount, HelloApplication.hareCount, HelloApplication.grassCount, HelloApplication.lakeCount, HelloApplication.hunterCount));
        gameState.getGameEntities().forEach(e -> e.start());
//        System.out.println("gameState" + gameState);
        this.startRuntime();
    }


    /**
     * Loop for controlling and updating the game state.
     */
    public void startRuntime() {
        while (!stopGame) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTickTime > TICK_INTERVAL) {
                controlState();
                lastTickTime = currentTime;
            }
        }
        if (stopGame) {
            this.stopGame();
        }
    }

    public void stopGame() {
        gameState.getGameEntities().forEach(e -> {
            e.interrupt();
        });
        gameState.getGameEntities().clear();
    }

    /**
     * Actualizes the game state by removing dead entities.
     */
    public void controlState() {
        List<GameEntity> toRemove = new ArrayList<>();
        var iterator = gameState.getGameEntities().iterator();
        while (iterator.hasNext()) {
            var entity = iterator.next();
            if(entity instanceof LivingEntity) {
                if (!((LivingEntity) entity).isEntityAlive()) {
                    toRemove.add(entity);
                }
            }
            if (entity instanceof Grass && ((Grass) entity).isEaten()) {
                toRemove.add(entity);
            }
        }

        if (!toRemove.isEmpty()) {
            gameState.getGameEntities().removeAll(toRemove);
            //this.printInfo();
        }
    }
    /**
     * Generates the game entities.
     * @return The list of generated game entities.
     */
    public List<GameEntity> generateEntities(int foxCount, int hareCount, int grassCount, int lakeCount, int hunterCount){
        var entities = new CopyOnWriteArrayList<GameEntity>();

        for (int i = 0; i <= foxCount; i++) {
            var fox = new Fox();
            generatePosition(fox);
            entities.add(fox);
        }

        for (int i = 0; i <= hareCount; i++) {
            var hare = new Hare();
            generatePosition(hare);
            entities.add(hare);
        }

        // add Lake
        // set random free position
        for (int i = 0; i <= lakeCount; i++) {
            var lake = new Lake();
            generatePosition(lake);
            entities.add(lake);
        }
        for (int i = 0; i <= grassCount; i++) {
            var grass = new Grass();
            generatePosition(grass);
            entities.add(grass);
        }
        for (int i = 0; i <= hunterCount; i++) {
            var hunter = new Hunter();
            generatePosition(hunter);
            entities.add(hunter);
        }

        for (int i = 0; i < 10; i++) {
            var tree = new Tree();
            generatePosition(tree);
            entities.add(tree);
        }

        return entities;
    }
/*    *//**
     * Prints information about the game state.
     *//*
    private void printInfo() {
        System.out.println("-----");
        System.out.println("Game state: " + this.gameState.getGameEntities());

        //System.out.println("Total grass game entities: " + this.gameState.getGameEntities().stream().filter(e -> e instanceof Grass).toList().size());
    }*/

    /**
     * Generates a random position for the given game entity from the list of available positions.
     *
     * @param entity The game entity.
     */
    public static void generatePosition(GameEntity entity) {
        if (positionIterator.hasNext()) {
            GamePosition position = positionIterator.next();
            if(entity instanceof Tree || entity instanceof Lake) {
                position.setHitboxSize(new Pair<>(50,50));
            }
            if(entity instanceof Bullet) {
                position.setHitboxSize(new Pair<>(5,5));
            }

            entity.setPosition(position);
        } else {
            throw new IllegalStateException("No more positions available");
        }

    }
}