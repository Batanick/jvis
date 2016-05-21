package org.botanick.jvis;

import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.botanick.jvis.resources.ResourceDB;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanPropertyDefinition;

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

        resourceDB.init();

        load(new TestClass());
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
        saveBtn.setOnAction(event -> System.out.println("Save!"));

        final Button openBtn = new Button("Open");
        openBtn.setOnAction(event -> System.out.println("open!"));
        pane.getChildren().addAll(newBtn, openBtn, saveBtn);

        return pane;
    }

    private GridPane buildMain() {
        final GridPane grid = new GridPane();
        return grid;
    }

    private VBox instantiateElementContainer(final String _name) {
        final VBox box = new VBox();
        box.getChildren().addAll(new Label(_name));
        return box;
    }

    private void load(final Object _obj) {
        final BeanDescription description = resourceDB.loadDescription(_obj.getClass());
        if (description == null) {
            return;
        }

        for (BeanPropertyDefinition property : description.findProperties()) {
            final DataRenderer renderer = resourceDB.findRendererFor(property);
            if (renderer == null) {
                Logging.log("Unable to find renderer for property: " + property);
            }
        }

    }

}
