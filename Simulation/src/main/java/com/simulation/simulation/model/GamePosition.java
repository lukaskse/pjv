package com.simulation.simulation.model;

import com.simulation.simulation.Game;
import com.simulation.simulation.model.game.entities.Tree;
import kotlin.Pair;
import lombok.*;
/**
 * The `GamePosition` class represents a position on the game board with specified x and y coordinates.
 * It contains methods for getting and setting coordinates, as well as checking if a position is occupied or seen by entities.
 */
@ToString
public class GamePosition {

    @Getter
    @Setter
    private Pair<Integer, Integer> hitboxSize = new Pair<>(20, 20);
    /**
     * The x-coordinate of the position.
     */
    @Getter
    @Setter
    int x;
    /**
     * The y-coordinate of the position.
     */
    @Getter
    @Setter
    int y;

    /**
     * Constructor to initialize a game position with specified x and y coordinates.
     *
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     */
    public GamePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
        * Checks if the position is occupied based on the HitBoxSize of entities.
        * @param other The position to check for collision.
        * @return True if the position is occupied by another entity, false otherwise.
     */
    public boolean isHitboxColliding(GamePosition other) {
        // hitbox is in the center of x and y
        int x1 = this.x;
        int y1 = this.y;
        int x2 = other.x;
        int y2 = other.y;

        return Math.abs(x1 - x2) <= Math.max(other.hitboxSize.getFirst(), this.hitboxSize.getFirst()) &&
                Math.max(other.hitboxSize.getSecond(), this.hitboxSize.getSecond()) >= Math.abs(y1 - y2);
    }

}
