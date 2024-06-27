package com.simulation.simulation.model;

import com.simulation.simulation.Game;
import com.simulation.simulation.model.game.entities.Bullet;
import com.simulation.simulation.model.game.entities.Lake;
import com.simulation.simulation.model.game.entities.Tree;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Random;
/**
 * The `LivingEntity` abstract class represents a living entity in the game world.
 * It contains information about its vitality, movement speed, and methods for movement and distance calculation.
 */
@ToString
public abstract class LivingEntity extends GameEntity {
    private final Random random = new Random();
    /**
     * Indicates whether the entity is alive.
     */
    @Getter
    @Setter
    private boolean isEntityAlive = true;
    /**
     * The entity hunting this entity.
     */
    @ToString.Exclude
    public GameEntity hunter = null;
    /**
     * The speed of movement for the entity.
     */
    @Getter
    @Setter
    public int speed = 0;
    /**
     * Moves the entity with certain direction.
     *
     * @param speed The speed of movement.
     */
    public void move(int speed) {
        if (hunter == null) {
            this.randomMove(speed);
        } else {
            this.moveAway(hunter, speed);
        }
    }
    /**
     * Moves the entity randomly.
     *
     * @param speed The speed of movement.
     */
    public void randomMove(int speed) {
        var nextCords = this.getNextGamePosition(speed);
        var tries =0;

        while(this.isOccupied(nextCords)) {
            nextCords = this.getNextGamePosition(speed);
            if(tries > 4){
                return;
            } else {
                tries++;
            }
        }

        // Ensure the new position is within the bounds
        var newX = Math.max(0, Math.min(nextCords.getX(), Game.getGameState().getGameBoard().getWidth() - 1));
        var newY = Math.max(0, Math.min(nextCords.getY(), Game.getGameState().getGameBoard().getHeight()  - 1));
        GamePosition setPosition = new GamePosition(newX, newY);
        if (this.isOccupied(setPosition)) {
            this.randomMove(speed);
        }
        else {
            this.position.setX(newX);
            this.position.setY(newY);
        }
    }
    /**
     * Calculates the distance between two entities.
     *
     * @param entity1 The first entity.
     * @param entity2 The second entity.
     * @return The distance between the entities.
     */
    public static double calculateDistance(LivingEntity entity1, GameEntity entity2) {
        return Math.sqrt(Math.pow(entity1.getPosition().getX() - entity2.getPosition().getX(), 2) + Math.pow(entity1.getPosition().getY() - entity2.getPosition().getY(), 2));
    }
    /**
     * Moves the entity towards another entity.
     *
     * @param entity The entity to move towards.
     * @param speed  The speed of movement.
     */
    public void moveTowards(GameEntity entity, int speed) {
        var nextCords = this.getNextMoveTorwadsPosition(entity, speed);
        var tries =0;
        while (this.isOccupied(nextCords)) {
            this.randomMove(speed);
            nextCords = this.getNextMoveTorwadsPosition(entity, speed);
            if(tries > 4){
                return;
            } else {
                tries++;
            }
        }

        var newX = Math.max(0, Math.min(nextCords.getX(), Game.getGameState().getGameBoard().getWidth() - 1));
        var newY = Math.max(0, Math.min(nextCords.getY(), Game.getGameState().getGameBoard().getHeight() - 1));
        GamePosition setPosition = new GamePosition(newX, newY);
        if (this.isOccupied(setPosition)) {
            this.moveTowards(entity,speed);
        }

        else {
            this.position.setX(newX);
            this.position.setY(newY);
        }

    }
    /**
     * Moves the entity away from another entity.
     *
     * @param entity The entity to move away from.
     * @param speed  The speed of movement.
     */
    public void moveAway(GameEntity entity, int speed) {
        var nextCords = this.getNextMoveAwayPosition(entity, speed);
        var tries =0;
        while (this.isOccupied(nextCords)) {
            this.randomMove(speed);
            nextCords = this.getNextMoveAwayPosition(entity, speed);
            if(tries > 4){
                return;
            } else {
                tries++;
            }
        }

        var newX = Math.max(0, Math.min(nextCords.getX(), Game.getGameState().getGameBoard().getWidth() - 1));
        var newY = Math.max(0, Math.min(nextCords.getY(), Game.getGameState().getGameBoard().getHeight() - 1));
        GamePosition setPosition = new GamePosition(newX, newY);
        if (this.isOccupied(setPosition)) {
            this.moveAway(entity,speed);
        }

        else {
            this.position.setX(newX);
            this.position.setY(newY);
        }
    }
    /**
     * Marks the entity as dead.
     * The entity will be removed from the game in the next game tick.
     * The onDeath method is called to execute the behavior of the entity when it dies.
     */
    public void die() {
        this.isEntityAlive = false;
        this.onDeath();
    }

    /**
     * Checks if the position is occupied by another entity.
     *
     * @param position The position to check.
     * @return True if the position is occupied, false otherwise.
     */
    public boolean isOccupied(GamePosition position) {
        for (GameEntity entity : Game.getGameState().getGameEntities()) {
            if(entity == this || entity instanceof Bullet){
                continue;
            }


            if (entity instanceof LivingEntity && ((LivingEntity) entity).isEntityAlive() && entity.getPosition().isHitboxColliding(position)) {
                return true;
            }
            else if (entity instanceof Lake && entity.getPosition().isHitboxColliding(position)) {
                return true;
            }
            else if (entity instanceof Tree && entity.getPosition().isHitboxColliding(position)) {
                return true;
            }
        }
        return false;
    }

    /** Returns the next game position for the entity to move to.
     *
     * @param speed The speed of movement.
     * @return The next game position.
     */
    private GamePosition getNextGamePosition(int speed){
        int newX = this.position.getX() + (random.nextInt(3) - 1) * speed; // Move left, right, or stay
        int newY = this.position.getY() + (random.nextInt(3) - 1) * speed;
        return new GamePosition(newX, newY);
    }

    /** Returns the next game position for the entity to move towards.
     *
     * @param entity The entity to move towards.
     * @param speed The speed of movement.
     * @return The next game position.
     */
    private GamePosition getNextMoveTorwadsPosition(GameEntity entity, int speed){
        int x = this.position.getX();
        int y = this.position.getY();
        int targetX = entity.getPosition().getX();
        int targetY = entity.getPosition().getY();
        if (x < targetX) {
            x = x + speed;
        } else if (x > targetX) {
            x = x - speed;
        }

        if (y < targetY) {
            y = y + speed;
        } else if (y > targetY) {
            y = y - speed;
        }
        return new GamePosition(x, y);
    }

    /** Returns the next game position for the entity to move away from.
     *
     * @param entity The entity to move away from.
     * @param speed The speed of movement.
     * @return The next game position.
     */
    private GamePosition getNextMoveAwayPosition(GameEntity entity, int speed){
        int x = this.position.getX();
        int y = this.position.getY();
        int targetX = entity.getPosition().getX();
        int targetY = entity.getPosition().getY();
        if (x < targetX) {
            x = x - speed;
        } else if (x > targetX) {
            x = x + speed;
        }

        if (y < targetY) {
            y = y - speed;
        } else if (y > targetY) {
            y = y + speed;
        }
        return new GamePosition(x, y);
    }
    /**
     * Method to execute the behavior of the entity when it dies.
     */
    protected void onDeath() {

    }


}
