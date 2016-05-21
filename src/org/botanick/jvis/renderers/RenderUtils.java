package org.botanick.jvis.renderers;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;

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
        final Label label = new Label(name + ":");
        label.setFont(Font.font(label.getFont().getFamily(), 12));

        hBox.getChildren().addAll(label, control);
        return hBox;
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
        field.setText(extractValue(instance, propertyDefinition));
        field.setFont(Font.font(field.getFont().getFamily(), 12));

        return field;
    }

    public static String extractValue(Object _instance, BeanPropertyDefinition propertyDefinition) {
        final AnnotatedMember accessor = propertyDefinition.getAccessor();
        accessor.fixAccess();
        final Member member = accessor.getMember();
        if (member instanceof Method) {
            try {
                return ((Method) member).invoke(_instance).toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (member instanceof Field) {
            try {
                return ((Field)member).get(_instance).toString();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        throw new RuntimeException("Unknown accessor type:" + accessor.getClass().getName());
    }


}
