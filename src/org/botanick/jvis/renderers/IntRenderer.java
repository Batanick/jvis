package org.botanick.jvis.renderers;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.botanick.jvis.DataRenderer;
import org.codehaus.jackson.map.BeanPropertyDefinition;

import java.lang.reflect.Type;

/**
 * Created by Bot_A_Nick with love on 5/21/2016.
 */
public class IntRenderer implements DataRenderer {
    @Override
    public boolean applicable(Type _type) {
        return _type.equals(Integer.class) || _type.equals(Integer.TYPE);
    }

    @Override
    public void render(BeanPropertyDefinition property, VBox container) {
        final TextField field = RenderUtils.textField("\\.[0-9]");
        container.getChildren().add(RenderUtils.addLabel(property.getName(), field));

//        field.focusedProperty().addListener();
    }
}
