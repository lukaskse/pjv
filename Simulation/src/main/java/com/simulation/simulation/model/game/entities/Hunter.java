package com.simulation.simulation.model.game.entities;

import com.simulation.simulation.Game;
import com.simulation.simulation.GameState;
import com.simulation.simulation.model.GameEntity;
import com.simulation.simulation.model.GamePosition;
import com.simulation.simulation.model.LivingEntity;


/**
 * The `Hunter` class represents a hunter entity in the game world.
 * It contains information about the target and the last shot time of the hunter.
 * This class implements the behavior of a hunter in the game world.
 */
public class Hunter extends LivingEntity {



    private LivingEntity target;

    public Fox getHunterTarget(){
        return (Fox) target;
    }
    private long lastShotTime;
    private static final long SHOOT_INTERVAL = 6000;

    /**
     * Constructor to initialize a hunter.
     */
    public Hunter() {
        this.speed = 4;
        this.lastShotTime = 0;
    }

    /**
     * Updates the state of the hunter in each game tick.
     *
     * @param gameState The state of the game.
     */

    @Override
    public void tick(GameState gameState) {
        this.move(speed);
        this.setTarget(gameState);
        if (this.getPosition().getX() - target.getPosition().getX() < 100 && this.getPosition().getY() - target.getPosition().getY() < 100) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime >= SHOOT_INTERVAL) {
                this.shoot(gameState);
                lastShotTime = currentTime;
            }
            }

    }

    /**
     * Sets the target of the hunter to the nearest fox.
     *
     * @param gameState The state of the game.
     */
    public void setTarget(GameState gameState) {
        double loopUpRadius = Double.MAX_VALUE;
        LivingEntity nearestTarget = null;
        if (this.target == null) {
            for (GameEntity entity : gameState.getGameEntities()) {
                if (entity instanceof Fox) {
                    double distance = calculateDistance(this, entity);
                    if (distance < loopUpRadius) {
                        nearestTarget = (LivingEntity) entity;
                        loopUpRadius = distance;
                    }
                }
            }

            if (nearestTarget != null) {
                this.target = nearestTarget;
                target.hunter = this;

            }
        } else {
            double currentTargetDistance = calculateDistance(this, this.target);
            for (GameEntity entity : Game.getGameState().getGameEntities()) {
                if (entity instanceof Fox) {
                    double distance = calculateDistance(this, entity);
                    if (distance < loopUpRadius && distance < currentTargetDistance) {
                        nearestTarget = (Fox) entity;
                        loopUpRadius = distance;
                    }
                }
                if (nearestTarget != null) {
                    target.hunter = null;
                    this.target = nearestTarget;
                    target.hunter = this;
                }
            }
        }
    }

    /**
     * Method to make the hunter shoot a bullet at the target.
     *
     * @param gameState The state of the game.
     */
    public void shoot(GameState gameState) {
            GamePosition bulletPosition = new GamePosition(this.getPosition().getX(), this.getPosition().getY());
            Bullet bullet = new Bullet(bulletPosition, target);
            gameState.getGameEntities().add(bullet);
            bullet.start();
        }
    }



