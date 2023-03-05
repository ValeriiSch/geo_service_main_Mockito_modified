import ru.netology.entity.Country;
import ru.netology.i18n.LocalizationServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

public class TestLocalizationServiceImpl {
    @BeforeAll
    public static void initSuite() {
        System.out.println("Running Tests");
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("\nAll tests complete");
    }

    @ParameterizedTest
    @MethodSource("args_country")
    public void test_locale(Country country, String expected) {
        LocalizationServiceImpl localizationService = new LocalizationServiceImpl();

        String result = localizationService.locale(country);

        Assertions.assertEquals(expected, result);
    }

    private static Stream<Arguments> args_country() {
        return Stream.of(Arguments.of(Country.RUSSIA, "Добро пожаловать"),
                Arguments.of(Country.GERMANY, "Welcome"),
                Arguments.of(Country.USA, "Welcome"),
                Arguments.of(Country.BRAZIL, "Welcome"));
    }
}
