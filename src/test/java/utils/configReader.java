package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class configReader {
	
	public static Properties prop;
	
	public String getProperty(String properties) {
		Path localConfig = Path.of("src/test/resources/Testdata/config.properties");
		Path exampleConfig = Path.of("src/test/resources/Testdata/config.properties.example");
		Path configPath = Files.exists(localConfig) ? localConfig : exampleConfig;
		prop = new Properties();
		try (FileInputStream input = new FileInputStream(configPath.toFile())) {
			prop.load(input);
		} catch (IOException e) {
			throw new IllegalStateException("Could not load configuration from " + configPath.toAbsolutePath(), e);
		}

		String environmentVariable = switch (properties) {
			case "username" -> "CURA_USERNAME";
			case "password" -> "CURA_PASSWORD";
			case "wrong_username" -> "CURA_WRONG_USERNAME";
			case "wrong_password" -> "CURA_WRONG_PASSWORD";
			default -> null;
		};
		String value = environmentVariable == null ? null : System.getenv(environmentVariable);
		if (value == null || value.isBlank()) {
			value = prop.getProperty(properties);
		}
		if ((value == null || value.isBlank()) && environmentVariable != null) {
			throw new IllegalStateException("Set the " + environmentVariable + " environment variable before running tests.");
		}
		return value;
	}

}

