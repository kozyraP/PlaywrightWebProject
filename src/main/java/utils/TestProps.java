package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestProps {
    private Logger log = LogManager.getLogger();
    private Properties prop;

    public TestProps() {
        this.prop = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("./src/main/resources/config.properties")) {
            prop.load(fileInputStream);
        } catch (IOException e) {
            log.error("Error while reading properties file ", e);
        }
    }

    public String getProperty(String key) {
        return prop.getProperty(key) != null ? prop.getProperty(key).trim() : null;
    }

    public void updateTestProperties() {
        prop.keySet().forEach(key -> {
            String propKey = (String) key;
            if (System.getProperty(propKey) != null)
                prop.setProperty(propKey, System.getProperty(propKey));
        });
    }
}