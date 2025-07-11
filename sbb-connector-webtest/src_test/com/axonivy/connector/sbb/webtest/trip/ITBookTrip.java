package com.axonivy.connector.sbb.webtest.trip;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.engine.EngineUrl;
import com.axonivy.ivy.webtest.engine.WebAppFixture;
import com.codeborne.selenide.ElementsCollection;

@IvyWebTest
class ITBookTrip {

	private static final SimpleDateFormat DATE_FORMATTER_PRESENTABLE = new SimpleDateFormat("dd.MM.yyyy");
	private static final SimpleDateFormat DATE_TIME_FORMATTER_PRESENTABLE = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	private static final String MOCK_DATE_VALUE = "10.06.2023";
	private static final String ERROR_MESSAGE_CLASS = ".ui-messages-error-summary";
	private static final String CLIENT_ID_VARIABLE = "sbbConnector.clientId";
	private static final String CLIENT_ID_VALUE = "DEMO";
	private static final String JOURNEY_URI_VARIABLE = "sbbConnector.journeyUri";
	private static final String JOURNEY_URI_VALUE = "https://smapi-osdm-journey-mock.app.sbb.ch";

	@BeforeEach
	void startDemoProcess(WebAppFixture fixture) {
		fixture.var(CLIENT_ID_VARIABLE, CLIENT_ID_VALUE);
		fixture.var(JOURNEY_URI_VARIABLE, JOURNEY_URI_VALUE);
		open(EngineUrl.createProcessUrl("sbb-connector-demo/189FEADF3244D108/start.ivp"));
	}

	@Test
	void start_inputTripSearchDataFieldsAreEmpty_proceedingShowsErrorMessages() {
		processToShowTrip();

		// Assert
		ElementsCollection errorMessages = $$(By.cssSelector(ERROR_MESSAGE_CLASS));
		assertThat(errorMessages).hasSize(4);
		errorMessages.get(0).shouldHave(text("From: Value is required."));
		errorMessages.get(1).shouldHave(text("To: Value is required."));
		errorMessages.get(2).shouldHave(text("Departure date: Value is required."));
		errorMessages.get(3).shouldHave(text("Arrival date: Value is required."));
	}

	@Test
	void start_inputTripSearchDataFieldsDateAndTimeHaveInvalidContent_proceedingShowsErrorMessages() {
		selectFromAndTo();

		// Insert invalid content into fields Date and Time
		$(By.id("form:departure-date_input")).sendKeys(Keys.CONTROL, "A", Keys.DELETE);
		$(By.id("form:departure-date_input")).setValue("C");

		$(By.id("form:arrival-date_input")).sendKeys(Keys.CONTROL, "A", Keys.DELETE);
		$(By.id("form:arrival-date_input")).setValue("D");

		processToShowTrip();

		// Assert
		ElementsCollection errorMessages = $$(By.cssSelector(ERROR_MESSAGE_CLASS));
		assertThat(errorMessages).hasSize(2);
		errorMessages.get(0).shouldHave(text("Departure date: 'C' could not be understood as a date and time."));
		errorMessages.get(1).shouldHave(text("Arrival date: 'D' could not be understood as a date and time."));
	}

	@Test
	void start_proceedFromInputTripSearchDataWithOptionEarliestDeparture_showShowTripsDialog() throws ParseException {
		Date date = DATE_FORMATTER_PRESENTABLE.parse(MOCK_DATE_VALUE);
		String dateStr = DATE_TIME_FORMATTER_PRESENTABLE.format(date);

		selectFromAndTo();

		$(By.id("form:departure-date_input")).setValue(dateStr);
		$(By.id("form:arrival-date_input")).setValue(dateStr);

		processToShowTrip();

		// Assert
		assertThat($(By.id("icon")).getAttribute("class")).isEqualTo("fa-solid fa-train-tram");
		$(By.id("form:trips:0:departure-date")).shouldHave(text(MOCK_DATE_VALUE));
		$(By.id("form:trips:0:duration")).shouldHave(text("Duration: 00:15"));
		$(By.id("form:trips:0:segments:0:departure-name")).shouldHave(text("BurgdorfGleis 2"));
		$(By.id("form:trips:0:segments:0:arrival-name")).shouldHave(text("BernGleis 9"));
		$(By.id("form:trips:0:segments:0:departure-date")).shouldHave(text(MOCK_DATE_VALUE));
		$(By.id("form:trips:0:segments:0:arrival-date")).shouldHave(text(MOCK_DATE_VALUE));
		$(By.id("form:trips:0:segments:0:direction")).shouldHave(text("Out bound"));
		$(By.id("form:trips:0:segments:0:transport-name")).shouldHave(text("Zug"));
		$(By.id("form:trips:0:segments:0:published-service-name")).shouldHave(text("IR 17 2828"));
		$(By.id("form:trips:0:segments:0:transport-description")).shouldHave(text("InterRegio"));
	}

	private void selectFromAndTo() {
		$(By.id("form:from_input")).setValue("Bern");
		$(By.id("form:from_panel")).click();
		$(By.id("form:to_input")).setValue("Burg");
		$(By.id("form:to_panel")).click();
	}

	private void processToShowTrip() {
		$(By.id("form:proceed")).click();
	}
}
