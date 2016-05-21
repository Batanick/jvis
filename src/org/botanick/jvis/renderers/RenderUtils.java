package org.botanick.jvis.renderers;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

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

    public static TextField textField(String mask) {
        final TextField field = new TextField();
        field.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!field.getText().matches(mask)) {
                    field.setText(oldValue.toString());
                }
            }
        });

        field.setFont(Font.font(field.getFont().getFamily(), 12));

        return field;
    }


}
