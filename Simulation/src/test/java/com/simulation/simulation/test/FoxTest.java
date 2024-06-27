package com.simulation.simulation.test;

import com.simulation.simulation.Game;
import com.simulation.simulation.GameBoard;
import com.simulation.simulation.GameState;

import com.simulation.simulation.model.GameEntity;
import com.simulation.simulation.model.game.entities.Fox;
import com.simulation.simulation.model.game.entities.Hare;
import com.simulation.simulation.model.game.entities.Lake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FoxTest {

    private Game game;

    private GameState gameState;
    private Thread thread;
    private List<GameEntity> entities = new ArrayList<>();
    private final Fox fox = new Fox();

    private final Fox femaleFox = new Fox();

    private final Hare hare = new Hare();


    @BeforeEach
    void setUp() {
        GameBoard gameBoard = new GameBoard(1000, 1000);
        game = new Game(gameBoard);
        entities.add(fox);
        entities.add(hare);
        entities.add(femaleFox);
        gameState = new GameState(gameBoard, entities);
        Game.setGameState(gameState);
        this.thread = new Thread(() -> {
            game.startGame();
        });
        this.thread.start();
    }

    @Test
    void foxInitialization() {
        assertNotNull(fox.getGender());
        assertTrue(fox.getHunger() >= 50 && fox.getHunger() <= 200);
        assertTrue(fox.getThirst() >= 70 && fox.getThirst() <= 200);
    }

    @Test
    void foxDiesWhenHungerAndThirstAreZero() {
        fox.setHunger(0);
        fox.setThirst(0);
        fox.tick(gameState);
        assertFalse(fox.isEntityAlive());
    }

    @Test
    void foxMovesTowardsTargetWhenHungerIsPriority() {
        fox.setThirst(200);
        fox.setMate(200);
        fox.setHunger(20);
        fox.setPriority(gameState);
        assertEquals("HUNGER", fox.getFoxPriority());
        System.out.println(fox.getTarget());
        assertEquals(hare, fox.getTarget());
    }

    @Test
    void foxMovesTowardsLakeWhenThirstIsPriority() {
        fox.setThirst(50);
        fox.setMate(200);
        fox.setHunger(200);
        fox.setPriority(gameState);
        assertEquals("THIRST", fox.getFoxPriority());
        assertNotNull(fox.getLakeTarget());
    }

    @Test
    void foxMovesTowardsMateWhenMateIsPriority() {
        femaleFox.setGender("F");
        fox.setGender("M");
        fox.setMate(10);
        femaleFox.setMate(10);
        fox.setHunger(200);
        fox.setThirst(200);
        femaleFox.setHunger(200);
        femaleFox.setThirst(200);
        fox.setPriority(gameState);
        femaleFox.setPriority(gameState);
        assertEquals("MATE", fox.getFoxPriority());
        assertNotNull(fox.getMateTarget());
        assertEquals(femaleFox, fox.getMateTarget());
    }

    @Test
    void foxEatsHareWhenInRange() {
        fox.setTarget(hare);
        fox.setHunger(50);
        fox.setThirst(200);
        fox.setMate(200);
        fox.tick(gameState);
        assertEquals(200, fox.getHunger());
        assertFalse(hare.isEntityAlive());
    }
}