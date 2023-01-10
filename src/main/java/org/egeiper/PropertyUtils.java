package org.egeiper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
public final class PropertyUtils {
    private static final String EXCEPTION_MESSAGE = "Invalid property key or resource file";
    private PropertyUtils() {
    }

    public static String getProperty(final String fileName, final String propertyKey) {
        final Properties prop = new Properties();

        final String resource = Objects.requireNonNull(Thread.currentThread()
                .getContextClassLoader().getResource(fileName)).getPath();
        try (BufferedReader reader =
                     Files.newBufferedReader(Paths.get(resource))) {
            prop.load(reader);
        } catch (IOException e) {
            throw new FileOrResourceNotFoundException(EXCEPTION_MESSAGE, e);
        }
        return prop.getProperty(propertyKey);
    }
}
