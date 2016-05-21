package org.botanick.jvis;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import javafx.util.Pair;
import org.botanick.jvis.renderers.RenderUtils;
import org.botanick.jvis.resources.ResourceDB;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanPropertyDefinition;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main extends Application {
    private static final String NAME = "JacksonVis";

    private final ResourceDB resourceDB = new ResourceDB();

    private GridPane mainPane;
    private Scene scene;
    private Stage stage;

    private List<Pair<Node, Node>> drawLines = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
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

        load(new TestClass());


    }

    private void load(Object instance) {
        drawLines.clear();
        mainPane.getChildren().clear();

        load(new TestSubElement(), 0, 0);

        stage.setScene(null);
        stage.setScene(scene);

        for (Pair<Node, Node> drawLine : drawLines) {
            drawLine(drawLine.getKey(), drawLine.getValue());
        }
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
        newBtn.setOnAction(event -> load(new TestClass()));

        final Button saveBtn = new Button("Save");
        saveBtn.setOnAction(event -> {

        });

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

    private Pair<Node, Integer> load(final Object _obj, int col, int row) {
        final BeanDescription description = resourceDB.loadDescription(_obj.getClass());
        if (description == null) {
            return null;
        }

        final VBox container = createElementContainer(_obj.getClass().getSimpleName());

        int childrenCount = 0;
        for (BeanPropertyDefinition property : description.findProperties()) {
            final DataRenderer renderer = resourceDB.findRendererFor(property);
            if (renderer != null) {
                renderer.render(_obj, property, container);
                continue;
            }

            final Label label = RenderUtils.label(property.getName());
            container.getChildren().add(label);
            final Pair<Node, Integer> loadResult = load(RenderUtils.extractValue(_obj, property), col + 1, row + childrenCount);
            if (loadResult == null) {
                continue;
            }
            childrenCount += loadResult.getValue();
            drawLines.add(new Pair<>(label, loadResult.getKey()));
        }

        mainPane.add(container, col, row, 1, Math.max(1, childrenCount));
        GridPane.setValignment(container, VPos.TOP);

        return new Pair<>(container, Math.max(1, childrenCount));
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

    private void drawLine(Node start, Node finish) {
        Bounds startBounds = start.getBoundsInLocal();
        startBounds = start.localToScene(startBounds);
        startBounds = mainPane.sceneToLocal(startBounds);

        Bounds finishBounds = finish.getBoundsInLocal();
        finishBounds = finish.localToScene(finishBounds);
        finishBounds = mainPane.sceneToLocal(finishBounds);

        final Line line = new Line(startBounds.getMaxX(), startBounds.getMinY() + startBounds.getHeight()  / 2,
                finishBounds.getMinX(), finishBounds.getMinY() + finishBounds.getHeight() / 2);
        line.setManaged(false);
        mainPane.getChildren().add(line);
    }

}
