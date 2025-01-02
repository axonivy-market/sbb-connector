package com.axonivy.connector.sbb.test.trip;

import static com.axonivy.connector.sbb.constant.Constant.TRIPS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.sbb.test.constant.Constant;
import com.axonivy.connector.sbb.tripscollection.GetTripsCollectionDataHeaders;
import com.axonivy.connector.sbb.tripscollection.GetTripsCollectionDataIn;
import com.axonivy.connector.sbb.tripscollection.GetTripsCollectionDataParameters;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.engine.client.sub.SubProcessCallResult;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.scripting.objects.List;
import ch.sbb.api.smapi.osdm.journey.client.StopPlaceRef;
import ch.sbb.api.smapi.osdm.journey.client.Trip;

@IvyProcessTest
class TestGetTrips {

	private static final BpmProcess GET_TRIPS_COLLECTION_PROCESS = BpmProcess.path("GetTripsCollection");
	private static final BpmElement GET_TRIPS_COLLECTION_START = GET_TRIPS_COLLECTION_PROCESS
			.elementName("call(GetTripsCollectionDataIn)");
	private static final BpmElement GET_TRIPS_COLLECTION = GET_TRIPS_COLLECTION_PROCESS
			.elementName("Get Trips Collection");

	private static final String MOCK_ARRIVAL_TIME = "5265-17-20T21:44:30";
	private static final String MOCK_DEPARTURE_TIME = "7184-02-28T22:32:10";
	private static final String MOCK_ORIGIN = "BernGleis 1";
	private static final String MOCK_DESTINATION = "BernGleis 10";

	@Test
	void call_mockedApi_returnsMockedResponse(BpmClient bpmClient) {
		GetTripsCollectionDataHeaders getTripsCollectionDataHeaders = prepareGetTripsCollectionDataHeaders();
		GetTripsCollectionDataIn getTripsCollectionPlacesDataIn = prepareGetTripsCollectionDataIn(
				getTripsCollectionDataHeaders);
		List<Trip> trips = List.create(Trip.class);
		bpmClient.mock().element(GET_TRIPS_COLLECTION).with(in -> {
			try {
				GetTripsCollectionDataIn inGetTripsCollectionDataIn = (GetTripsCollectionDataIn) in.get(Constant.IN);
				GetTripsCollectionDataHeaders inGetTripsCollectionDataHeaders = (GetTripsCollectionDataHeaders) inGetTripsCollectionDataIn
						.get(Constant.HEADERS);
				assertEquals(inGetTripsCollectionDataHeaders, getTripsCollectionDataHeaders);
				assertEquals(inGetTripsCollectionDataHeaders.get(Constant.REQUESTOR_PARAMETER),
						Constant.MOCK_REQUESTOR_VALUE);
				assertEquals(inGetTripsCollectionDataHeaders.get(Constant.TRACEPARENT_PARAMETER),
						Constant.MOCK_TRACEPARENT_VALUE);
				assertEquals(inGetTripsCollectionDataHeaders.get(Constant.TRACESTATE_PARAMETER),
						Constant.MOCK_TRACESTATE_VALUE);
				assertEquals(inGetTripsCollectionDataHeaders.get(Constant.ACCEPT_LANGUAGE_PARAMETER),
						Constant.MOCK_ACCEPT_LANGUAGE_VALUE);

				GetTripsCollectionDataParameters inGetTripsCollectionDataInParameters = (GetTripsCollectionDataParameters) inGetTripsCollectionDataIn
						.get(Constant.PARAMS);

				assertEquals(inGetTripsCollectionDataInParameters.getArrivalTime(), MOCK_ARRIVAL_TIME);
				assertEquals(inGetTripsCollectionDataInParameters.getDepartureTime(), MOCK_DEPARTURE_TIME);
				StopPlaceRef origin = (StopPlaceRef) inGetTripsCollectionDataInParameters.getOrigin();
				assertEquals(origin.getStopPlaceRef(), MOCK_ORIGIN);
				StopPlaceRef destination = (StopPlaceRef) inGetTripsCollectionDataInParameters.getDestination();
				assertEquals(destination.getStopPlaceRef(), MOCK_DESTINATION);
				in.set(TRIPS, trips);
				return in;
			} catch (NoSuchFieldException ex) {
				throw new RuntimeException(ex);
			}
		});

		// Run
		SubProcessCallResult result = bpmClient.start().subProcess(GET_TRIPS_COLLECTION_START)
				.execute(getTripsCollectionPlacesDataIn).subResult();

		// Assert
		assertEquals(result.param(TRIPS, List.class), trips);
	}

	@Test
	void call_realApi_returnsRealResponse(BpmClient bpmClient) {
		GetTripsCollectionDataHeaders getTripsCollectionDataHeaders = prepareGetTripsCollectionDataHeaders();
		GetTripsCollectionDataIn getTripsCollectionPlacesDataIn = prepareGetTripsCollectionDataIn(
				getTripsCollectionDataHeaders);

		// Run
		SubProcessCallResult result = bpmClient.start().subProcess(GET_TRIPS_COLLECTION_START)
				.execute(getTripsCollectionPlacesDataIn).subResult();

		// Assert
		Assertions.assertTrue(result.param(TRIPS, List.class).size() > 0);
		Trip trip = (Trip) result.param(TRIPS, List.class).get(0);
		Assertions.assertTrue(trip != null && trip.getLegs().size() > 0);
	}

	private GetTripsCollectionDataIn prepareGetTripsCollectionDataIn(
			GetTripsCollectionDataHeaders getTripsCollectionDataHeaders) {
		GetTripsCollectionDataParameters getTripsCollectionDataParameters = new GetTripsCollectionDataParameters();
		StopPlaceRef origin = new StopPlaceRef();
		origin.setStopPlaceRef(MOCK_ORIGIN);
		getTripsCollectionDataParameters.setOrigin(origin);

		StopPlaceRef destination = new StopPlaceRef();
		destination.setStopPlaceRef(MOCK_DESTINATION);
		getTripsCollectionDataParameters.setDestination(destination);

		getTripsCollectionDataParameters.setArrivalTime(MOCK_ARRIVAL_TIME);
		getTripsCollectionDataParameters.setDepartureTime(MOCK_DEPARTURE_TIME);

		GetTripsCollectionDataIn getTripsCollectionDataIn = new GetTripsCollectionDataIn();
		getTripsCollectionDataIn.setHeaders(getTripsCollectionDataHeaders);
		getTripsCollectionDataIn.setParams(getTripsCollectionDataParameters);

		return getTripsCollectionDataIn;
	}

	private GetTripsCollectionDataHeaders prepareGetTripsCollectionDataHeaders() {
		GetTripsCollectionDataHeaders getTripsCollectionDataHeaders = new GetTripsCollectionDataHeaders();
		getTripsCollectionDataHeaders.setRequestor(Constant.MOCK_REQUESTOR_VALUE);
		getTripsCollectionDataHeaders.setTraceparent(Constant.MOCK_TRACEPARENT_VALUE);
		getTripsCollectionDataHeaders.setTracestate(Constant.MOCK_TRACESTATE_VALUE);
		getTripsCollectionDataHeaders.setAcceptLanguage(Constant.MOCK_ACCEPT_LANGUAGE_VALUE);
		return getTripsCollectionDataHeaders;
	}
}
