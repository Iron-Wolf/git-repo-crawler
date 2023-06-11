package com.crawlab;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class RessourcesUtils {

    /**
     * Get a file from the resources folder.
     * Works everywhere, IDEA, unit test and JAR file.
     */
    public static InputStream getFileFromResourceAsStream(String fileName) {
        // The class loader that loaded the class
        ClassLoader classLoader = RessourcesUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    /**
     * Get a property file and return a {@link Properties} object loaded with the file
     */
    public static Properties getPropertiesFromFile(String fileName) {
        Properties prop = new Properties();
        try {
            var inputStream = RessourcesUtils.getFileFromResourceAsStream(fileName);
            prop.load(inputStream);
        } catch (IOException e) {
            LoggerManager.log.severe(e.getMessage());
            throw new RuntimeException(String.format("Error reading %s file", fileName));
        }
        return prop;
    }

    /**
     * Map a JSON object to a POJO
     */
    public static <T> T mapJson(JSONObject json, Class<T> valueType){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(
                    json.toString(),
                    valueType);

        } catch (IOException e) {
            LoggerManager.log.severe(e.getMessage());
            throw new RuntimeException("Error reading JSON");
        }
    }

    /**
     * Map a JSON object to a list of POJO
     */
    public static Object mapJsonToList(JSONArray json, Class valueType){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(
                    json.toString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, valueType));

        } catch (IOException e) {
            LoggerManager.log.severe(e.getMessage());
            throw new RuntimeException("Error reading JSON");
        }
    }
}
