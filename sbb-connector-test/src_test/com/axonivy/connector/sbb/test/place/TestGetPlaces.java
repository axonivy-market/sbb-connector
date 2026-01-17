package com.axonivy.connector.sbb.test.place;

import static com.axonivy.connector.sbb.constant.Constant.PLACES;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.sbb.place.GetPlacesData;
import com.axonivy.connector.sbb.place.GetPlacesDataHeaders;
import com.axonivy.connector.sbb.place.GetPlacesDataIn;
import com.axonivy.connector.sbb.place.GetPlacesDataParameters;
import com.axonivy.connector.sbb.test.BaseTest;
import com.axonivy.connector.sbb.test.constant.Constant;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.engine.client.sub.SubProcessCallResult;
import ch.ivyteam.ivy.scripting.objects.List;
import ch.sbb.api.smapi.osdm.journey.client.OneOfPlaceResponsePlacesItems;

class TestGetPlaces extends BaseTest {

	private static final BpmProcess GET_PLACES_PROCESS = BpmProcess.path("GetPlaces");
	private static final BpmElement GET_PLACES_START = GET_PLACES_PROCESS.elementName("call(GetPlacesDataIn)");
	private static final BpmElement GET_PLACES = GET_PLACES_PROCESS.elementName("Get Places");
	private static final String MOCK_NAME_VALUE = "Bern";
	private static final double MOCK_POSITION_LATITUDE_VALUE = 100;
	private static final double MOCK_POSITION_LONGTITUDE_VALUE = 200;

	@Test
	void call_mockedApi_returnsMockedResponse(BpmClient bpmClient) {
		GetPlacesDataHeaders getPlacesDataHeaders = prepareGetPlacesDataHeaders();
		GetPlacesDataIn getPlacesDataIn = prepareGetPlacesDataIn(getPlacesDataHeaders);

		// Mocks
		List<OneOfPlaceResponsePlacesItems> places = List.create(OneOfPlaceResponsePlacesItems.class);

		bpmClient.mock().element(GET_PLACES).with(in -> {
			try {
				GetPlacesDataIn inGetPlacesDataIn = (GetPlacesDataIn) in.get(Constant.IN);
				GetPlacesDataHeaders inGetPlacesDataHeaders = (GetPlacesDataHeaders) inGetPlacesDataIn
						.get(Constant.HEADERS);
				assertEquals(inGetPlacesDataHeaders, getPlacesDataHeaders);
				assertEquals(inGetPlacesDataHeaders.get(Constant.REQUESTOR_PARAMETER), Constant.MOCK_REQUESTOR_VALUE);
				assertEquals(inGetPlacesDataHeaders.get(Constant.TRACEPARENT_PARAMETER),
						Constant.MOCK_TRACEPARENT_VALUE);
				assertEquals(inGetPlacesDataHeaders.get(Constant.TRACESTATE_PARAMETER), Constant.MOCK_TRACESTATE_VALUE);
				assertEquals(inGetPlacesDataHeaders.get(Constant.ACCEPT_LANGUAGE_PARAMETER),
						Constant.MOCK_ACCEPT_LANGUAGE_VALUE);
				GetPlacesDataParameters inGetPlacesDataInParameters = (GetPlacesDataParameters) inGetPlacesDataIn
						.get(Constant.PARAMS);
				assertEquals(inGetPlacesDataInParameters.getName(), MOCK_NAME_VALUE);
				assertEquals(inGetPlacesDataInParameters.getGeoPositionLatitude().doubleValue(),
						Double.valueOf(MOCK_POSITION_LATITUDE_VALUE));
				assertEquals(inGetPlacesDataInParameters.getGeoPositionLongitude().doubleValue(),
						Double.valueOf(MOCK_POSITION_LONGTITUDE_VALUE));

				in.set(PLACES, places);
				return in;
			} catch (NoSuchFieldException ex) {
				throw new RuntimeException(ex);
			}
		});

		// Run
		SubProcessCallResult result = bpmClient.start().subProcess(GET_PLACES_START).execute(getPlacesDataIn)
				.subResult();

		// Assert
		assertEquals(result.param(PLACES, List.class), places);
	}

	@Test
	void call_realApi_returnsRealResponse(BpmClient bpmClient) {
		GetPlacesDataHeaders getPlacesDataHeaders = prepareGetPlacesDataHeaders();
		GetPlacesDataIn getPlacesDataIn = prepareGetPlacesDataIn(getPlacesDataHeaders);
		// Run
		ExecutionResult result = bpmClient.start().subProcess(GET_PLACES_START).execute(getPlacesDataIn);
		GetPlacesData data = result.data().last();
		Assertions.assertEquals(data.getError().getAttribute("RestClientResponseStatusCode"), 503);
	}

	private GetPlacesDataIn prepareGetPlacesDataIn(GetPlacesDataHeaders getPlacesDataHeaders) {
		GetPlacesDataParameters getPlacesDataParameters = new GetPlacesDataParameters();
		getPlacesDataParameters.setName(MOCK_NAME_VALUE);
		getPlacesDataParameters.setGeoPositionLatitude(MOCK_POSITION_LATITUDE_VALUE);
		getPlacesDataParameters.setGeoPositionLongitude(MOCK_POSITION_LONGTITUDE_VALUE);

		GetPlacesDataIn getPlacesDataIn = new GetPlacesDataIn();
		getPlacesDataIn.setHeaders(getPlacesDataHeaders);
		getPlacesDataIn.setParams(getPlacesDataParameters);

		return getPlacesDataIn;
	}

	private GetPlacesDataHeaders prepareGetPlacesDataHeaders() {
		GetPlacesDataHeaders getPlacesDataHeaders = new GetPlacesDataHeaders();
		getPlacesDataHeaders.setRequestor(Constant.MOCK_REQUESTOR_VALUE);
		getPlacesDataHeaders.setTraceparent(Constant.MOCK_TRACEPARENT_VALUE);
		getPlacesDataHeaders.setTracestate(Constant.MOCK_TRACESTATE_VALUE);
		getPlacesDataHeaders.setAcceptLanguage(Constant.MOCK_ACCEPT_LANGUAGE_VALUE);
		return getPlacesDataHeaders;
	}

}
