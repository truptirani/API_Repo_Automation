package utils;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonUtils {

    public static String getJson(String fileName) {
        try {
            return new String(
                Files.readAllBytes(
                    Paths.get("src/test/resources/testdata/" + fileName)
                )
            );
        } catch (Exception e) {
            throw new RuntimeException("Unable to read JSON file: " + fileName);
        }
    }
}


