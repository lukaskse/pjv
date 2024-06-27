package com.simulation.simulation.model.game.entities;

import com.simulation.simulation.Game;
import com.simulation.simulation.GameState;
import com.simulation.simulation.model.GameEntity;
import com.simulation.simulation.model.GamePosition;
import com.simulation.simulation.model.LivingEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Random;
/**
 * The `Fox` class represents a fox entity in the game world.
 * It contains information about the gender, hunger, thirst, mating status, and priority of the fox.
 * This class implements the behavior of a fox in the game world.
 */
@ToString
public class Fox extends LivingEntity {
    /**
     * Gender of the fox ('M' for male, 'F' for female).
     */
    @Getter
    @Setter
    private String gender;
    /**
     * Hunger level of the fox (ranging from 0 to 100).
     */
    @Getter
    @Setter
    private int hunger = Math.max(50, Math.min(200, new Random().nextInt(200)));
    /**
     * Thirst level of the fox (ranging from 0 to 100).
     */

    @Getter
    @Setter
    private int thirst = Math.max(70, Math.min(200, new Random().nextInt(200)));
    /**
     * Mating readiness level of the fox (ranging from 0 to 100).
     */
    @Getter
    @Setter
    private int mate = 100;

    /**
     * Indicates whether the fox is new.
     */
    @Getter
    @Setter
    public boolean isNew = false;

    /**
     * Priority of the fox's behavior.
     */
    public String getFoxPriority() {
        return priority;
    }

    private String priority = null;


    /**
     * Target hare for hunting.
     */
    @ToString.Exclude
    @Getter
    @Setter
    private Hare target = null;
    /**
     * Target fox for mating.
     */
    @ToString.Exclude
    @Getter
    private Fox mateTarget = null;
    /**
     * Target lake for quenching thirst.
     */
    @ToString.Exclude
    @Getter
    private Lake lakeTarget = null;

    /**
     * Constructor to initialize a fox with a random gender and speed 5.
     */
    public Fox() {
        Random random = new Random();
        String[] genders = {"M", "F"};
        int index = random.nextInt(genders.length);
        this.gender = genders[index];
        this.speed = 5;

    }

    /**
     * Method to perform actions of the fox in each game tick.
     *
     * @param gameState The state of the game in which the fox exists.
     */
    @Override
    public void tick(GameState gameState) {
        this.hunger -= 1;
        this.thirst -= 1;
        this.mate -= 1;

        if (this.hunger <= 0 || this.thirst <= 0) {
            die();
            return;
        }
        if (this.priority == null) {
            this.setPriority(gameState);
        }

        if (this.target != null) {
            this.moveTowards(this.target, this.speed);
            if (calculateDistance(this, this.target) < 30) {
                this.eat();

            }
        } else if (this.mateTarget != null) {
            this.moveTowards(this.mateTarget, this.speed);
            this.mateTarget.moveTowards(this, this.speed);
            if (calculateDistance(this, this.mateTarget) < 30) {
//                System.out.println("FOX " + this.id + " is mating with FOX " + this.mateTarget.id);
                this.mate = 100;
                this.mateTarget.mate = 100;
                this.mateTarget.priority = null;
                this.mateTarget.mateTarget = null;
                this.mateTarget = null;
                this.priority = null;
                this.born();
            }
        } else if (this.lakeTarget != null) {
            this.moveTowards(this.lakeTarget, this.speed);
            if (calculateDistance(this, this.lakeTarget) < 60) {
       //         System.out.println("FOX " + this.id + " is drinking water");
                this.thirst = 200;
                this.lakeTarget = null;
                this.priority = null;
                this.setPriority(Game.getGameState());
            }
        } else {
            this.randomMove(this.speed);
        }
    }
    /**
     * Method to set the priority behavior of the fox based on its current state.
     *
     * @param gameState The state of the game in which the fox exists.
     */
    public void setPriority(GameState gameState) {
        if (this.hunger < this.thirst && this.hunger < 100) {
            this.priority = "HUNGER";
        } else if (this.thirst < 100) {
            this.priority = "THIRST";
        } else if (this.mate < 30) {
            this.priority = "MATE";
        }

        if (this.priority == null) {
            return;
        }

        switch (this.priority) {
            case "HUNGER":
                this.executeHunger();
                break;
            case "THIRST":
                this.executeThirst();
                break;
            case "MATE":
                this.executeMate();
                break;
        }
    }

    /**
     * Method to execute the hunger behavior of the fox.
     */
    private void executeHunger() {
        Hare nearestHare = null;
        double loopUpRadius = Double.MAX_VALUE;

        if (this.target == null) {
            for (GameEntity entity : Game.getGameState().getGameEntities()) {
                if (entity instanceof Hare) {
                    double distance = calculateDistance(this, entity);
                    if (distance < loopUpRadius) {
                        nearestHare = (Hare) entity;
                        loopUpRadius = distance;
                    }
                }

            }

            if (nearestHare != null) {
                nearestHare.hunter = this;
                this.target = nearestHare;
            }

        } else {

            // TODO: add switch target logic
            // if there is a closer target switch to it
            // if target is dead switch to another target
            double currentTargetDistance = calculateDistance(this, this.target);
            for (GameEntity entity : Game.getGameState().getGameEntities()) {
                if (entity instanceof Hare) {
                    double distance = calculateDistance(this, entity);

                    if (distance < loopUpRadius && distance < currentTargetDistance) {
                        nearestHare = (Hare) entity;
                        loopUpRadius = distance;
                    }
            }

            if (nearestHare != null) {
                this.target.hunter = null;
                this.target = nearestHare;
                nearestHare.hunter = this;

            }


            // find new target


        }
        }

    }

    /**
     * Method to execute the mating behavior of the fox.
     */
    private void executeMate(){
        Fox nearestFox = null;
        double loopUpRadius = Double.MAX_VALUE;

        if (this.mateTarget == null) {
            for (GameEntity entity : Game.getGameState().getGameEntities()) {
                if (entity instanceof Fox && !((Fox) entity).getGender().equals(this.gender)){
                    double distance = calculateDistance(this, entity);
                    if (distance < loopUpRadius) {
                        nearestFox = (Fox) entity;
                        loopUpRadius = distance;
                    }
                }

            }

            if (nearestFox != null) {
                nearestFox.mateTarget = this;
                this.mateTarget = nearestFox;

            }
        }
    }

    /**
     * Method to execute the thirst behavior of the fox.
     */

    public void eat() {
//        System.out.println("FOX " + this.id + " is eating Hare " + target.id);
        this.hunger = 200;
        var newPosition = this.target.getPosition();
        target.die();
        this.target.hunter = null;
        this.target = null;
        this.priority = null;
        this.position = newPosition;
        this.setPriority(Game.getGameState());
    }

    /**
     * Method to execute the mating behavior of the fox.
     */
    public void born() {
        int x = this.position.getX() + 20;
        int y = this.position.getY() + 20;
        var newX = Math.max(0, Math.min(x, Game.getGameState().getGameBoard().getWidth() - 1));
        var newY = Math.max(0, Math.min(y, Game.getGameState().getGameBoard().getHeight() - 1));
        GamePosition setPosition = new GamePosition(newX, newY);
        if (this.isOccupied(setPosition)) {
            this.randomMove(this.speed);
            return;
        }

            this.mateTarget = null;
            this.priority = null;
            this.mate = 100;
            var fox = new Fox();
            fox.isNew = true;
            fox.setPosition(setPosition);
            Game.getGameState().addEntities(fox);

            this.setPriority(Game.getGameState());

    }

    /**
     * Method to execute the thirst behavior of the fox.
     */
    public void executeThirst() {
        //find nearest lake
        Lake nearestLake = null;
        double loopUpRadius = Double.MAX_VALUE;
        if (this.mateTarget == null) {
            for (GameEntity entity : Game.getGameState().getGameEntities()) {
                if (entity instanceof Lake){
                    double distance = calculateDistance(this, entity);
                    if (distance < loopUpRadius) {
                        nearestLake = (Lake) entity;
                        loopUpRadius = distance;
                    }
                }
            }

            if (nearestLake != null) {
                this.lakeTarget= nearestLake;

            }
        }
    }

    /**
     * Method to unset the entity and target entity of the fox on death.
     */
    @Override
    protected void onDeath() {
        if(this.target != null) {
            this.target.hunter = null;
            this.mateTarget = null;
            this.lakeTarget = null;

            this.target = null;
            this.hunter = null;
        }
    }

}
