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

import ch.sbb.api.smapi.osdm.journey.client.Trip;
import ch.sbb.api.smapi.osdm.journey.client.TripCollectionResponse;

public class TripCollectionResponseDeserializer extends JsonDeserializer<TripCollectionResponse> {
	private ObjectMapper mapper;

	public TripCollectionResponseDeserializer(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public TripCollectionResponse deserialize(JsonParser parser, DeserializationContext context)
			throws IOException, JacksonException {

		JsonNode node = parser.readValueAsTree();
		return extractTripResponse(node);
	}

	private TripCollectionResponse extractTripResponse(JsonNode node)
			throws JsonProcessingException, IllegalArgumentException {
		TripCollectionResponse result = new TripCollectionResponse();
		if (Objects.nonNull(node)) {
			List<Trip> trips = new ArrayList<>();
			if (node.has(Constant.TRIPS)) {
				JsonNode tripsNode = node.get(Constant.TRIPS);
				if (tripsNode != null && tripsNode.isArray()) {
					for (JsonNode tripNode : tripsNode) {
						String tripNodeJson = tripNode.toString().replace(Constant.OBJECT_TYPE_JSON_PROPERTY,
								Constant.TYPE_JSON_PROPERTY);
						Trip trip = mapper.readValue(tripNodeJson, Trip.class);
						trips.add(trip);
					}
				}
			}
			result.setTrips(trips);
		}
		return result;
	}
}