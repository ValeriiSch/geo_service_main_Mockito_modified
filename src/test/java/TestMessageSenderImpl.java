import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationServiceImpl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TestMessageSenderImpl {
    @BeforeAll
    public static void initSuite() {
        System.out.println("Running Tests");
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("\nAll tests complete");
    }

    @ParameterizedTest
    @MethodSource("args_send")
    public void test_MassageSender(String ipAddress, String expected) {
        GeoServiceImpl geoService = Mockito.mock(GeoServiceImpl.class);
        LocalizationServiceImpl localizationService = Mockito.spy(LocalizationServiceImpl.class);
        MessageSenderImpl messageSender = new MessageSenderImpl(geoService, localizationService);
        Map<String, String> map = new HashMap<>();
        map.put("x-real-ip", ipAddress);

        Mockito.when(geoService.byIp(Mockito.anyString()))
                .thenReturn(new Location("Moscow", Country.RUSSIA, "Lenina", 15));
        Mockito.when(localizationService.locale(Country.RUSSIA))
                .thenReturn("Добро пожаловать");
        String result = messageSender.send(map);

        Assertions.assertEquals(expected, result);

    }

    private static Stream<Arguments> args_send() {
        return Stream.of(Arguments.of("100.0.0.0", "Добро пожаловать"),
                Arguments.of("", "Welcome"));
    }
}
