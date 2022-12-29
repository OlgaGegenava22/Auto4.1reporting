package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static java.time.Duration.ofSeconds;

class DeliveryTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }
    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");}

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $x("//*[@placeholder=\"Город\"]").setValue(validUser.getCity());
        $x("//*[@placeholder=\"Дата встречи\"]").sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE);
        $x("//*[@placeholder=\"Дата встречи\"]").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $x("//*[text()=\"Запланировать\"]").click();
        $("[data-test-id='success-notification']").shouldBe(visible, ofSeconds(15));
        $("[data-test-id='success-notification']>.notification__content").shouldHave(text("Встреча успешно запланирована на " + firstMeetingDate));
        $x("//*[@placeholder=\"Дата встречи\"]").sendKeys(Keys.chord(Keys.CONTROL, "a") + Keys.BACK_SPACE);
        $x("//*[@placeholder=\"Дата встречи\"]").setValue(secondMeetingDate);
        $x("//*[text()=\"Запланировать\"]").click();
        $("[data-test-id='replan-notification']").shouldBe(visible);
        $("[data-test-id='replan-notification']>.notification__content").shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $x("//*[text()=\"Перепланировать\"]").click();
        $("[data-test-id='success-notification']").shouldBe(Condition.visible);
        $("[data-test-id='success-notification']>.notification__content").shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate));
    }
}
