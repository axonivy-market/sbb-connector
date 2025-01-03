package com.axonivy.connector.sbb.connector.rest.osdmdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.axonivy.connector.sbb.constant.Constant;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ivyteam.ivy.environment.Ivy;
import ch.sbb.api.smapi.osdm.journey.client.Address;
import ch.sbb.api.smapi.osdm.journey.client.FareConnectionPoint;
import ch.sbb.api.smapi.osdm.journey.client.OneOfPlaceResponsePlacesItems;
import ch.sbb.api.smapi.osdm.journey.client.PlaceResponse;
import ch.sbb.api.smapi.osdm.journey.client.PointOfInterest;
import ch.sbb.api.smapi.osdm.journey.client.StopPlace;

public class PlaceResponseDeserializer extends JsonDeserializer<PlaceResponse> {
	private ObjectMapper mapper;

	public PlaceResponseDeserializer(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public PlaceResponse deserialize(JsonParser parser, DeserializationContext context)
			throws IOException, JacksonException {
		JsonNode node = parser.readValueAsTree();
		return extractPlaceResponse(node);
	}

	private PlaceResponse extractPlaceResponse(JsonNode node) throws JsonProcessingException, IllegalArgumentException {
		PlaceResponse result = new PlaceResponse();
		if (Objects.nonNull(node)) {
			List<OneOfPlaceResponsePlacesItems> places = new ArrayList<>();
			if (node.has(Constant.PLACES)) {
				JsonNode placesNode = node.get(Constant.PLACES);
				extractPlaces(placesNode, places);
				result.setPlaces(places);
			}
		}
		return result;
	}

	private void extractPlaces(JsonNode placesNode, List<OneOfPlaceResponsePlacesItems> places) {
		if (placesNode != null && placesNode.isArray()) {
			for (JsonNode placeNode : placesNode) {
				extractPlace(placeNode, places);
			}
		}
	}

	private void extractPlace(JsonNode placeNode, List<OneOfPlaceResponsePlacesItems> places) {
		if (placeNode.has(Constant.OBJECT_TYPE_JSON_PROPERTY)) {
			String jsonData = placeNode.toString().replace(Constant.OBJECT_TYPE_JSON_PROPERTY,
					Constant.TYPE_JSON_PROPERTY);
			String typeClassName = placeNode.get(Constant.OBJECT_TYPE_JSON_PROPERTY).asText();
			OneOfPlaceResponsePlacesItems place = parseJsonToPlaceItem(jsonData, typeClassName);
			if (place != null) {
				places.add(place);
			}
		}
	}

	private OneOfPlaceResponsePlacesItems parseJsonToPlaceItem(String jsonData, String typeClassName) {
		OneOfPlaceResponsePlacesItems place = null;
		try {
			if (StopPlace.class.getSimpleName().equals(typeClassName)) {
				place = mapper.readValue(jsonData, StopPlace.class);
			} else if (PointOfInterest.class.getSimpleName().equals(typeClassName)) {
				place = mapper.readValue(jsonData, PointOfInterest.class);
			} else if (Address.class.getSimpleName().equals(typeClassName)) {
				place = mapper.readValue(jsonData, Address.class);
			} else if (FareConnectionPoint.class.getSimpleName().equals(typeClassName)) {
				place = mapper.readValue(jsonData, FareConnectionPoint.class);
			}
		} catch (Exception e) {
			Ivy.log().error(e);
		}

		return place;
	}
}
