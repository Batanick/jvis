package org.botanick.jvis.renderers;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.botanick.jvis.DataRenderer;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.introspect.AnnotatedMember;

import java.lang.reflect.Type;

/**
 * Created by Bot_A_Nick with love on 5/21/2016.
 */
public class FloatRenderer implements DataRenderer {
    @Override
    public boolean applicable(Type _type) {
        return _type.equals(Float.class) || _type.equals(Float.TYPE);
    }

    @Override
    public void render(Object _instance, BeanPropertyDefinition property, VBox container) {
        final TextField field = RenderUtils.textField(newValue -> {
            try {
                final AnnotatedMember mutator = property.getMutator();
                mutator.fixAccess();
                mutator.setValue(_instance, Float.valueOf(newValue));
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }, _instance, property);
        container.getChildren().add(RenderUtils.addLabel(property.getName(), field));
    }
}
