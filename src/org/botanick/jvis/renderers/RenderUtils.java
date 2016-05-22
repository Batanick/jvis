package org.botanick.jvis.renderers;

import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import org.botanick.jvis.Logging;
import org.botanick.jvis.resources.ResourceDB;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.introspect.AnnotatedMember;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Bot_A_Nick with love on 5/21/2016.
 */
public class RenderUtils {
    private RenderUtils() {
        //nothing
    }

    public static Pane addLabel(final String name, final Control control) {
        final HBox hBox = new HBox();
        hBox.setSpacing(8);
        final Label label = label(name);

        hBox.getChildren().addAll(label, control);
        return hBox;
    }

    public static Label label(String name) {
        final Label label = new Label(name + ":");
        label.setPrefWidth(120);
        label.setFont(Font.font(label.getFont().getFamily(), 14));
        return label;
    }

    public static TextField textField(FieldSetter setter, Object instance, BeanPropertyDefinition propertyDefinition) {
        final TextField field = new TextField();
        field.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (setter.trySetField(field.getText())) {
                        field.getStyleClass().removeAll("error");
                    } else {
                        field.getStyleClass().add("error");
                    }
                }
        );
        field.setText(extractValue(instance, propertyDefinition).toString());
        field.setFont(Font.font(field.getFont().getFamily(), 12));

        return field;
    }

    public static Object extractValue(Object _instance, BeanPropertyDefinition propertyDefinition) {
        final AnnotatedMember accessor = propertyDefinition.getAccessor();
        accessor.fixAccess();
        final Member member = accessor.getMember();
        if (member instanceof Method) {
            try {
                return ((Method) member).invoke(_instance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (member instanceof Field) {
            try {
                return ((Field) member).get(_instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        throw new RuntimeException("Unknown accessor type:" + accessor.getClass().getName() + ", prop:" + propertyDefinition.getName());
    }

    public static boolean setValue(Object instance, BeanPropertyDefinition property, Object value) {
        final AnnotatedMember mutator = property.getMutator();
        mutator.fixAccess();
        mutator.setValue(instance, value);
        return true;
    }

    public static Object instance(BeanPropertyDefinition property, ResourceDB resourceDB) {
        Class<?> clazz = property.getField().getRawType();
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            final Set<Class> classes = resourceDB.subclassesOf(clazz);
            clazz = demandChoice(classes);
        }

        if (clazz == null) {
            return null;
        }

        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Logging.log("Unable to instantiate isntance of class:" + clazz.getName());
        }

        return null;
    }


    public static Class demandChoice(Set<Class> cases) {
        if (cases.isEmpty()) {
            return null;
        }

        final List<String> choices = new ArrayList<>();
        for (Class cse : cases) {
            if (cse.isInterface() || Modifier.isAbstract(cse.getModifiers())) {
                continue;
            }
            choices.add(cse.getName());
        }

        if (choices.isEmpty()) {
            return null;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setContentText("Choose implementation:");
        dialog.setHeaderText("Implementation");
        dialog.setHeaderText("Implementation");

        final Optional<String> result = dialog.showAndWait();

        if (result.isPresent())
            for (Class aCase : cases) {
                if (aCase.getName().equals(result.get())) {
                    return aCase;
                }
            }
        return null;
    }
}
