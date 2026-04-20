package by.nikita.recipebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
public class RecipebookApplication {

    public static void main(String[] args) {
        configureDatasourceFromEnvironment();
        SpringApplication.run(RecipebookApplication.class, args);
    }

    private static void configureDatasourceFromEnvironment() {
        String dbUrl = System.getenv("DB_URL");

        if (dbUrl == null || dbUrl.isBlank()) {
            return;
        }

        if (dbUrl.startsWith("jdbc:postgresql://")) {
            System.setProperty("spring.datasource.url", dbUrl);
            return;
        }

        if (dbUrl.startsWith("postgres://")) {
            dbUrl = "postgresql://" + dbUrl.substring("postgres://".length());
        }

        if (!dbUrl.startsWith("postgresql://")) {
            return;
        }

        try {
            URI uri = new URI(dbUrl);
            String username = System.getenv("DB_USERNAME");
            String password = System.getenv("DB_PASSWORD");

            if ((username == null || username.isBlank() || password == null || password.isBlank())
                && uri.getUserInfo() != null) {
                String[] credentials = uri.getUserInfo().split(":", 2);
                if (credentials.length > 0 && !credentials[0].isBlank()) {
                    username = credentials[0];
                }
                if (credentials.length > 1 && !credentials[1].isBlank()) {
                    password = credentials[1];
                }
            }

            String jdbcUrl = "jdbc:postgresql://" + uri.getHost();
            if (uri.getPort() != -1) {
                jdbcUrl += ":" + uri.getPort();
            }
            jdbcUrl += uri.getPath();
            if (uri.getRawQuery() != null && !uri.getRawQuery().isBlank()) {
                jdbcUrl += "?" + uri.getRawQuery();
            }

            System.setProperty("spring.datasource.url", jdbcUrl);

            if (username != null && !username.isBlank()) {
                System.setProperty("spring.datasource.username", username);
            }

            if (password != null && !password.isBlank()) {
                System.setProperty("spring.datasource.password", password);
            }
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Invalid DB_URL value: " + dbUrl, exception);
        }
    }
}
