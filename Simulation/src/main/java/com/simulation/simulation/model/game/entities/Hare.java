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
 * The `Hare` class represents a hare entity in the game world.
 * It contains information about the gender, hunger, thirst, mating status, and priority of the hare.
 * This class implements the behavior of a hare in the game world.
 */
@ToString
public class Hare extends LivingEntity {
    /**
     * Gender of the hare ('M' for male, 'F' for female).
     */
    @Getter
    @Setter
    private String gender;
    /**
     * Hunger level of the hare (ranging from 0 to 100).
     */
    @Getter
    @Setter
    private int hunger = Math.max(50, Math.min(200, new Random().nextInt(200)));
    /**
     * Thirst level of the hare (ranging from 0 to 100).
     */
    @Getter
    @Setter
    private int thirst = Math.max(70, Math.min(200, new Random().nextInt(200)));
    /**
     * Mating readiness level of the hare (ranging from 0 to 100).
     */
    @Getter
    @Setter
    private int mate = 100;
    /**
     * Priority of the hare's behavior.
     */

    private String priority = null;

    public String getHarePriority() {
        return priority;
    }
    /**
     * Target grass for eating.
     */
    @ToString.Exclude
    private Grass grassTarget = null;
    /**
     * Target lake for drinking.
     */
    @ToString.Exclude
    private Lake lakeTarget = null;
    /**
     * Target hare for mating.
     */
    @ToString.Exclude
    private Hare mateTarget = null;

    @Getter
    @Setter
    public boolean isNew = false;
    /**
     * Constructor to initialize a hare with a random gender and speed.
     */
    public Hare() {
        Random random = new Random();
        String[] genders = {"M", "F"};
        int index = random.nextInt(genders.length);
        this.gender = genders[index];
        this.speed = 3;
    }
    /**
     * Method to perform actions of the hare in each game tick.
     *
     * @param gameState The state of the game in which the hare exists.
     */
    @Override
    public void tick(GameState gameState) {
        // Move the hare
        this.move(this.speed);

        // Decrease hunger, thirst, and mating readiness levels
        this.hunger -= 1;
        this.thirst -= 1;
        this.mate -= 1;

        // Check if the hare should die due to hunger, thirst, or being hunted
        if (this.thirst <= 0 || this.hunger <= 0) {
            this.die();
            return;
        }

        // Set priority behavior based on current state
        this.setPriority(gameState);

        // Execute behavior based on priority
        if (this.grassTarget != null) {// Move towards target grass and eat if close enough
            this.moveTowards(this.grassTarget, this.speed);
            if(this.grassTarget.isEaten()){
                this.grassTarget = null;
                this.priority = null;
                this.setPriority(Game.getGameState());
            }
            if (calculateDistance(this, this.grassTarget) < 30) {
                Game.getGameState().getGameEntities().remove(this.grassTarget);
             //   System.out.println("Hare " + this.id + " is eating grass");
                this.eat();
            }
        }
        if (this.mateTarget != null) {
            this.moveTowards(this.mateTarget, this.speed);
            // Move towards mate hare and mate if close enough
            //this.mateTarget.moveTowards(this, this.speed);
            if (calculateDistance(this, this.mateTarget) < 30) {
                this.mate = 100;
                this.mateTarget.mate = 100;
                this.mateTarget.priority = null;
                this.mateTarget.mateTarget = null;
                this.mateTarget = null;
                this.priority = null;
                this.born();
            }
        } else if (this.lakeTarget != null) {
            // Move towards lake to quench thirst
            this.moveTowards(this.lakeTarget, this.speed);
            if (calculateDistance(this, this.lakeTarget) < 60) {
                this.thirst = 200;
                this.lakeTarget = null;
                this.priority = null;
            }
        } else {
            // Move randomly if no priority behavior is set
            this.move(this.speed);
        }

    }
    /**
     * Method to set the priority behavior of the hare based on its current state.
     *
     * @param gameState The state of the game in which the hare exists.
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
     * Method to execute the priority behavior of the hare based on its current state.
     */
    private void executeHunger() {
        Grass nearestGrass = null;
        double loopUpRadius = Double.MAX_VALUE;

        if (this.grassTarget == null) {
            for (GameEntity entity : Game.getGameState().getGameEntities()) {
                if (entity instanceof Grass && !((Grass) entity).isEaten()){
                        double distance = calculateDistance(this, entity);
                        if (distance < loopUpRadius) {
                            nearestGrass = (Grass) entity;
                            loopUpRadius = distance;
                        }
                }

            }

            if (nearestGrass != null) {
                this.grassTarget = nearestGrass;
            }
        }




    }

    /**
     * Method to execute the mating behavior of the hare.
     */
    private void executeMate(){
        Hare nearestHare = null;
        double loopUpRadius = Double.MAX_VALUE;

        if (this.mateTarget == null) {
            for (GameEntity entity : Game.getGameState().getGameEntities()) {
                if (entity instanceof Hare && !((Hare) entity).getGender().equals(this.gender)){
                    double distance = calculateDistance(this, entity);
                    if (distance < loopUpRadius) {
                        nearestHare = (Hare) entity;
                        loopUpRadius = distance;
                    }
                }

            }

            if (nearestHare != null) {
                nearestHare.mateTarget = this;
                this.mateTarget = nearestHare;

            }
        }
    }
     /**
     * Method to execute the hunger behavior of the hare and eat the target grass.
     */
    public void eat() {
        var newPosition = new GamePosition(this.position.getX(), this.position.getY());
        grassTarget.setEaten(true);
        this.setPosition(newPosition);
        this.grassTarget = null;
        this.hunger = 200;
        this.priority = null;
        this.setPriority(Game.getGameState());
    }
    /**
     * Method to execute the mating behavior of the hare and give birth to a new hare.
     */
    public void born() {

        int x = this.position.getX() + 20;
        int y = this.position.getY() + 20;
        GamePosition setPosition = new GamePosition(x, y);
        if(this.isOccupied(setPosition)){
            this.randomMove(this.speed);
            return;
        }
            this.mate= 100;
            this.mateTarget = null;
            this.priority = null;
            var hare = new Hare();
            hare.isNew = true;
            hare.setPosition(setPosition);
            Game.getGameState().addEntities(hare);


    }
/**
     * Method to execute the thirst behavior of the hare and drink from the target lake.
     */
    public void executeThirst() {
        //find nearest lake
        Lake nearestLake = null;
        double loopUpRadius = Double.MAX_VALUE;
        if (this.lakeTarget == null) {
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
     * Method to unset entity and target entity of the hare on death.
     */
    @Override
    protected void onDeath() {
        this.setEntityAlive(false);
        this.grassTarget = null;
        this.lakeTarget = null;
        this.mateTarget = null;
    }
}
