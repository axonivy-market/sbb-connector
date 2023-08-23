package com.axonivy.connector.sbb.webtest.trip;

import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.engine.EngineUrl;
import com.codeborne.selenide.ElementsCollection;


@IvyWebTest
class ITBookTrip {

  private static final SimpleDateFormat DATE_FORMATTER_PRESENTABLE = new SimpleDateFormat("dd.MM.yyyy");
  private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
  private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");
  private static final ZoneOffset SWISS_ZONE_OFFSET = ZoneId.of("Europe/Zurich").getRules().getOffset(Instant.now());

  @Test
  void start_showInputTripSearchDataDialog_initialFieldValuesArePresent() {
    open(EngineUrl.createProcessUrl("sbb-connector-demo/189FEADF3244D108/start.ivp"));
    Date currentDate = new Date();

    // Assert
    $(By.id("form:from_input")).shouldHave(value(""));
    $(By.id("form:to_input")).shouldHave(value(""));
    $(By.id("form:date_input")).shouldHave(value(DATE_FORMATTER_PRESENTABLE.format(currentDate)));
    $(By.id("form:time_input")).shouldHave(value(TIME_FORMATTER.format(currentDate)));
    $(By.id("form:departureOrArrival_input")).shouldNotBe(checked);
  }

  @Test
  void start_inputTripSearchDataFieldsAreEmpty_proceedingShowsErrorMessages() {
    open(EngineUrl.createProcessUrl("sbb-connector-demo/189FEADF3244D108/start.ivp"));

    // Empty fields
    $(By.id("form:from_input")).clear();
    $(By.id("form:to_input")).clear();
    $(By.id("form:date_input")).sendKeys(Keys.CONTROL, "A", Keys.DELETE);
    $(By.id("form:time_input")).sendKeys(Keys.CONTROL, "A", Keys.DELETE);

    // Proceed
    $(By.id("form:proceed")).click();

    // Assert
    ElementsCollection errorMessages = $$(By.cssSelector(".ui-messages-error-summary"));

    assertThat(errorMessages).hasSize(4);
    errorMessages.get(0).shouldHave(text("From: Value is required."));
    errorMessages.get(1).shouldHave(text("To: Value is required."));
    errorMessages.get(2).shouldHave(text("Date: Value is required."));
    errorMessages.get(3).shouldHave(text("Time: Value is required."));
  }

  @Test
  void start_inputTripSearchDataFieldsDateAndTimeHaveInvalidContent_proceedingShowsErrorMessages() {
    open(EngineUrl.createProcessUrl("sbb-connector-demo/189FEADF3244D108/start.ivp"));

    // Insert invalid content into fields Date and Time
    $(By.id("form:date_input")).sendKeys(Keys.CONTROL, "A", Keys.DELETE);
    $(By.id("form:time_input")).sendKeys(Keys.CONTROL, "A", Keys.DELETE);

    $(By.id("form:from_input")).setValue("A");
    $(By.id("form:to_input")).setValue("B");
    $(By.id("form:date_input")).setValue("C");
    $(By.id("form:time_input")).setValue("D");

    // Proceed
    $(By.id("form:proceed")).click();

    // Assert
    ElementsCollection errorMessages = $$(By.cssSelector(".ui-messages-error-summary"));

    assertThat(errorMessages).hasSize(2);
    errorMessages.get(0).shouldHave(text("Date: 'C' could not be understood as a date."));
    errorMessages.get(1).shouldHave(text("Time: 'D' could not be understood as a time."));
  }

  @Test
  void start_inputTripSearchDataFieldsFromAndToLocationsHaveNotBeenSelected_proceedingShowsErrorMessages() {
    open(EngineUrl.createProcessUrl("sbb-connector-demo/189FEADF3244D108/start.ivp"));

    // Insert content into fields From and To without selecting Location
    $(By.id("form:from_input")).setValue("A");
    $(By.id("form:to_input")).setValue("B");

    // Proceed
    $(By.id("form:proceed")).click();

    // Assert
    ElementsCollection errorMessages = $$(By.cssSelector(".ui-messages-error-summary"));

    assertThat(errorMessages).hasSize(2);
    errorMessages.get(0).shouldHave(text("From: Choose a Location from the Dropdown Menu"));
    errorMessages.get(1).shouldHave(text("To: Choose a Location from the Dropdown Menu"));
  }

  @Test
  void start_proceedFromInputTripSearchDataWithOptionEarliestDeparture_showShowTripsDialog() {
    open(EngineUrl.createProcessUrl("sbb-connector-demo/189FEADF3244D108/start.ivp"));
    Date currentDate = new Date();

    // Fill fields of InputTripSearchData dialog
    $(By.id("form:from_input")).setValue("AA");
    $(By.id("form:from_panel")).click();

    $(By.id("form:to_input")).setValue("BB");
    $(By.id("form:to_panel")).click();

    $(By.id("form:time_input")).sendKeys(Keys.CONTROL, "A", Keys.DELETE);
    $(By.id("form:time_input")).setValue("10:00");

    // Proceed
    $(By.id("form:proceed")).click();

    // Assert
    $(By.id("form:trips:0:departureDate")).shouldHave(text(DATE_FORMATTER_PRESENTABLE.format(currentDate)));
    assertThat($(By.id("form:trips:0:departureTime")).innerHtml().matches("10:00  .")).isTrue();
    assertThat($(By.id("form:trips:0:departureTime")).getAttribute("class").split(" ")).doesNotContain("red");
    assertThat($(By.id("form:trips:0:arrivalDate")).exists()).isFalse();
    $(By.id("form:trips:0:arrivalTime")).shouldHave(text("11:00"));
    assertThat($(By.id("form:trips:0:arrivalTime")).getAttribute("class").split(" ")).doesNotContain("red");
    $(By.id("form:trips:0:duration")).shouldHave(text("Duration: 01:00"));

    assertThat($(By.id("icon")).getAttribute("class")).isEqualTo("fa-solid fa-train");

    $(By.id("form:trips:0:segments:0:departureName")).shouldHave(text("Bern"));
    $(By.id("form:trips:0:segments:0:departureDate")).shouldHave(text(DATE_FORMATTER_PRESENTABLE.format(currentDate)));
    $(By.id("form:trips:0:segments:0:departureTime")).shouldHave(text("10:00"));
    assertThat($(By.id("form:trips:0:segments:0:departureTime")).getAttribute("class").split(" "))
            .doesNotContain("red");
    $(By.id("form:trips:0:segments:0:departurePlatform")).shouldHave(text("Platform: 7"));
    assertThat($(By.id("form:trips:0:segments:0:departurePlatform")).getAttribute("class").split(" "))
            .doesNotContain("red");

    assertThat($(By.id("form:trips:0:segments:0:arrivalName")).innerHtml().matches("Z.rich HB")).isTrue();
    $(By.id("form:trips:0:segments:0:arrivalDate")).shouldBe(empty);
    $(By.id("form:trips:0:segments:0:arrivalTime")).shouldHave(text("11:00"));
    assertThat($(By.id("form:trips:0:segments:0:arrivalTime")).getAttribute("class").split(" "))
            .doesNotContain("red");
    $(By.id("form:trips:0:segments:0:arrivalPlatform")).shouldHave(text("Platform: 19"));
    assertThat($(By.id("form:trips:0:segments:0:arrivalPlatform")).getAttribute("class").split(" "))
            .contains("red");

    $(By.id("form:trips:0:segments:0:transportProductName")).shouldHave(text("IC 721"));
    $(By.id("form:trips:0:segments:0:direction")).shouldHave(text("Direction: St. Gallen"));

    $(By.id("form:trips:0:onlineOffersLink")).shouldHave(text("Online Offers"));
    assertThat($(By.id("form:trips:0:onlineOffersLink")).getAttribute("href")).isEqualTo(String.format(
            """
                    https://www.sbb.ch/en/buying/pages/fahrplan/fahrplan.xhtml?recon=wQg8CgJWMRKRBSVDMiVCNkhLSQkJgFQlMjRBJTNEMSU0ME8lM0RCZXJuJTQwWCU\
                    zRDc0MzkxMwEZdFklM0Q0Njk0ODgzNCU0MEwlM0Q4NTA3MDAwJTQwYQE-EDI4JTQwOksAEEFhcmF1DUwgODA1MTI2OSU0CUwANwFaADYdTAwyMTEzPkwANDIwMjAwODI\
                    zMTAzNCUyGQ8BKywyNElSKzE2KysrJTIBHQAxCQcNBhBDMiVBNz7aAP6PAC4mATRaJUMzJUJDcmljaCtIQhHkBDU0AcgZ5BA3ODE3NzkwADNKMAEV5AgxMjMy5AAENTI\
                    J5AQzN2bkAAxCNktDAexAQjYlMjNWRSUyMzAlMjNDRiUhNAULAEERFABNDQkMU0lDVAEfCCUyMwk6BEtDSkMAMEVSRyUyMzIlMjNISU4NOjhFQ0slMjMzNjYzOTQlN0M\
                    uCQAINDcyCRIJCQgwJTcFBBA2NTcwMQkZBDM4ASIBDSQtMjE0NzQ4MzYzCScoMjMa3gEKOAoSCgdNUygSB1NUQVRJT04SGSEemC0wOC0yM1QxMDozNDowMCswMjowMCI\
                    HUExBTk5FRAobCg4KAzEzMhU2FRsMKAEKHw1XDDAyMThKIQAIAgo6DSFBfxUhABoyeAAMMToxM0Z4ALgoAxIYEAMaBjAwMDAxMSICSVIqAjE2MgQyMTY5Kg5QVUJMSUN\
                    fSk9VUk5FWRrFAVZpADbhAAgxOjJSaQAVxgwyMTE5FaUZ5wAEFV1BYRUhPsYAADUhODo-ARwoBRIaCAMQBTbIABwzNzIEMjI2Nz7IAA==&datum=%s%s&zeit=1000\
                    """,
            DATE_FORMATTER.format(currentDate),
            SWISS_ZONE_OFFSET.toString()));
  }

  @Test
  void start_proceedFromInputTripSearchDataWithOptionLatestArrival_showShowTripsDialog() {
    open(EngineUrl.createProcessUrl("sbb-connector-demo/189FEADF3244D108/start.ivp"));
    Date currentDate = new Date();

    // Fill fields of InputTripSearchData dialog
    $(By.id("form:departureOrArrival")).click();

    $(By.id("form:from_input")).setValue("AA");
    $(By.id("form:from_panel")).click();

    $(By.id("form:to_input")).setValue("BB");
    $(By.id("form:to_panel")).click();

    $(By.id("form:time_input")).sendKeys(Keys.CONTROL, "A", Keys.DELETE);
    $(By.id("form:time_input")).setValue("10:00");

    // Proceed
    $(By.id("form:proceed")).click();

    // Assert
    $(By.id("form:trips:0:departureDate")).shouldHave(text(DATE_FORMATTER_PRESENTABLE.format(currentDate)));
    assertThat($(By.id("form:trips:0:departureTime")).innerHtml().matches("09:00  .")).isTrue();
    assertThat($(By.id("form:trips:0:departureTime")).getAttribute("class").split(" ")).doesNotContain("red");
    assertThat($(By.id("form:trips:0:arrivalDate")).exists()).isFalse();
    $(By.id("form:trips:0:arrivalTime")).shouldHave(text("10:00"));
    assertThat($(By.id("form:trips:0:arrivalTime")).getAttribute("class").split(" ")).doesNotContain("red");
    $(By.id("form:trips:0:duration")).shouldHave(text("Duration: 01:00"));

    assertThat($(By.id("icon")).getAttribute("class")).isEqualTo("fa-solid fa-train");

    $(By.id("form:trips:0:segments:0:departureName")).shouldHave(text("Bern"));
    $(By.id("form:trips:0:segments:0:departureDate")).shouldHave(text(DATE_FORMATTER_PRESENTABLE.format(currentDate)));
    $(By.id("form:trips:0:segments:0:departureTime")).shouldHave(text("09:00"));
    assertThat($(By.id("form:trips:0:segments:0:departureTime")).getAttribute("class").split(" "))
            .doesNotContain("red");
    $(By.id("form:trips:0:segments:0:departurePlatform")).shouldHave(text("Platform: 7"));
    assertThat($(By.id("form:trips:0:segments:0:departurePlatform")).getAttribute("class").split(" "))
            .doesNotContain("red");

    assertThat($(By.id("form:trips:0:segments:0:arrivalName")).innerHtml().matches("Z.rich HB")).isTrue();
    $(By.id("form:trips:0:segments:0:arrivalDate")).shouldBe(empty);
    $(By.id("form:trips:0:segments:0:arrivalTime")).shouldHave(text("10:00"));
    assertThat($(By.id("form:trips:0:segments:0:arrivalTime")).getAttribute("class").split(" "))
            .doesNotContain("red");
    $(By.id("form:trips:0:segments:0:arrivalPlatform")).shouldHave(text("Platform: 19"));
    assertThat($(By.id("form:trips:0:segments:0:arrivalPlatform")).getAttribute("class").split(" "))
            .contains("red");

    $(By.id("form:trips:0:segments:0:transportProductName")).shouldHave(text("IC 721"));
    $(By.id("form:trips:0:segments:0:direction")).shouldHave(text("Direction: St. Gallen"));

    $(By.id("form:trips:0:onlineOffersLink")).shouldHave(text("Online Offers"));
    assertThat($(By.id("form:trips:0:onlineOffersLink")).getAttribute("href")).isEqualTo(String.format(
            """
                    https://www.sbb.ch/en/buying/pages/fahrplan/fahrplan.xhtml?recon=wQg8CgJWMRKRBSVDMiVCNkhLSQkJgFQlMjRBJTNEMSU0ME8lM0RCZXJuJTQwWCU\
                    zRDc0MzkxMwEZdFklM0Q0Njk0ODgzNCU0MEwlM0Q4NTA3MDAwJTQwYQE-EDI4JTQwOksAEEFhcmF1DUwgODA1MTI2OSU0CUwANwFaADYdTAwyMTEzPkwANDIwMjAwODI\
                    zMTAzNCUyGQ8BKywyNElSKzE2KysrJTIBHQAxCQcNBhBDMiVBNz7aAP6PAC4mATRaJUMzJUJDcmljaCtIQhHkBDU0AcgZ5BA3ODE3NzkwADNKMAEV5AgxMjMy5AAENTI\
                    J5AQzN2bkAAxCNktDAexAQjYlMjNWRSUyMzAlMjNDRiUhNAULAEERFABNDQkMU0lDVAEfCCUyMwk6BEtDSkMAMEVSRyUyMzIlMjNISU4NOjhFQ0slMjMzNjYzOTQlN0M\
                    uCQAINDcyCRIJCQgwJTcFBBA2NTcwMQkZBDM4ASIBDSQtMjE0NzQ4MzYzCScoMjMa3gEKOAoSCgdNUygSB1NUQVRJT04SGSEemC0wOC0yM1QxMDozNDowMCswMjowMCI\
                    HUExBTk5FRAobCg4KAzEzMhU2FRsMKAEKHw1XDDAyMThKIQAIAgo6DSFBfxUhABoyeAAMMToxM0Z4ALgoAxIYEAMaBjAwMDAxMSICSVIqAjE2MgQyMTY5Kg5QVUJMSUN\
                    fSk9VUk5FWRrFAVZpADbhAAgxOjJSaQAVxgwyMTE5FaUZ5wAEFV1BYRUhPsYAADUhODo-ARwoBRIaCAMQBTbIABwzNzIEMjI2Nz7IAA==&datum=%s%s&zeit=0900\
                    """,
            DATE_FORMATTER.format(currentDate),
            SWISS_ZONE_OFFSET.toString()));
  }

  @Test
  void start_proceedFromInputTripSearchDataWithDayChangeDuringTrip_showShowTripsDialog() {
    open(EngineUrl.createProcessUrl("sbb-connector-demo/189FEADF3244D108/start.ivp"));
    Date currentDate = new Date();

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(currentDate);
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    Date tomorrowDate = calendar.getTime();

    // Fill fields of InputTripSearchData dialog
    $(By.id("form:from_input")).setValue("AA");
    $(By.id("form:from_panel")).click();

    $(By.id("form:to_input")).setValue("BB");
    $(By.id("form:to_panel")).click();

    $(By.id("form:time_input")).sendKeys(Keys.CONTROL, "A", Keys.DELETE);
    $(By.id("form:time_input")).setValue("23:30");

    // Proceed
    $(By.id("form:proceed")).click();

    // Assert
    $(By.id("form:trips:0:departureDate")).shouldHave(text(DATE_FORMATTER_PRESENTABLE.format(currentDate)));
    assertThat($(By.id("form:trips:0:departureTime")).innerHtml().matches("23:30  .")).isTrue();
    assertThat($(By.id("form:trips:0:departureTime")).getAttribute("class").split(" ")).doesNotContain("red");
    $(By.id("form:trips:0:arrivalDate")).shouldHave(text(DATE_FORMATTER_PRESENTABLE.format(tomorrowDate)));
    $(By.id("form:trips:0:arrivalTime")).shouldHave(text("00:30"));
    assertThat($(By.id("form:trips:0:arrivalTime")).getAttribute("class").split(" ")).doesNotContain("red");
    $(By.id("form:trips:0:duration")).shouldHave(text("Duration: 01:00"));

    assertThat($(By.id("icon")).getAttribute("class")).isEqualTo("fa-solid fa-train");

    $(By.id("form:trips:0:segments:0:departureName")).shouldHave(text("Bern"));
    $(By.id("form:trips:0:segments:0:departureDate")).shouldHave(text(DATE_FORMATTER_PRESENTABLE.format(currentDate)));
    $(By.id("form:trips:0:segments:0:departureTime")).shouldHave(text("23:30"));
    assertThat($(By.id("form:trips:0:segments:0:departureTime")).getAttribute("class").split(" "))
            .doesNotContain("red");
    $(By.id("form:trips:0:segments:0:departurePlatform")).shouldHave(text("Platform: 7"));
    assertThat($(By.id("form:trips:0:segments:0:departurePlatform")).getAttribute("class").split(" "))
            .doesNotContain("red");

    assertThat($(By.id("form:trips:0:segments:0:arrivalName")).innerHtml().matches("Z.rich HB")).isTrue();
    $(By.id("form:trips:0:segments:0:arrivalDate")).shouldHave(text(DATE_FORMATTER_PRESENTABLE.format(tomorrowDate)));
    $(By.id("form:trips:0:segments:0:arrivalTime")).shouldHave(text("00:30"));
    assertThat($(By.id("form:trips:0:segments:0:arrivalTime")).getAttribute("class").split(" "))
            .doesNotContain("red");
    $(By.id("form:trips:0:segments:0:arrivalPlatform")).shouldHave(text("Platform: 19"));
    assertThat($(By.id("form:trips:0:segments:0:arrivalPlatform")).getAttribute("class").split(" "))
            .contains("red");

    $(By.id("form:trips:0:segments:0:transportProductName")).shouldHave(text("IC 721"));
    $(By.id("form:trips:0:segments:0:direction")).shouldHave(text("Direction: St. Gallen"));

    $(By.id("form:trips:0:onlineOffersLink")).shouldHave(text("Online Offers"));
    assertThat($(By.id("form:trips:0:onlineOffersLink")).getAttribute("href")).isEqualTo(String.format(
            """
                    https://www.sbb.ch/en/buying/pages/fahrplan/fahrplan.xhtml?recon=wQg8CgJWMRKRBSVDMiVCNkhLSQkJgFQlMjRBJTNEMSU0ME8lM0RCZXJuJTQwWCU\
                    zRDc0MzkxMwEZdFklM0Q0Njk0ODgzNCU0MEwlM0Q4NTA3MDAwJTQwYQE-EDI4JTQwOksAEEFhcmF1DUwgODA1MTI2OSU0CUwANwFaADYdTAwyMTEzPkwANDIwMjAwODI\
                    zMTAzNCUyGQ8BKywyNElSKzE2KysrJTIBHQAxCQcNBhBDMiVBNz7aAP6PAC4mATRaJUMzJUJDcmljaCtIQhHkBDU0AcgZ5BA3ODE3NzkwADNKMAEV5AgxMjMy5AAENTI\
                    J5AQzN2bkAAxCNktDAexAQjYlMjNWRSUyMzAlMjNDRiUhNAULAEERFABNDQkMU0lDVAEfCCUyMwk6BEtDSkMAMEVSRyUyMzIlMjNISU4NOjhFQ0slMjMzNjYzOTQlN0M\
                    uCQAINDcyCRIJCQgwJTcFBBA2NTcwMQkZBDM4ASIBDSQtMjE0NzQ4MzYzCScoMjMa3gEKOAoSCgdNUygSB1NUQVRJT04SGSEemC0wOC0yM1QxMDozNDowMCswMjowMCI\
                    HUExBTk5FRAobCg4KAzEzMhU2FRsMKAEKHw1XDDAyMThKIQAIAgo6DSFBfxUhABoyeAAMMToxM0Z4ALgoAxIYEAMaBjAwMDAxMSICSVIqAjE2MgQyMTY5Kg5QVUJMSUN\
                    fSk9VUk5FWRrFAVZpADbhAAgxOjJSaQAVxgwyMTE5FaUZ5wAEFV1BYRUhPsYAADUhODo-ARwoBRIaCAMQBTbIABwzNzIEMjI2Nz7IAA==&datum=%s%s&zeit=2330\
                    """,
            DATE_FORMATTER.format(currentDate),
            SWISS_ZONE_OFFSET.toString()));
  }
}
