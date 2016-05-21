package org.botanick.jvis.renderers;

import org.botanick.jvis.DataRenderer;

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
    public void render() {

    }
}
