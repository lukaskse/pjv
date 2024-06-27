package com.simulation.simulation.test;

import com.simulation.simulation.Game;
import com.simulation.simulation.GameBoard;
import com.simulation.simulation.GameState;
import com.simulation.simulation.model.GamePosition;
import com.simulation.simulation.model.LivingEntity;
import com.simulation.simulation.model.game.entities.Bullet;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class BulletTest {

    @Test
    public void testBulletConstructorAndTick() {
        // Create a mock GamePosition
        GamePosition mockPosition = Mockito.mock(GamePosition.class);


        // Create a mock LivingEntity
        LivingEntity mockTarget = Mockito.mock(LivingEntity.class);
        when(mockTarget.getPosition()).thenReturn(mockPosition);

        // Create a new Bullet with the mock GamePosition and mock LivingEntity
        Bullet bullet = new Bullet(mockPosition, mockTarget);

        when(mockTarget.getPosition()).thenReturn(mockPosition);
        when(mockPosition.getX()).thenReturn(100);
        when(mockPosition.getY()).thenReturn(100);
        mockTarget.setPosition(mockPosition);


        // Create a mock GameState
        GameState mockGameState = Mockito.mock(GameState.class);

        Game game = new Game(new GameBoard(1000, 1000));
        game.setGameState(mockGameState);


        // Call the tick method
        bullet.tick(mockGameState);

        // Verify that the isEntityAlive method was called on the mock LivingEntity
        verify(mockTarget, times(1)).isEntityAlive();
    }

    @Test
    public void testMoveTowards() {
        // Create a mock GamePosition
        GamePosition mockPosition = Mockito.mock(GamePosition.class);
        when(mockPosition.getX()).thenReturn(100);
        when(mockPosition.getY()).thenReturn(100);

        // Create a mock LivingEntity
        LivingEntity mockTarget = Mockito.mock(LivingEntity.class);
        when(mockTarget.getPosition()).thenReturn(mockPosition);

        // Create a new Bullet with the mock GamePosition and mock LivingEntity
        Bullet bullet = new Bullet(mockPosition, mockTarget);

        // Call the moveTowards method
        bullet.moveTowards(7);

        // Verify that the getX and getY methods were called on the mock GamePosition
        verify(mockPosition, times(5)).getX();
        verify(mockPosition, times(5)).getY();
    }

    @Test
    public void testSetDirection() {
        // Create a mock GamePosition for the bullet
        GamePosition mockPositionBullet = Mockito.mock(GamePosition.class);
        when(mockPositionBullet.getX()).thenReturn(100);
        when(mockPositionBullet.getY()).thenReturn(100);

        // Create a mock GamePosition for the target
        GamePosition mockPositionTarget = Mockito.mock(GamePosition.class);
        when(mockPositionTarget.getX()).thenReturn(200);
        when(mockPositionTarget.getY()).thenReturn(200);

        // Create a mock LivingEntity
        LivingEntity mockTarget = Mockito.mock(LivingEntity.class);
        when(mockTarget.getPosition()).thenReturn(mockPositionTarget);

        // Create a new Bullet with the mock GamePosition and mock LivingEntity
        Bullet bullet = new Bullet(mockPositionBullet, mockTarget);

        // Call the setDirection method
        bullet.setDirection();

        // Verify that the getX and getY methods were called on the mock GamePosition
        verify(mockPositionBullet, times(2)).getX();
        verify(mockPositionBullet, times(2)).getY();
        verify(mockPositionTarget, times(2)).getX();
        verify(mockPositionTarget, times(2)).getY();
    }


}
