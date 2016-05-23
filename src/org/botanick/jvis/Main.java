package org.botanick.jvis;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.botanick.jvis.renderers.RenderUtils;
import org.botanick.jvis.resources.ResourceDB;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanPropertyDefinition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {
    private static final String NAME = "JacksonVis";

    private final ResourceDB resourceDB = new ResourceDB();

    private GridPane mainPane;
    private Scene scene;
    private Stage stage;

    private Object loaded = null;
    private File file = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        primaryStage.setTitle(NAME);

        final BorderPane root = new BorderPane();
        root.setTop(buildControlls());
        mainPane = buildMain();
        root.setCenter(new ScrollPane(mainPane));

        scene = new Scene(root);
        primaryStage.setScene(scene);
        scene.getStylesheets().addAll("/org/botanick/jvis/styles.css");

        primaryStage.show();
        primaryStage.setMaximized(true);

        resourceDB.init();

        load(new TestClass());
    }

    private void reload() {
        if (loaded == null) {
            return;
        }

        load(loaded);
    }

    private void loadNew() {
        file = null;
        loaded = null;

        loaded = RenderUtils.instance((Class) null, resourceDB);
        reload();
    }

    private void save() {
        if (file == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Resource File");
            file = fileChooser.showSaveDialog(stage);
        }

        if (file == null) {
            return;
        }

        try {
            resourceDB.getMapper().writerWithDefaultPrettyPrinter().writeValue(file, loaded);
        } catch (IOException e) {
            RenderUtils.showError(e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
    }

    private void open() {
        file = null;
        loaded = null;

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                loaded = resourceDB.getMapper().readValue(file, Object.class);
            } catch (IOException e) {
                RenderUtils.showError(e.getMessage(), Arrays.toString(e.getStackTrace()));
                file = null;
                return;
            }
        }

        reload();
    }

    private void load(Object instance) {
        loaded = instance;
        mainPane.getChildren().clear();

        load(instance, 0, 0, null);
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
        newBtn.setOnAction(event -> loadNew());

        final Button saveBtn = new Button("Save");
        saveBtn.setOnAction(event -> save());

        final Button openBtn = new Button("Open");
        openBtn.setOnAction(event -> open());
        pane.getChildren().addAll(newBtn, openBtn, saveBtn);

        return pane;
    }

    private GridPane buildMain() {
        final GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setHgap(80);
        grid.setVgap(30);
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setMinSize(1024, 768);

        grid.setGridLinesVisible(false);
        return grid;
    }

    private VBox createElementContainer(final String _name, final EventHandler<ActionEvent> customControll) {
        final VBox box = new VBox();
        box.setPadding(new Insets(3, 3, 3, 3));
        box.setStyle("-fx-border-radius: 10 10 10 10;"
                + "-fx-background-color: #AA6666;"
                + "-fx-background-radius: 10 10 10 10;");

        final HBox hBox = new HBox();
        if (customControll != null) {
            final Button btn = new Button("X");
            btn.setOnAction(customControll);
            hBox.getChildren().add(btn);
        }

        final Label label = new Label(_name);
        hBox.getChildren().add(label);
        label.setFont(Font.font(label.getFont().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));

        box.getChildren().addAll(hBox, new Separator(Orientation.HORIZONTAL));
        box.setMaxHeight(10);
        return box;
    }

    private Pair<Node, Integer> load(final Object _obj, int col, int row, EventHandler<ActionEvent> customControll) {
        final BeanDescription description = resourceDB.loadDescription(_obj.getClass());
        if (description == null) {
            return null;
        }

        final VBox container = createElementContainer(_obj.getClass().getSimpleName(), customControll);

        int childrenCount = 0;
        for (BeanPropertyDefinition property : description.findProperties()) {
            childrenCount += loadField(_obj, col, row + childrenCount, container, property);
        }

        System.out.println(_obj.getClass().getSimpleName() + ":" + col + ":" + row + ":" + Math.max(1, childrenCount));
        mainPane.add(container, col, row, 1, Math.max(1, childrenCount));
        GridPane.setValignment(container, VPos.TOP);

        return new Pair<>(container, Math.max(1, childrenCount));
    }

    private int loadField(Object _obj, int col, int row, VBox container, BeanPropertyDefinition property) {
        final DataRenderer renderer = resourceDB.findRendererFor(property);
        if (renderer != null) {
            renderer.render(_obj, property, container);
            return 0;
        }

        if (RenderUtils.isArray(property)) {
            return loadArray(property, _obj, container, col, row);
        }

        final Object value = RenderUtils.extractValue(_obj, property);

        // loading as object
        final HBox hBox = new HBox();
        container.getChildren().add(hBox);

        hBox.getChildren().add(RenderUtils.label(property.getName()));

        final Button btn = new Button();
        hBox.getChildren().add(btn);

        if (value == null) {
            btn.setText("+");
            btn.setOnAction(event -> {
                if (RenderUtils.setValue(_obj, property, RenderUtils.instance(property, resourceDB))) {
                    reload();
                }
            });
            return 0;
        }

        btn.setText("x");
        btn.setOnAction(event -> {
            if (RenderUtils.setValue(_obj, property, null)) {
                reload();
            }
        });

        final Pair<Node, Integer> loadResult = load(value, col + 1, row, null);
        if (loadResult == null) {
            return 0;
        }

        new ConnectedLine(hBox, loadResult.getKey());
        return loadResult.getValue();
    }

    private int loadArray(BeanPropertyDefinition property, final Object obj, VBox container, int parentCol, int parentRow) {
        final Class<?> componentType = property.getField().getRawType().getComponentType();
        Object currentValue = RenderUtils.extractValue(obj, property);
        if (currentValue == null) {
            RenderUtils.setValue(obj, property, RenderUtils.instantiateArray(componentType));
            currentValue = RenderUtils.extractValue(obj, property);
        }

        final List<Object> values = new ArrayList<>();
        for (Object element : ((Object[]) currentValue)) {
            if (element == null) {
                continue;
            }
            values.add(element);
        }

        final HBox hBox = new HBox();
        container.getChildren().add(hBox);
        final Button addElementBtn = new Button("+");
        addElementBtn.setOnAction(event -> {
            Object[] current = (Object[]) RenderUtils.extractValue(obj, property);
            final Object[] newValue = Arrays.copyOf(current, current.length + 1);
            final Object instance = RenderUtils.instance(componentType, resourceDB);
            newValue[newValue.length - 1] = instance;
            RenderUtils.setValue(obj, property, newValue);

            reload();
        });

        hBox.getChildren().addAll(RenderUtils.label(property.getName()), addElementBtn);

        int i = 0;
        int childsCount = 0;
        for (final Object element : values) {
            final Pair<Node, Integer> result = load(element, parentCol + 1, parentRow + childsCount,
                    event -> {
                        Object[] current = (Object[]) RenderUtils.extractValue(obj, property);
                        RenderUtils.setValue(obj, property, RenderUtils.removeElement(current, element));
                        reload();
                    });

            if (result == null) {
                continue;
            }


            new ConnectedLine(hBox, result.getKey());
            childsCount += result.getValue();
            i++;
        }

        return Math.max(i, childsCount);
    }

    private final class ConnectedLine implements ChangeListener<Transform> {
        private final Node start;
        private final Node finish;
        private final Line line;

        public ConnectedLine(Node start, Node finish) {
            this.start = start;
            this.finish = finish;

            line = new Line();
            line.setManaged(false);
            mainPane.getChildren().add(line);
            refresh();

            start.localToParentTransformProperty().addListener(this);
            finish.localToParentTransformProperty().addListener(this);
        }

        @Override
        public void changed(ObservableValue<? extends Transform> observable, Transform oldValue, Transform newValue) {
            refresh();
        }

        private void refresh() {
            if (start.getParent() == null || finish.getParent() == null) {
                return;
            }

            Bounds startBounds = start.getBoundsInLocal();
            startBounds = start.localToScene(startBounds);
            startBounds = mainPane.sceneToLocal(startBounds);

            Bounds finishBounds = finish.getBoundsInLocal();
            finishBounds = finish.localToScene(finishBounds);
            finishBounds = mainPane.sceneToLocal(finishBounds);

            line.setStartX(startBounds.getMinX() + start.getParent().getBoundsInLocal().getWidth());
            line.setStartY(startBounds.getMinY() + startBounds.getHeight() / 2 + 2);

            line.setEndX(finishBounds.getMinX());
            line.setEndY(finishBounds.getMinY() + finishBounds.getHeight() / 2 + 2);

            line.setStroke(Color.GREY);
            line.setStrokeWidth(3);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            line.setFill(Color.CORNSILK.deriveColor(0, 1.2, 1, 0.6));
        }
    }


}
