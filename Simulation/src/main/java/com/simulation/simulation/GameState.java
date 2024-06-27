package com.simulation.simulation;

import com.simulation.simulation.model.GameEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
/**
 * The `GameState` class represents the state of the game, which includes the game board and a list of game entities.
 * It provides methods for adding and removing game entities from the list.
 */
@AllArgsConstructor
@ToString
public class GameState {
    /**
     * The game board representing the current state of the game.
     */
    @Getter
    private GameBoard gameBoard;
    /**
     * The list of game entities in the current state of the game.
     */
    @Getter
    @Setter
    private List<GameEntity> gameEntities;


    /**
     * Adds a game entity to the list of game entities in a synchronized manner.
     * The entity is also started.
     * @param entity The game entity to be added.
     */
    public synchronized void addEntities(GameEntity entity) {
       this.gameEntities.add(entity);
       entity.start();
    }
}
