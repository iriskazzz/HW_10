import com.codeborne.selenide.CollectionCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class RZDTests {

  @BeforeEach
  void setup() {
    open("https://rzd.ru/");
  }

  @Tag("SMOKE")
  @ValueSource(strings = {"Пассажирам", "Грузовые перевозки", "Компания", "Работа в РЖД", "Контакты"})
  @ParameterizedTest(name = "При открытии раздела {0} доступны кнопки для работы с сайтом")
  void checkHeader(String menuSection) {
    $$(".header_site li").find(text(menuSection)).click();
    $(".header_specials").shouldBe(visible);
  }

  @Tag("REGRESS")
  @CsvSource(value = {
          "Пенза, Казань, 16.10.2023",
          "Пенза, Москва, 01.11.2023"
  })
  @ParameterizedTest(name = "Проверка что есть поезда по маршруту {0} - {1} {2}")
  void checkTimetable(String cityFrom, String cityTo, String dateFrom) {
    $$(".header_site li").find(text("Пассажирам")).click();
    $("#direction-from").setValue(cityFrom);
    $("[aria-label='" + cityFrom + "']").click();
    $("#direction-to").setValue(cityTo);
    $("[aria-label='" + cityTo + "']").click();
    $("#datepicker-from").setValue(dateFrom);
    $(".rzd-button").click();
    $$(".results-cards rzd-search-results-card-railway-flat-card")
            .shouldBe(CollectionCondition.sizeGreaterThan(0));
  }

  static Stream<Arguments> dataProviderForCheckMenuSection() {
    return Stream.of(
            Arguments.of("Пассажирам",
                    List.of("Пассажирам", "Грузовые перевозки", "Компания", "Работа в РЖД", "Контакты"),
                    List.of("Купить билет", "Поезда и маршруты", "Вокзалы", "Тарифы и акции", "Услуги и сервисы",
                            "РЖД Бонус", "Правила", "Информация", "Карта сайта")),
            Arguments.of("Версия для слабовидящих",
                    List.of("Пассажирам", "Грузовые перевозки", "Компания", "Работа в РЖД", "Контакты"),
                    List.of("Купить билет", "Поезда и маршруты", "Вокзалы", "Тарифы и акции", "Услуги и сервисы",
                            "РЖД Бонус", "Правила", "Информация", "Карта сайта"))
    );
  }

  @Tag("SMOKE")
  @MethodSource("dataProviderForCheckMenuSection")
  @ParameterizedTest(name = "Проверка отображения разделов {1} и {2} меню для режима {0}")
  void checkMenuSection(String version, List<String> menuHeader, List<String> menuActions) {
    $(".header").$(byText(version)).click();
    $$(".header_site li").shouldHave(texts(menuHeader));
    $$(".header_actions li").filter(visible).shouldHave(texts(menuActions));
  }

  @Tag("REGRESS")
  @EnumSource(Lang.class)
  @ParameterizedTest(name = "Для локали {0} отображается картинка")
  void selenideSiteEnumMenuTest(Lang lang) {
    $(".icon-locale").click();
    $(byText(lang.name())).click();
    $(".logo-link").shouldBe(visible);
  }
}