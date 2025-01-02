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
				if (placesNode != null && placesNode.isArray()) {
					for (JsonNode placeNode : placesNode) {
						if (placeNode.has(Constant.OBJECT_TYPE_JSON_PROPERTY) && StopPlace.class.getSimpleName()
								.equals(placeNode.get(Constant.OBJECT_TYPE_JSON_PROPERTY).asText())) {
							String stopPlaceJson = placeNode.toString().replace(Constant.OBJECT_TYPE_JSON_PROPERTY,
									Constant.TYPE_JSON_PROPERTY);
							OneOfPlaceResponsePlacesItems place = mapper.readValue(stopPlaceJson, StopPlace.class);
							places.add(place);
						} else if (placeNode.has(Constant.OBJECT_TYPE_JSON_PROPERTY) && PointOfInterest.class
								.getSimpleName().equals(placeNode.get(Constant.OBJECT_TYPE_JSON_PROPERTY).asText())) {
							String pointOfInterestJson = placeNode.toString()
									.replace(Constant.OBJECT_TYPE_JSON_PROPERTY, Constant.TYPE_JSON_PROPERTY);
							OneOfPlaceResponsePlacesItems place = mapper.readValue(pointOfInterestJson,
									PointOfInterest.class);
							places.add(place);
						} else if (placeNode.has(Constant.OBJECT_TYPE_JSON_PROPERTY) && Address.class.getSimpleName()
								.equals(placeNode.get(Constant.OBJECT_TYPE_JSON_PROPERTY).asText())) {
							String addressJson = placeNode.toString().replace(Constant.OBJECT_TYPE_JSON_PROPERTY,
									Constant.TYPE_JSON_PROPERTY);
							OneOfPlaceResponsePlacesItems place = mapper.readValue(addressJson, Address.class);
							places.add(place);
						} else if (placeNode.has(Constant.OBJECT_TYPE_JSON_PROPERTY) && FareConnectionPoint.class
								.getSimpleName().equals(placeNode.get(Constant.OBJECT_TYPE_JSON_PROPERTY).asText())) {
							String fareConnectionPointJson = placeNode.toString()
									.replace(Constant.OBJECT_TYPE_JSON_PROPERTY, Constant.TYPE_JSON_PROPERTY);
							OneOfPlaceResponsePlacesItems place = mapper.readValue(fareConnectionPointJson,
									FareConnectionPoint.class);
							places.add(place);
						}
					}
				}
				result.setPlaces(places);
			}
		}
		return result;
	}
}
