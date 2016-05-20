package org.botanick.jvis;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.botanick.jvis.resources.ResourceDB;

public class Main extends Application {
    private static final String NAME = "JacksonVis";

    private final ResourceDB resourceDB = new ResourceDB();

    private GridPane mainPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(NAME);

        final BorderPane root = new BorderPane();
        root.setTop(buildControlls());
        mainPane = buildMain();
        root.setCenter(mainPane);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Pane buildControlls() {
        final HBox pane = new HBox();
        pane.setPadding(new Insets(5, 7, 5, 5));
        pane.setSpacing(8);
        pane.setStyle("-fx-background-color: #336699;");

        final Button newBtn = new Button("New");
        newBtn.setOnAction(event -> System.out.println("New!"));

        final Button saveBtn = new Button("Save");
        newBtn.setOnAction(event -> System.out.println("Save!"));

        final Button openBtn = new Button("Open");
        newBtn.setOnAction(event -> System.out.println("open!"));
        pane.getChildren().addAll(newBtn, openBtn, saveBtn);

        return pane;
    }

    private GridPane buildMain() {
        final GridPane grid = new GridPane();


        return grid;
    }

}
