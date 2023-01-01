package org.egeiper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public final class PropertyUtils {
    private static final String EXCEPTION_MESSAGE = "Invalid property key or resource file";
    private static final String RESOURCE_DIRECTORY = "/Users/egeiper/IdeaProjects/QA-Base/src/main/resources/";
    private PropertyUtils() {
    }


    public static String getProperty(final String fileName, final String propertyKey) {
        final Properties prop = new Properties();
        try (BufferedReader reader =
                     Files.newBufferedReader(Paths.get(RESOURCE_DIRECTORY + fileName))) {
            prop.load(reader);
        } catch (IOException e) {
            throw new FileOrResourceNotFoundException(EXCEPTION_MESSAGE, e);
        }
        return prop.getProperty(propertyKey);
    }
}
