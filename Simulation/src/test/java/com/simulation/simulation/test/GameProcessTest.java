package com.simulation.simulation.test;

import com.simulation.simulation.Game;
import com.simulation.simulation.GameBoard;
import com.simulation.simulation.GameState;
import com.simulation.simulation.model.GameEntity;
import com.simulation.simulation.model.game.entities.Fox;
import com.simulation.simulation.model.game.entities.Hare;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameProcessTest {

    private GameBoard gameBoard = new GameBoard(1000, 1000);
    private Game game = new Game(gameBoard);
    private GameState gameState = new GameState(gameBoard, new ArrayList<>());
    private Thread thread;
    private final List<GameEntity> entities = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        gameBoard = new GameBoard(1600, 1000);
        game = new Game(gameBoard);
        gameState = new GameState(gameBoard, entities);
        game.setGameState(gameState);
        this.thread = new Thread(() -> {
            game.startGame();
        });
        this.thread.start();
    }

    @AfterEach
    void tearDown() {
        this.thread.interrupt();
    }


    @Test
    void testEntityTick() throws InterruptedException {
        assertNotNull(gameState.getGameEntities());
        System.out.println(gameState.getGameEntities());
        assertTrue(gameState.getGameEntities().isEmpty());
        Fox fox = new Fox();
        var t = fox.getHunger();
        gameState.addEntities(fox);
        var foxInList = gameState.getGameEntities().stream().filter(e -> e == fox).findFirst();
        System.out.println(gameState.getGameEntities());
        assertTrue(foxInList.isPresent());

        // wait for 5 fox ticks
        Thread.sleep(fox.getTick() * 5);

        assertEquals(t - 5, fox.getHunger());

        fox.setHunger(6);
        Hare hare = new Hare();
        hare.setPosition(fox.getPosition());
        gameState.addEntities(hare);
        fox.setTarget(hare);


        // wait for 5 fox ticks
        Thread.sleep(fox.getTick());

        assertFalse(hare.isEntityAlive());
        assertEquals(fox.getHunger(), 200);

        assertTrue(fox.getFoxPriority() != "HUNGER");

        fox.setThirst(6);

        // wait for 5 fox ticks
        Thread.sleep(fox.getTick());

        assertTrue(fox.getFoxPriority() == "THIRST");


    }

}
