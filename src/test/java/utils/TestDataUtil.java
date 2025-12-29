package utils;

import java.io.InputStream;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;

public class TestDataUtil {

     private static final ObjectMapper mapper = new ObjectMapper();


    public static <T> T readJson(String fileName, Class<T> clazz) {
        try {
            InputStream is = TestDataUtil.class
                    .getClassLoader()
                    .getResourceAsStream(fileName);

            if (is == null) {
                throw new RuntimeException("JSON file not found: " + fileName);
            }

            return mapper.readValue(is, clazz);

        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: " + fileName, e);
        }
    }
    
}
