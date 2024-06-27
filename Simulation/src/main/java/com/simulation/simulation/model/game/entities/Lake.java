package com.simulation.simulation.model.game.entities;

import com.simulation.simulation.GameState;
import com.simulation.simulation.model.GameEntity;
import com.simulation.simulation.model.GamePosition;
/**
 * The `Lake` class represents a lake entity in the game world.
 * It does not have any specific behavior in the game world.
 */
public class Lake extends GameEntity {
    /**
     * Method called in each game tick to update the state of the lake.
     *
     * @param gameState The state of the game.
     */
    @Override
    public void tick(GameState gameState) {
        // Lakes do not have any specific behavior in each game tick

    }

}
