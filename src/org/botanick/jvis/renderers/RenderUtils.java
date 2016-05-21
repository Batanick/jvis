package org.botanick.jvis.renderers;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import org.botanick.jvis.Logging;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.introspect.AnnotatedMember;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

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

    public static boolean setDefaultValue(Object instance, BeanPropertyDefinition property, boolean defaultNull) {
        final AnnotatedMember mutator = property.getMutator();
        mutator.fixAccess();
        final Class<?> clazz = property.getField().getRawType();
        try {
            final Object valueInstance = defaultNull ? null : clazz.newInstance();
            mutator.setValue(instance, valueInstance);
        } catch (InstantiationException | IllegalAccessException e) {
            Logging.log(e.getMessage());
            return false;
        }

        return true;
    }
}
