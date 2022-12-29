import java.io.*;
import java.util.Properties;

public class PropertyFinder {
    private static final String EXCEPTION_MESSAGE = "Invalid property key or resource file";
    private static final String RESOURCE_DIRECTORY = "/Users/egeiper/IdeaProjects/QA-Base/src/test/resources";

    public static String getProperty(String fileName, String propertyKey) {
        Properties prop = new Properties();
        try {
            prop.load(new FileReader(String.format("%s/%s",RESOURCE_DIRECTORY,fileName)));
        } catch (IOException e) {
            throw new FileOrResourceNotFoundException(EXCEPTION_MESSAGE);
        }
        return prop.getProperty(propertyKey);
    }
}
