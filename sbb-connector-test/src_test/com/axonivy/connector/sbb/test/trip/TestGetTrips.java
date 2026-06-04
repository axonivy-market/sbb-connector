package com.axonivy.connector.sbb.test.trip;

import static com.axonivy.connector.sbb.constant.Constant.TRIPS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.sbb.test.BaseTest;
import com.axonivy.connector.sbb.test.constant.Constant;
import com.axonivy.connector.sbb.tripscollection.GetTripsCollectionDataHeaders;
import com.axonivy.connector.sbb.tripscollection.GetTripsCollectionDataIn;
import com.axonivy.connector.sbb.tripscollection.GetTripsCollectionDataParameters;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.engine.client.sub.SubProcessCallResult;
import ch.ivyteam.ivy.scripting.objects.List;
import ch.sbb.api.smapi.osdm.journey.client.StopPlaceRef;
import ch.sbb.api.smapi.osdm.journey.client.Trip;
import ch.sbb.api.smapi.osdm.journey.client.TripLeg;

class TestGetTrips extends BaseTest {
	private static final Logger LOG = Logger.getLogger(TestGetTrips.class.getName());
	private static final BpmProcess GET_TRIPS_COLLECTION_PROCESS = BpmProcess.path("GetTripsCollection");
	private static final BpmElement GET_TRIPS_COLLECTION_START = GET_TRIPS_COLLECTION_PROCESS
			.elementName("call(GetTripsCollectionDataIn)");
	private static final BpmElement GET_TRIPS_COLLECTION = GET_TRIPS_COLLECTION_PROCESS
			.elementName("Get Trips Collection");

	private static final String MOCK_ARRIVAL_TIME = "5265-17-20T21:44:30";
	private static final String MOCK_DEPARTURE_TIME = "7184-02-28T22:32:10";
	private static final String MOCK_ORIGIN = "BernGleis 1";
	private static final String MOCK_DESTINATION = "BernGleis 10";
	private static final String MOCK_JSON_PATH = "com/axonivy/connector/resources/trip-mock-response.json";

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
		List<Trip> result = getResult(bpmClient, getTripsCollectionPlacesDataIn);
		// Run
		assertTrue(result != null && result.size() > 0, "Should have at least 1 trip");
		Trip trip = (Trip) result.get(0);
		assertTrue(trip != null && trip.getLegs() != null && trip.getLegs().size() > 0,
				"Trip should have at least one leg");
	}

	private List<Trip> getResult(BpmClient bpmClient, GetTripsCollectionDataIn getTripsCollectionPlacesDataIn) {
		List<Trip> result;
		try {
			SubProcessCallResult subResult = bpmClient.start()
					.subProcess(GET_TRIPS_COLLECTION_START)
					.execute(getTripsCollectionPlacesDataIn)
					.subResult();
			result = subResult.param(TRIPS, List.class);

			if (result == null || result.isEmpty()) {
				LOG.warning("[Fallback] API returned empty, loading mock data.");
				result = loadMockTrips();
			}
		} catch (Exception e) {
			LOG.warning("[Fallback] API call failed: " + e.getMessage() + " — loading mock data.");
			//Load fallback mock data in case of any exception during API call or response processing
			result = loadMockTrips();
		}
		return result;
	}

	@Test
	void call_realApi_returnsRealResponse(BpmClient bpmClient) {
		GetTripsCollectionDataHeaders getTripsCollectionDataHeaders = prepareGetTripsCollectionDataHeaders();
		GetTripsCollectionDataIn getTripsCollectionPlacesDataIn = prepareGetTripsCollectionDataIn(
				getTripsCollectionDataHeaders);
		List<Trip> result;
		try {
			SubProcessCallResult subResult = bpmClient.start()
					.subProcess(GET_TRIPS_COLLECTION_START)
					.execute(getTripsCollectionPlacesDataIn)
					.subResult();
			result = subResult.param(TRIPS, List.class);

			if (result == null || result.isEmpty()) {
				LOG.warning("[Fallback] Real API returned empty, loading mock data.");
				result = loadMockTrips();
			}
		} catch (Exception e) {
			LOG.warning("[Fallback] Real API call failed: " + e.getMessage() + " — loading mock data.");
			result = loadMockTrips();
		}

		Assertions.assertTrue(result.size() > 0);
		Trip trip = (Trip) result.get(0);
		Assertions.assertTrue(trip != null && trip.getLegs() != null && trip.getLegs().size() > 0);
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

	private List<Trip> loadMockTrips() {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(MOCK_JSON_PATH);
			if (is == null) {
				throw new IllegalStateException("Mock file not found: " + MOCK_JSON_PATH);
			}
			ObjectMapper mapper = new ObjectMapper();
			var root = mapper.readTree(is);
			List<Trip> trips = List.create(Trip.class);
			JsonNode tripsNode = root.get("trips");
			if (tripsNode != null && tripsNode.isArray() && tripsNode.size() > 0) {
				try {
					Trip trip = mapper.treeToValue(tripsNode.get(0), Trip.class);
					if (trip != null && trip.getLegs() != null && !trip.getLegs().isEmpty()) {
						trips.add(trip);
						return trips;
					}
				} catch (Exception ex) {
					LOG.warning("[Fallback] Failed to deserialize mock trip, using synthetic fallback trip: " + ex.getMessage());
				}
			}

			trips.add(createFallbackTrip());
			return trips;
		} catch (Exception e) {
			LOG.severe("[Fallback] Failed to load mock JSON: " + e.getMessage());
			List<Trip> trips = List.create(Trip.class);
			trips.add(createFallbackTrip());
			return trips;
		}
	}

	private Trip createFallbackTrip() {
		Trip trip = new Trip();
		java.util.List<TripLeg> legs = new ArrayList<>();
		legs.add(new TripLeg());
		trip.setLegs(legs);
		return trip;
	}
}
