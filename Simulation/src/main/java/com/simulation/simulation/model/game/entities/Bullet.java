package com.simulation.simulation.model.game.entities;

import com.simulation.simulation.Game;
import com.simulation.simulation.GameState;
import com.simulation.simulation.model.GameEntity;
import com.simulation.simulation.model.GamePosition;
import com.simulation.simulation.model.LivingEntity;
import lombok.Getter;
import lombok.Setter;


/**
 * The `Bullet` class represents a bullet entity in the game world.
 * It contains information about the speed, direction, and target of the bullet.
 * This class implements the behavior of a bullet in the game world.
 */
public class Bullet extends GameEntity {

    @Getter
    @Setter
    private int speed;
    @Getter
    @Setter
    private int directionX;
    @Getter
    @Setter
    private int directionY;

    @Getter
    @Setter
    private LivingEntity target;

    @Getter
    @Setter
    private int timeExist = 0;

    /**
     * Constructor to initialize a bullet with a position and a target entity.
     *
     * @param position The position of the bullet on the game board.
     * @param target   The target entity of the bullet.
     */

    public Bullet(GamePosition position, LivingEntity target) {
        this.setPosition(position);
        this.target = target;
        this.speed = 7;
        setDirection();// Set bullet speed
    }

    /**
     * Updates the state of the bullet in each game tick.
     *
     * @param gameState The state of the game.
     */

    @Override
    public void tick(GameState gameState) {
        if (target == null || !target.isEntityAlive() || !Game.getGameState().getGameEntities().contains(target)){
            // Remove the bullet if it has no target or the target is dead
            Game.getGameState().getGameEntities().remove(this);
            return;
        }
        timeExist++;
        if (timeExist > 25) {
            Game.getGameState().getGameEntities().remove(this);
            return;
        }
        if(this.getPosition().getX() > Game.getGameState().getGameBoard().getWidth() || this.getPosition().getX() < 0 || this.getPosition().getY() > Game.getGameState().getGameBoard().getHeight() || this.getPosition().getY() < 0){
            Game.getGameState().getGameEntities().remove(this);
            return;
        }
        moveTowards(speed);


        // Check collision with the target
        for (GameEntity entity : Game.getGameState().getGameEntities()) {
            if (!(entity instanceof Fox)){
                continue;
            }
            if (this.getPosition().isHitboxColliding(entity.getPosition()) ) {
                ((LivingEntity) entity).setEntityAlive(false);
                Game.getGameState().getGameEntities().remove(this);
            }
        }

    }

    /**
     * Moves the bullet towards its target at a given speed.
     *
     * @param speed The speed at which the bullet moves.
     */

    public void moveTowards(int speed) {
        this.setPosition(new GamePosition(this.position.getX() + directionX * speed, this.position.getY() + directionY * speed));
    }

    /**
     * Sets the direction of the bullet based on the position of the target entity.
     */
    public void setDirection(){
        GamePosition currentPosition = this.getPosition();
        GamePosition targetPosition = target.getPosition();

        if (currentPosition.getX() < targetPosition.getX()) {
            directionX = 1;
        } else if (currentPosition.getX() > targetPosition.getX()) {
            directionX = -1;
        } else {
            directionX = 0;
        }
        if (currentPosition.getY() < targetPosition.getY()) {
            directionY = 1;
        } else if (currentPosition.getY() > targetPosition.getY()) {
            directionY = -1;
        } else {
            directionY = 0;
        }
    }
}

