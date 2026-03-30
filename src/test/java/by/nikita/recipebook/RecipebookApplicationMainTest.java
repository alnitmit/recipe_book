package by.nikita.recipebook;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class RecipebookApplicationMainTest {

    @Test
    void mainShouldDelegateToSpringApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            RecipebookApplication.main(new String[]{"--test"});
            springApplication.verify(() -> SpringApplication.run(RecipebookApplication.class, new String[]{"--test"}));
        }
    }
}
