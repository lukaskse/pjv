package com.simulation.simulation;

import com.simulation.simulation.model.GameEntity;

import com.simulation.simulation.model.game.entities.Fox;
import com.simulation.simulation.model.game.entities.Hare;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;



import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The `HelloApplication` class represents the main application class for the simulation.
 * It initializes the graphical user interface and handles the drawing of the game state.
 * It also provides a method for starting the game loop in a new thread.
 */
public class HelloApplication extends Application {

    public static Game game;
    public static Thread gameThread;
    static GameBoard gameBoard = new GameBoard(1000, 1000);
    public static final Logger logger = Logger.getLogger(HelloApplication.class.getName());
    private static final Map<Class<? extends GameEntity>, Integer> entityCountMap = new HashMap<>();

    private static Image foxImage;
    private static Image hareImage;
    private static Image hunterImage;
    private static Image treeImage;
    private static Image grassImage;
    private static Image lakeImage;

    private static long lastLogTime = System.currentTimeMillis();
    private static long logInterval = 5000;

    @Setter
    public static int foxCount = 0;
    public static int hareCount = 0;
    public static int grassCount = 0;
    public static int lakeCount = 0;
    public static int hunterCount = 0;

    public static int totalCount = 0;

    private static TextField textField;

    static GraphicsContext gc;
     /**
     * The main method serves as the entry point for the application.
     * It initializes the game instance and starts the game loop in a new thread.
      * It also launches the JavaFX application.
      * Set the enableLogging variable to true to enable logging.
      * @param args The arguments.
     */
    public static void main(String[] args) {
        launch();
    }

    public static void startGame() {
        if(game != null) {
            game.stopGame();
        }

        if(gameThread != null) {
            gameThread.interrupt();
            gameThread = null;
        }

        game = new Game(gameBoard);
        Game.stopGame = false;
        gameThread = new Thread(() -> {
            game.startGame();
        });
        gameThread.start();
    }
    /**
     * The `start` method initializes the graphical user interface.
     *
     * @param stage The JavaFX stage where the scene is displayed.
     */
    @Override
    public void start(Stage stage){
        foxImage = new Image(getClass().getResourceAsStream("/imgs/fox.png"));
        hareImage = new Image(getClass().getResourceAsStream("/imgs/hare.png"));
        hunterImage = new Image(getClass().getResourceAsStream("/imgs/hunter.png"));
        treeImage = new Image(getClass().getResourceAsStream("/imgs/tree.png"));
        grassImage = new Image(getClass().getResourceAsStream("/imgs/grass.png"));
        lakeImage = new Image(getClass().getResourceAsStream("/imgs/lake.png"));
        textField  = new TextField();
        // Set up logging
        setupLogger();

        // Create a canvas for drawing
        Canvas canvas = new Canvas(gameBoard.getWidth(), gameBoard.getHeight());
        gc = canvas.getGraphicsContext2D();


        // Create the root layout and add the canvas
        BorderPane root = new BorderPane();
        VBox vbox = new VBox();
        vbox.setBackground(Background.fill(Color.LIGHTGREEN));


        // Create the menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.setBackground(Background.fill(Color.LIGHTGREEN));
      //  menuBar.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // Create the options menu
        Menu optionsMenu = new Menu("Options");
        CheckMenuItem loggingMenuItem = new CheckMenuItem("Enable Logging");
        loggingMenuItem.setSelected(true); // Enable logging by default
        loggingMenuItem.setOnAction((event) -> {
            this.toggleLogging(loggingMenuItem.isSelected());
        });
        optionsMenu.getItems().add(loggingMenuItem);

        menuBar.getMenus().addAll(optionsMenu);
        vbox.getChildren().add(menuBar);
        root.setTop(menuBar);



        HBox hbox = new HBox();
        hbox.setSpacing(40);
        vbox.getChildren().add(hbox);
        hbox.setAlignment(javafx.geometry.Pos.CENTER);

        Button button = new Button("Add Fox");
        button.setOnAction((event) -> {
            var fox = new Fox();
            fox.isNew = true;
            Game.generatePosition(fox);
            Game.getGameState().addEntities(fox);
            logger.info("New Fox added");
        });


        Button button2 = new Button("Add Hare");
        button2.setOnAction((event) -> {
            var hare = new Hare();
            hare.isNew = true;
            Game.generatePosition(hare);
            Game.getGameState().addEntities(hare);
            logger.info("New Hare added");
        });


        Button startButton = new Button("StartGame");


        startButton.setOnAction(event ->  {
                startSimulation();
                startButton.setDisable(true);
        });


        textField.setEditable(false);

        textField.setPrefWidth(600);
        hbox.getChildren().addAll(button, button2, startButton, textField);
        root.setCenter(vbox);
        vbox.getChildren().add(canvas);

        // Load the FXML file and create the scene
        Scene scene = new Scene(root, gameBoard.getWidth(), gameBoard.getHeight());
        stage.setTitle("Simulation");
        stage.setScene(scene);
        stage.show();



        // tread for drawing the game entities on the canvas every 100ms
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                draw();
            }
        }).start();
    }

    private void startSimulation() {
        System.out.println("startSimulation");
        this.getEntitiesCount();
    }

    private void getEntitiesCount() {
        VBox vbox = new VBox();
        Stage stage = new Stage();
        stage.setTitle("Entities Count");
        stage.setScene(new Scene(vbox, 800, 200));
        stage.show();
        HBox hbox = new HBox();
        vbox.getChildren().add(hbox);
        hbox.setSpacing(40);
    //    hbox.setAlignment(javafx.geometry.Pos.CENTER);
        Button button = new Button("Add Fox");;
        Button button2 = new Button("Add Hare");
        Button button3 = new Button("Add Grass");
        Button button4 = new Button("Add Lake");
        Button button5 = new Button("Add Hunter");

        TextArea textArea = new TextArea();
        textArea.setText("Fox: " + this.foxCount + "\nHare: "+ this.hareCount + "\nGrass: " + this.grassCount +"\nLake: " + this.lakeCount + "\nHunter: " + this.hunterCount + "\n");
        vbox.getChildren().add(textArea);

        hbox.getChildren().addAll(button, button2, button3, button4, button5);

        button.setOnAction((event) -> {
            this.foxCount++;
            this.totalCount++;
            textArea.setText("Fox: " + this.foxCount + "\nHare: "+ this.hareCount + "\nGrass: " + this.grassCount +"\nLake: " + this.lakeCount + "\nHunter: " + this.hunterCount + "\n");
            if (this.foxCount >= 30 || this.totalCount >= 184) {
                button.setDisable(true);
            }
        });
        button2.setOnAction((event) -> {
            this.hareCount++;
            this.totalCount++;
            textArea.setText("Fox: " + this.foxCount + "\nHare: "+ this.hareCount + "\nGrass: " + this.grassCount +"\nLake: " + this.lakeCount + "\nHunter: " + this.hunterCount + "\n");
            if (this.hareCount >= 50 || this.totalCount >= 184) {
                button2.setDisable(true);
            }
        });
        button3.setOnAction((event) -> {
            this.grassCount++;
            this.totalCount++;
            textArea.setText("Fox: " + this.foxCount + "\nHare: "+ this.hareCount + "\nGrass: " + this.grassCount +"\nLake: " + this.lakeCount + "\nHunter: " + this.hunterCount + "\n");
            if (this.grassCount >= 70 || this.totalCount >= 184) {
                button3.setDisable(true);
            }
        });
        button4.setOnAction((event) -> {
            this.lakeCount++;
            this.totalCount++;
            textArea.setText("Fox: " + this.foxCount + "\nHare: "+ this.hareCount + "\nGrass: " + this.grassCount +"\nLake: " + this.lakeCount + "\nHunter: " + this.hunterCount + "\n");
            if (this.lakeCount >= 30 || this.totalCount >= 184) {
                button4.setDisable(true);
            }
        });
        button5.setOnAction((event) -> {
            this.hunterCount++;
            this.totalCount++;
            textArea.setText("Fox: " + this.foxCount + "\nHare: "+ this.hareCount + "\nGrass: " + this.grassCount +"\nLake: " + this.lakeCount + "\nHunter: " + this.hunterCount + "\n");
            if (this.hunterCount >= 10 || this.totalCount >= 184) {
                button5.setDisable(true);
            }
        });
        System.out.println("this.foxCount" + this.foxCount);
        VBox vbox2 = new VBox();
        vbox.getChildren().add(vbox2);
        Button startButton = new Button("StartGame");
        startButton.setOnAction(event ->  {
            startGame();
            startButton.setDisable(true);
            stage.hide();
        });
        vbox2.getChildren().add(startButton);
    }

    private static void setTextStatistics(String s) {
        System.out.println("setTextStatistics");
        textField.setText(s);
    }


    /**
     * The `draw` method is responsible for drawing the game state on the canvas.
     */
    private static void draw() {
        //reset canvas
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(0, 0, gameBoard.getWidth(), gameBoard.getHeight());
        // Check if the game state is available
        if (Game.getGameState() == null) {
            return;
        }
        entityCountMap.clear();

        //System.out.println("Game.getGameState().getGameEntities()" + Game.getGameState().getGameEntities().size());
        // Iterate through game entities and draw them based on their type
        for(GameEntity entity : Game.getGameState().getGameEntities()) {
            entityCountMap.put(entity.getClass(), entityCountMap.getOrDefault(entity.getClass(), 0) + 1);
            switch (entity.getClass().getSimpleName()) {
                case "Fox":
                    if (((Fox) entity).isNew) {
                        gc.setFill(Color.HOTPINK);
                        gc.fillOval(entity.getPosition().getX(), entity.getPosition().getY(), 30, 30);
                        ((Fox) entity).isNew = false;
                    } else {
                        gc.drawImage(foxImage, entity.getPosition().getX(), entity.getPosition().getY(), 20, 20);
                    }
                    //gc.drawImage(foxImage, entity.getPosition().getX(), entity.getPosition().getY(), 20, 20);
                    break;
                case "Hare":
                    if (((Hare) entity).isNew) {
                        gc.setFill(Color.HOTPINK);
                        gc.fillOval(entity.getPosition().getX(), entity.getPosition().getY(), 30, 30);
                        ((Hare) entity).isNew = false;
                    } else {
                    gc.drawImage(hareImage, entity.getPosition().getX(), entity.getPosition().getY(), 20, 20);
                    }
                    break;
                case "Lake":
                    gc.drawImage(lakeImage, entity.getPosition().getX(), entity.getPosition().getY(), 50, 50);
                    break;
                case "Grass":
                    gc.drawImage(grassImage, entity.getPosition().getX(), entity.getPosition().getY(), 20, 20);
                    break;
                case "Tree":
                    gc.drawImage(treeImage, entity.getPosition().getX(), entity.getPosition().getY(), 50, 50);
                    break;
                    case "Hunter":
                    gc.drawImage(hunterImage, entity.getPosition().getX(), entity.getPosition().getY(), 20, 20);
                    break;
                case "Bullet":
                    // Clear the canvas
                    gc.setFill(Color.RED);
                    gc.fillOval(entity.getPosition().getX(), entity.getPosition().getY(), 5, 5);
                    break;

            }

//            gc.strokeRect(entity.getPosition().getHitboxTopLeftCorner().getFirst(), entity.getPosition().getHitboxTopLeftCorner().getSecond(), entity.getPosition().getHitboxSize().getFirst(), entity.getPosition().getHitboxSize().getSecond());
         /*   gc.fillOval(entity.getPosition().getX(), entity.getPosition().getY(), 20, 20);
            gc.strokeRect(entity.getPosition().getHitboxTopLeftCorner().getFirst(), entity.getPosition().getHitboxTopLeftCorner().getSecond(), 20, 20);*/
        }
        if (System.currentTimeMillis() - lastLogTime > logInterval) {
            StringBuilder sb = new StringBuilder("Animal Counts:");
            entityCountMap.forEach((key1, count) -> {
                var key = key1.getSimpleName();
                sb.append(" ").append(key).append(": ").append(count);
            });
            lastLogTime = System.currentTimeMillis();
            System.out.println(sb.toString());
            setTextStatistics(sb.toString());
        }

    }
    private void setupLogger() {
        logger.setLevel(Level.INFO);
    }

    private void toggleLogging(boolean enable) {
        if (!enable) {
            logger.setLevel(Level.OFF);
        }
    }
}