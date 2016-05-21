package org.botanick.jvis.renderers;

/**
 * Created by Bot_A_Nick with love on 5/21/2016.
 */
@FunctionalInterface
public interface FieldSetter {
    boolean trySetField(final String newValue);
}
