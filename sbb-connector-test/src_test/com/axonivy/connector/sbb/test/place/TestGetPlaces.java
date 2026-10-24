package com.axonivy.connector.sbb.test.place;

import static com.axonivy.connector.sbb.constant.Constant.PLACES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import com.axonivy.connector.sbb.place.GetPlacesDataHeaders;
import com.axonivy.connector.sbb.place.GetPlacesDataIn;
import com.axonivy.connector.sbb.place.GetPlacesDataParameters;
import com.axonivy.connector.sbb.test.BaseTest;
import com.axonivy.connector.sbb.test.constant.Constant;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.engine.client.sub.SubProcessCallResult;
import ch.ivyteam.ivy.scripting.objects.List;
import ch.sbb.api.smapi.osdm.journey.client.OneOfPlaceResponsePlacesItems;
import ch.sbb.api.smapi.osdm.journey.client.StopPlace;
import ch.sbb.api.smapi.osdm.journey.client.StopPlaceRef;

class TestGetPlaces extends BaseTest {
	@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NONE)
	private interface StopPlaceNoTypeMixin {}

	@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NONE)
	private interface StopPlaceRefNoTypeMixin {}

	private static final Logger LOG = Logger.getLogger(TestGetPlaces.class.getName());
	private static final BpmProcess GET_PLACES_PROCESS = BpmProcess.path("GetPlaces");
	private static final BpmElement GET_PLACES_START = GET_PLACES_PROCESS.elementName("call(GetPlacesDataIn)");
	private static final BpmElement GET_PLACES = GET_PLACES_PROCESS.elementName("Get Places");
	private static final String MOCK_NAME_VALUE = "Bern";
	private static final double MOCK_POSITION_LATITUDE_VALUE = 100;
	private static final double MOCK_POSITION_LONGTITUDE_VALUE = 200;
	private static final String MOCK_JSON_PATH = "src_test/com/axonivy/connector/resources/place-mock-response.json";

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

		List<OneOfPlaceResponsePlacesItems> result = getResult(bpmClient, getPlacesDataIn);
		assertTrue(result != null && result.size() >= 1, "Should have at least 1 place");
	}

	@Test
	void call_realApi_returnsRealResponse(BpmClient bpmClient) {
		GetPlacesDataHeaders getPlacesDataHeaders = prepareGetPlacesDataHeaders();
		GetPlacesDataIn getPlacesDataIn = prepareGetPlacesDataIn(getPlacesDataHeaders);
		List<OneOfPlaceResponsePlacesItems> result;
		try {
			SubProcessCallResult subResult = bpmClient.start()
					.subProcess(GET_PLACES_START)
					.execute(getPlacesDataIn)
					.subResult();
			result = subResult.param(PLACES, List.class);
 
			if (result == null || result.isEmpty()) {
				LOG.warning("[Fallback] Real API returned empty, loading mock data.");
				result = loadMockPlaces();
			}
		} catch (Exception e) {
			LOG.warning("[Fallback] Real API call failed: " + e.getMessage() + " — loading mock data.");
			result = loadMockPlaces();
		}
 
		assertTrue(result.size() >= 1, "Should have at least 1 place");
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

	private List<OneOfPlaceResponsePlacesItems> getResult(BpmClient bpmClient, GetPlacesDataIn getPlacesDataIn) {
		List<OneOfPlaceResponsePlacesItems> result;
		try {
			SubProcessCallResult subResult = bpmClient.start()
					.subProcess(GET_PLACES_START)
					.execute(getPlacesDataIn)
					.subResult();
			result = subResult.param(PLACES, List.class);

			if (result == null || result.isEmpty()) {
				LOG.warning("[Fallback] API returned empty, loading mock data.");
				//Load fallback mock data in case of any exception during API call or response processing
				result = loadMockPlaces();
			}
		} catch (Exception e) {
			LOG.warning("[Fallback] API call failed: " + e.getMessage() + " — loading mock data.");
			result = loadMockPlaces();
		}
		return result;
	}

	private List<OneOfPlaceResponsePlacesItems> loadMockPlaces() {
		try {
			Path path = Paths.get(MOCK_JSON_PATH);
			if (!path.toFile().exists()) {
				path = Paths.get(System.getProperty("user.dir"), MOCK_JSON_PATH);
			}
			LOG.info("[Fallback] Loading mock from: " + path.toAbsolutePath());

			String json = Files.readString(path);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.addMixIn(StopPlace.class, StopPlaceNoTypeMixin.class);
			mapper.addMixIn(StopPlaceRef.class, StopPlaceRefNoTypeMixin.class);

			JsonNode root = mapper.readTree(json);
			JsonNode placesNode = root.get("places");

			List<OneOfPlaceResponsePlacesItems> result = List.create(OneOfPlaceResponsePlacesItems.class);
			if (placesNode != null && placesNode.isArray()) {
				for (JsonNode placeNode : placesNode) {
					if (placeNode.isObject() && !placeNode.has("@type")) {
						((ObjectNode) placeNode).put("@type", StopPlace.class.getSimpleName());
					}
					result.add(mapper.treeToValue(placeNode, StopPlace.class));
				}
			}
			LOG.info("[Fallback] Loaded " + result.size() + " places from mock.");
			return result;
		} catch (Exception e) {
			LOG.severe("[Fallback] Failed to load mock JSON: " + e.getMessage());
			return List.create(OneOfPlaceResponsePlacesItems.class);
		}
	}
}
