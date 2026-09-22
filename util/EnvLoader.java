package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {

    private static final Map<String, String> ENV = new HashMap<>();

    static {
        loadEnvFile();
    }

    private static void loadEnvFile() {

        Path envPath = Paths.get(".env");

        if (!Files.exists(envPath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(envPath)) {

            String line;

            while ((line = reader.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int equalsIndex = line.indexOf('=');

                if (equalsIndex == -1) {
                    continue;
                }

                String key = line.substring(0, equalsIndex).trim();
                String value = line.substring(equalsIndex + 1).trim();

                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                ENV.put(key, value);
            }

        } catch (IOException e) {
            System.err.println("Could not read .env file.");
        }
    }

    public static String get(String key) {

        String value = ENV.get(key);

        if (value != null && !value.isBlank()) {
            return value;
        }

        return System.getenv(key);
    }
}