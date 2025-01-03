package com.axonivy.connector.sbb.connector.rest.osdmdata;

import java.io.IOException;

import com.axonivy.connector.sbb.constant.Constant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import ch.sbb.api.smapi.osdm.journey.client.AddressRef;
import ch.sbb.api.smapi.osdm.journey.client.FareConnectionPointRef;
import ch.sbb.api.smapi.osdm.journey.client.GeoPositionRef;
import ch.sbb.api.smapi.osdm.journey.client.OneOfTripSearchCriteriaOrigin;
import ch.sbb.api.smapi.osdm.journey.client.PointOfInterestRef;
import ch.sbb.api.smapi.osdm.journey.client.StopPlaceRef;

public class OneOfTripSearchCriteriaOriginSerializer extends JsonSerializer<OneOfTripSearchCriteriaOrigin> {

	@Override
	public void serialize(OneOfTripSearchCriteriaOrigin origin, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		// Do nothing
	}

	@Override
	public void serializeWithType(OneOfTripSearchCriteriaOrigin value, JsonGenerator gen,
			SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
		gen.writeStartObject();
		if (value instanceof AddressRef) {
			AddressRef addressRef = (AddressRef) value;
			writeStringFields(gen, addressRef.getClass().getSimpleName(), addressRef.getAddressRef());
		} else if (value instanceof StopPlaceRef) {
			StopPlaceRef stopPlaceRef = (StopPlaceRef) value;
			writeStringFields(gen, stopPlaceRef.getClass().getSimpleName(), stopPlaceRef.getStopPlaceRef());
		} else if (value instanceof FareConnectionPointRef) {
			FareConnectionPointRef fareConnectionPointRef = (FareConnectionPointRef) value;
			writeStringFields(gen, fareConnectionPointRef.getClass().getSimpleName(), fareConnectionPointRef.getName());
		} else if (value instanceof GeoPositionRef) {
			GeoPositionRef geoPositionRef = (GeoPositionRef) value;
			writeStringFields(gen, geoPositionRef.getClass().getSimpleName(), geoPositionRef.getObjectType());
		} else if (value instanceof PointOfInterestRef) {
			PointOfInterestRef pointOfInterestRef = (PointOfInterestRef) value;
			writeStringFields(gen, pointOfInterestRef.getClass().getSimpleName(),
					pointOfInterestRef.getPointOfInterestRef());
		}
		gen.writeEndObject();
	}

	private void writeStringFields(JsonGenerator gen, String objName, String objValue) throws IOException {
		gen.writeStringField(Constant.OBJECT_TYPE_JSON_PROPERTY, objName);
		String lowerCaseFirstCharacter = Character.toLowerCase(objName.charAt(0)) + objName.substring(1);
		gen.writeStringField(lowerCaseFirstCharacter, objValue);
	}
}
