package org.botanick.jvis.resources;

import org.botanick.jvis.DataRenderer;
import org.botanick.jvis.Logging;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Bot_A_Nick with love on 5/18/2016.
 */
public final class ResourceDB {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Map<Class, BeanDescription> classesCache = new HashMap<>();
    private final List<DataRenderer> renderers = new ArrayList<>();

    public void init() {
        Logging.log("Initializing resourceDB");

        final Reflections reflections = new Reflections("org.botanick.jvis.renderers");
        final Set<Class<? extends DataRenderer>> renderersSet = reflections.getSubTypesOf(DataRenderer.class);
        for (Class<? extends DataRenderer> renderer : renderersSet) {
            Logging.log("Loading renderer: " + renderer.getName());
            final Constructor<?> constructor = renderer.getConstructors()[0];
            try {
                renderers.add((DataRenderer) constructor.newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Logging.log(String.format("Unable to instantiate renderer of type:%s, reason:%s", renderer.getName(), e.getMessage()));
            }
        }
    }

    public BeanDescription loadDescription(final Class _clazz) {
        BeanDescription description = classesCache.get(_clazz);
        if (description != null) {
            return description;
        }

        final JavaType javaType = MAPPER.constructType(_clazz);
        description = MAPPER.getSerializationConfig().introspect(javaType);
        classesCache.put(_clazz, description);

        if (description == null) {
            Logging.log("Unable to load description for type: " + _clazz);
        }
        return description;
    }

    public DataRenderer findRendererFor(BeanPropertyDefinition property) {
        for (DataRenderer renderer : renderers) {
            if (renderer.applicable(property.getField().getGenericType())) {
                return renderer;
            }
        }

        return null;
    }
}
