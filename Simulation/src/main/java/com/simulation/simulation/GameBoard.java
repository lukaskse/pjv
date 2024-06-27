package com.simulation.simulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * The `GameBoard` class represents the game board with a specific width and height.
 * It contains information about the dimensions of the game board.
 */

/**
 * Default constructor for the GameBoard class.
 */
@AllArgsConstructor
@ToString
public class GameBoard {
    /**
     * The width of the game board.
     */
    @Getter
    @Setter
    private int width;
    /**
     * The height of the game board.
     */
    @Getter
    @Setter
    private int height;
}
