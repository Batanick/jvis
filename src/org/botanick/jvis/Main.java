package org.botanick.jvis;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.botanick.jvis.renderers.RenderUtils;
import org.botanick.jvis.resources.ResourceDB;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanPropertyDefinition;

import java.lang.reflect.Type;
import java.util.Collection;

public class Main extends Application {
    private static final String NAME = "JacksonVis";

    private final ResourceDB resourceDB = new ResourceDB();

    private GridPane mainPane;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(NAME);

        final BorderPane root = new BorderPane();
        root.setTop(buildControlls());
        mainPane = buildMain();
        root.setCenter(new ScrollPane(mainPane));

        scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        scene.getStylesheets().addAll("/org/botanick/jvis/styles.css");

        primaryStage.show();

        resourceDB.init();

        load(new TestClass(), 0, 0);
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
        grid.setHgap(80);
        grid.setVgap(30);
        grid.setAlignment(Pos.TOP_LEFT);

        grid.setGridLinesVisible(false);
        return grid;
    }

    private VBox createElementContainer(final String _name) {
        final VBox box = new VBox();
        box.setStyle("-fx-background-color: #AA6666;");

        final Label label = new Label(_name);
        label.setFont(Font.font(label.getFont().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));

        box.getChildren().addAll(label, new Separator(Orientation.HORIZONTAL));
        box.setMaxHeight(10);
        return box;
    }

    private int load(final Object _obj, int col, int row) {
        final BeanDescription description = resourceDB.loadDescription(_obj.getClass());
        if (description == null) {
            return 0;
        }

        final VBox container = createElementContainer(_obj.getClass().getSimpleName());

        int childsRendered = 0;
        for (BeanPropertyDefinition property : description.findProperties()) {
            final DataRenderer renderer = resourceDB.findRendererFor(property);
            if (renderer != null) {
                renderer.render(_obj, property, container);
                continue;
            }

            final Label label = RenderUtils.label(property.getName());
            container.getChildren().add(label);
            childsRendered += load(RenderUtils.extractValue(_obj, property), col + 1, row + childsRendered);
            drawArrow(label, col + 1, row + childsRendered);
        }

        mainPane.add(container, col, row, 1, Math.max(1, childsRendered));
        GridPane.setValignment(container, VPos.TOP);

        return Math.max(1, childsRendered);
    }

    private void loadCollection(BeanPropertyDefinition property, Object obj, int parentCol, int parentRow) {
        final Collection collection = (Collection) RenderUtils.extractValue(obj, property);

        int i = 0;
        for (Object element : collection) {
            load(element, parentCol + 1, parentRow + i++);
        }
    }

    private boolean isContainer(BeanPropertyDefinition propertyDefinition) {
        final Type type = propertyDefinition.getField().getGenericType();
        if (!(type instanceof Class)) {
            return false;
        }

        final Class clazz = (Class) type;
        return clazz.isArray() || clazz.isAssignableFrom(Collection.class);
    }

    private void drawArrow(Node node, int col, int row) {

    }


}
