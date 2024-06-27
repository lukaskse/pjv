package com.simulation.simulation.test;

import com.simulation.simulation.Game;
import com.simulation.simulation.GameBoard;
import com.simulation.simulation.GameState;
import com.simulation.simulation.model.GameEntity;
import com.simulation.simulation.model.GamePosition;
import com.simulation.simulation.model.game.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;


import static com.simulation.simulation.model.LivingEntity.calculateDistance;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class AnimalTest {

    private Game game;

    private GameState gameState;
    private Thread thread;
    private List<GameEntity> entities = new ArrayList<>();
    private final Fox fox = new Fox();

    private final Fox femaleFox = new Fox();

    private final Hare hare = new Hare();


    @BeforeEach
    void setUp() {
        GameBoard gameBoard = new GameBoard(500, 500);
        game = new Game(gameBoard);
        entities.add(fox);
        fox.setGender("M");
        fox.setPosition(new GamePosition(100, 100));
        entities.add(hare);
        hare.setPosition(new GamePosition(150, 150));
        femaleFox.setGender("F");
        femaleFox.setPosition(new GamePosition(50, 50));
        entities.add(femaleFox);
        gameState = new GameState(gameBoard, entities);
        Game.setGameState(gameState);
        System.out.println(gameState.getGameEntities());
        /*this.thread = new Thread(() -> {
            game.startGame();
        });
        this.thread.start();*/
    }

    @Test
    void testFirstLiveCycle(){
        fox.tick(gameState);
        fox.setHunger(0);
        fox.setThirst(100);
        fox.tick(gameState);
        assertFalse(fox.isEntityAlive());

        hare.setHunger(200);
        hare.setThirst(0);
        hare.tick(gameState);
        assertFalse(hare.isEntityAlive());

    }

    @Test
    void testSecondLiveCycle(){

        assertTrue(hare.getHarePriority() == null);


        var prevHunger = fox.getHunger();
        System.out.println(fox.getHunger());

        fox.tick(gameState);
        System.out.println(fox.getHunger());

        if (hare.isEntityAlive()){
            assertTrue(fox.getHunger() < prevHunger);
            assertTrue(calculateDistance(fox, hare) > 30);
        }
        else {
            assertTrue(fox.getHunger() == 200);
        }

      //  Thread.sleep(5000);

        fox.setHunger(20);
        fox.setThirst(200);

        fox.setPriority(gameState);
        assertTrue(fox.getFoxPriority() == "HUNGER");
        assertTrue(fox.getTarget() == hare);

        fox.tick(gameState);


        game.controlState();

        if (calculateDistance(fox, hare) < 30){
            assertTrue(fox.getHunger() == 200);
            assertTrue(gameState.getGameEntities().contains(hare) == false);
        }
        else {
            assertTrue(fox.getHunger() < 200);
        }



    }

    @Test
    void testThirdLiveCycle() {

        assertTrue(hare.getHarePriority() == null);

        var prevHunger = fox.getHunger();


        fox.tick(gameState);


        if (hare.isEntityAlive()) {
            assertTrue(fox.getHunger() < prevHunger);
            System.out.println(calculateDistance(fox, hare));
            assertTrue(calculateDistance(fox, hare) > 31);

        } else {
            System.out.println(calculateDistance(fox, hare));
            assertTrue(fox.getHunger() == 200);

            assertTrue(fox.getTarget() == null);
            assertTrue(fox.getFoxPriority() == null);

            fox.tick(gameState);

        }

        if (fox.getFoxPriority() == null) {
            assertTrue(fox.getHunger() > 100);
            assertTrue(fox.getThirst() > 100);
            assertTrue(fox.getFoxPriority() == null);
            assertTrue(fox.getLakeTarget() == null);
        }
        ;

        Hunter hunter = new Hunter();

        hunter.setPosition(new GamePosition(200, 200));
        entities.add(hunter);

        gameState = new GameState(new GameBoard(500, 500), entities);

        Game.setGameState(gameState);

        hunter.tick(gameState);

        assertTrue(hunter.getHunterTarget() == fox);
    }

}
