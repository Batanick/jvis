package org.botanick.jvis;

import java.lang.reflect.Type;

/**
 * Created by Bot_A_Nick with love on 5/20/2016.
 */
public interface DataRenderer {
    boolean applicable(Type _type);
    void render();
}
