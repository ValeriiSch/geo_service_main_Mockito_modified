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


public class TestGeoServiceImpl {
    @BeforeAll
    public static void initSuite() {
        System.out.println("Running Tests");
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("\nAll tests complete");
    }


    @ParameterizedTest
    @MethodSource("args_Ip")
    public void test_Location_byIp(String ip, Location expected) {
        GeoServiceImpl geoService = new GeoServiceImpl();

        Location result = geoService.byIp(ip);

        if (expected == null) Assertions.assertNull(result);
        else {
            Assertions.assertAll("Сценарий сравнения ip адреса",
                    () -> Assertions.assertEquals(expected.getCity(), result.getCity()),
                    () -> Assertions.assertEquals(expected.getCountry(), result.getCountry()),
                    () -> Assertions.assertEquals(expected.getStreet(), result.getStreet()),
                    () -> Assertions.assertEquals(expected.getBuiling(), result.getBuiling())
            );
        }
    }

    @ParameterizedTest
    @MethodSource("args_coordinates")
    public void test_Location_byCoordinates(double latitude, double longitude) {
        GeoServiceImpl geoService = new GeoServiceImpl();
        var expected = RuntimeException.class;

        Assertions.assertThrows(expected, () -> geoService.byCoordinates(latitude, longitude));
    }

    @ParameterizedTest
    @MethodSource("args_country")
    public void test_locale(Country country, String expected) {
        LocalizationServiceImpl localizationService = new LocalizationServiceImpl();

        String result = localizationService.locale(country);

        Assertions.assertEquals(expected, result);
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


    private static Stream<Arguments> args_Ip() {
        return Stream.of(Arguments.of("127.0.0.1", new Location(null, null, null, 0)),
                Arguments.of("172.0.32.11", new Location("Moscow", Country.RUSSIA, "Lenina", 15)),
                Arguments.of("96.44.183.149", new Location("New York", Country.USA, " 10th Avenue", 32)),
                Arguments.of("172.0.0.0", new Location("Moscow", Country.RUSSIA, null, 0)),
                Arguments.of("96.0.0.0", new Location("New York", Country.USA, null, 0)),
                Arguments.of("0.0.0.1", null));
    }

    private static Stream<Arguments> args_coordinates() {
        return Stream.of(Arguments.of(15.0, 25.0), Arguments.of(5.5, -2.0), Arguments.of(0.0, 0.0));
    }

    private static Stream<Arguments> args_country() {
        return Stream.of(Arguments.of(Country.RUSSIA, "Добро пожаловать"),
                Arguments.of(Country.GERMANY, "Welcome"),
                Arguments.of(Country.USA, "Welcome"),
                Arguments.of(Country.BRAZIL, "Welcome"));
    }

    private static Stream<Arguments> args_send() {
        return Stream.of(Arguments.of("100.0.0.0", "Добро пожаловать"),
                Arguments.of("", "Welcome"));
    }


}