package org.botanick.jvis;

import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
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
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setGridLinesVisible(true);
        return grid;
    }

    private VBox createElementContainer(final String _name, int col, int row) {
        final VBox box = new VBox();
        box.setStyle("-fx-background-color: #AA6666;");

        final Label label = new Label(_name);
        label.setFont(Font.font(label.getFont().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));

        box.getChildren().addAll(label, new Separator(Orientation.HORIZONTAL));
        mainPane.add(box, col, row);
        return box;
    }

    private void load(final Object _obj) {
        final BeanDescription description = resourceDB.loadDescription(_obj.getClass());
        if (description == null) {
            return;
        }

        final VBox container = createElementContainer(_obj.getClass().getSimpleName(), 0, 0);

        for (BeanPropertyDefinition property : description.findProperties()) {
            final DataRenderer renderer = resourceDB.findRendererFor(property);
            if (renderer == null) {
                Logging.log("Unable to find renderer for property: " + property);
                continue;
            }

            renderer.render(property, container);
        }
    }

}
