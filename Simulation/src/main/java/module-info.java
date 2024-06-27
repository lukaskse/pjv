module com.simulation.simulation {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.base;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires lombok;
    requires java.logging;


    opens com.simulation.simulation to javafx.fxml;

    exports com.simulation.simulation.model;
    exports com.simulation.simulation.model.game.entities;

    exports com.simulation.simulation;

}
