package com.axonivy.connector.sbb.connector.rest.osdmdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.sbb.api.smapi.osdm.journey.client.OneOfTripSearchCriteriaOrigin;
import ch.sbb.api.smapi.osdm.journey.client.PlaceResponse;
import ch.sbb.api.smapi.osdm.journey.client.TripCollectionResponse;

public class OSDMDataTypeCustomizations extends SimpleModule {

	private static final long serialVersionUID = 7937918079183158890L;

	public OSDMDataTypeCustomizations(ObjectMapper mapper) {
		addDeserializer(PlaceResponse.class, new PlaceResponseDeserializer(mapper));
		addDeserializer(TripCollectionResponse.class, new TripCollectionResponseDeserializer(mapper));
		addSerializer(OneOfTripSearchCriteriaOrigin.class, new OneOfTripSearchCriteriaOriginSerializer());
	}
}
