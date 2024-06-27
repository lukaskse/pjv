package com.simulation.simulation.test;

import com.simulation.simulation.model.LivingEntity;
import com.simulation.simulation.model.game.entities.Fox;
import com.simulation.simulation.GameBoard;
import com.simulation.simulation.model.GameEntity;
import com.simulation.simulation.GameState;
import com.simulation.simulation.Game;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class GameTest {

    private GameBoard gameBoard = new GameBoard(1000, 1000);
    private Game game = new Game(gameBoard);
    private GameState gameState = new GameState(gameBoard, new ArrayList<>());
    private Thread thread;
    private final List<GameEntity> entities = new ArrayList<>();
    private final Fox fox = new Fox();

    @BeforeEach
    void setUp() {
        gameBoard = new GameBoard(1600, 1000);
        game = new Game(gameBoard);
        entities.add(fox);
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
    void testGameInitialization() {
        assertNotNull(game);
        assertNotNull(gameBoard);
        assertNotNull(gameState);
    }

    @Test
    void testAddEntities() {
        assertNotNull(gameState.getGameEntities());
        assertFalse(gameState.getGameEntities().isEmpty());
        var foxInList = gameState.getGameEntities().stream().filter(e -> e == fox).findFirst();
        System.out.println(gameState.getGameEntities());
        assertTrue(foxInList.isPresent());

    }

    @Test
    void testControlState() throws InterruptedException {
        fox.die();
        assertFalse(fox.isEntityAlive() == true);
        List<GameEntity> toRemove = new ArrayList<>();
        for (GameEntity entity: gameState.getGameEntities()) {
            if (entity instanceof LivingEntity) {
                if (!((LivingEntity) entity).isEntityAlive()) {
                    System.out.println(entity + " is dead");
                    toRemove.add(entity);

                }
            }
        }
        gameState.getGameEntities().removeAll(toRemove);
        assertFalse(gameState.getGameEntities().contains(fox));

    }

    @Test
    void testGenerateEntities() {
        List<GameEntity> entities = game.generateEntities(20, 30, 30, 20, 20);
        assertNotNull(entities);
        assertFalse(entities.isEmpty());
        assertEquals(135, entities.size());
    }


}
