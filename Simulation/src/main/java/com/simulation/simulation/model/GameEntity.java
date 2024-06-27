package com.simulation.simulation.model;

import com.simulation.simulation.Game;
import com.simulation.simulation.GameState;

import com.simulation.simulation.HelloApplication;
import lombok.Getter;
import lombok.Setter;


/**
 * The abstract class `GameEntity` represents a game entity that can be placed on the game board.
 * It contains an identifier for the entity and its position on the game board.
 * This class provides a template for the behavior of entities in the game world.
 */
public abstract class GameEntity extends Thread{
    /**
     * The interval (in milliseconds) between each tick.
     */
    private final long TICK_INTERVAL = 100;

    /**
     * Static counter to keep track of the total number of entities in the game.
     */
    public static int GLOBAL_ENTITY_COUNTER = 0;
    /**
     * The unique identifier of the entity.
     */
    public final int id;
    /**
     * The position of the entity on the game board.
     */
    @Getter
    @Setter
    protected GamePosition position = new GamePosition(0,0);

    /**
     * Constructor to initialize a game entity with a unique identifier and an associated thread.
     */
    public GameEntity() {
        GameEntity.GLOBAL_ENTITY_COUNTER++;
        this.id = GameEntity.GLOBAL_ENTITY_COUNTER;
    }

    /**
     * Abstract method to be implemented by subclasses to define the behavior of the entity in each game tick.
     *
     * @param gameState The state of the game.
     */
    public abstract void tick(GameState gameState);

    @Override
    public void run() {
        while(!Game.isStopGame()) {
            this.tick(Game.getGameState());
            try {
                Thread.sleep(getTick());
            } catch (InterruptedException e) {
                System.out.print("Thread interrupted");
            }
        }
    }

    public void pause() throws InterruptedException {
        this.sleep(50000);
    }

    public void unpause() {
        this.notify();
    }

    // methods that checks if hitbox collides with x and y


    /**
     * Determines the tick interval for the associated entity based on its type.
     *
     * @return The tick interval for the entity.
     */
    public long getTick() {
        return TICK_INTERVAL * 3;
    }

}


