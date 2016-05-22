package org.botanick.jvis.resources;

import org.botanick.jvis.DataRenderer;
import org.botanick.jvis.Logging;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

/**
 * Created by Bot_A_Nick with love on 5/18/2016.
 */
public final class ResourceDB {
    private final ObjectMapper mapper = new ObjectMapper();

    private final Map<Class, BeanDescription> classesCache = new HashMap<>();
    private final List<DataRenderer> renderers = new ArrayList<>();
    private final Map<Class, Set<Class>> classToImplCache = new HashMap<>();
    private Reflections reflections;

    public void init() {
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        Logging.log("Initializing resourceDB");

        reflections = new Reflections(readPackageConfig());
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

        classToImplCache.put(Set.class, Collections.singleton(HashSet.class));
        classToImplCache.put(Map.class, Collections.singleton(HashMap.class));
        classToImplCache.put(List.class, Collections.singleton(ArrayList.class));
    }

    private static ConfigurationBuilder readPackageConfig() {
        final File config = new File("config");
        if (!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        final Set<URL> urls = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(config))) {
            String line;
            while ((line = br.readLine()) != null) {
                urls.addAll(ClasspathHelper.forPackage(line));
                // process the line.
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        final ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setUrls(urls);
        builder.setScanners(new SubTypesScanner(false));
        return builder;
    }

    public BeanDescription loadDescription(final Class _clazz) {
        BeanDescription description = classesCache.get(_clazz);
        if (description != null) {
            return description;
        }

        final JavaType javaType = mapper.constructType(_clazz);
        description = mapper.getSerializationConfig().introspect(javaType);
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

    public Set<Class> subclassesOf(Class clazz) {
        Set<Class> result = classToImplCache.get(clazz);
        if (result != null) {
            return result;
        }

        final ConfigurationBuilder builder = new ConfigurationBuilder();

        //noinspection unchecked
        result = reflections.getSubTypesOf(clazz);

        classToImplCache.put(clazz, result);
        return result;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }
}
