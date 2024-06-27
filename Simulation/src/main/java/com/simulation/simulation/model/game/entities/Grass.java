package com.simulation.simulation.model.game.entities;

import com.simulation.simulation.Game;
import com.simulation.simulation.GameState;
import com.simulation.simulation.model.GameEntity;
import com.simulation.simulation.model.GamePosition;
import lombok.Getter;
import lombok.Setter;
/**
 * The `Grass` class represents a patch of grass in the game world.
 * It grows over time and can be eaten by hare.
 */
public class Grass extends GameEntity {
    /**
     * Number of ticks since last growth.
     */
    int tics = 0;
    /**
     * Total number of ticks since the grass entity was created.
     */
    int totalTics = 0;
    /**
     * Flag indicating whether the grass has been eaten.
     */
    @Getter
    @Setter
    public boolean isEaten = false;
    /**
     * Method called in each game tick to update the state of the grass.
     *
     * @param gameState The state of the game.
     *
     */
    @Override
    public void tick(GameState gameState) {
        this.tics++;
        this.totalTics++;
        if (this.tics >= 100) {
            this.grow();
            this.tics = 0;
        }

        if (this.totalTics >= 2000) {
            this.isEaten = true;
        }
    }
    /**
     * Method to make the grass grow, creating new grass entity nearby.
     */
    public void grow() {
        var grass = new Grass();
        grass.setPosition(new GamePosition(this.getPosition().getX() + 10, this.getPosition().getY() +10));
        Game.getGameState().addEntities(grass);
    }

}
