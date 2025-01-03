package com.axonivy.connector.sbb.demo.trip;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.sbb.api.smapi.osdm.journey.client.Address;
import ch.sbb.api.smapi.osdm.journey.client.FareConnectionPoint;
import ch.sbb.api.smapi.osdm.journey.client.OneOfPlaceResponsePlacesItems;
import ch.sbb.api.smapi.osdm.journey.client.OneOfTripSearchCriteriaDestination;
import ch.sbb.api.smapi.osdm.journey.client.OneOfTripSearchCriteriaOrigin;
import ch.sbb.api.smapi.osdm.journey.client.PointOfInterest;
import ch.sbb.api.smapi.osdm.journey.client.StopPlace;

public class PlaceUtils {

	public static List<String> getNameOfPlaces(List<OneOfPlaceResponsePlacesItems> places) {
		List<String> placeNames = new ArrayList<>();

		for (OneOfPlaceResponsePlacesItems place : places) {
			if (place instanceof StopPlace) {
				StopPlace sp = (StopPlace) place;
				placeNames.add(sp.getName());
			} else if (place instanceof Address) {
				Address address = (Address) place;
				placeNames.add(address.getName());
			} else if (place instanceof PointOfInterest) {
				PointOfInterest poi = (PointOfInterest) place;
				placeNames.add(poi.getName());
			} else if (place instanceof FareConnectionPoint) {
				FareConnectionPoint fcp = (FareConnectionPoint) place;
				placeNames.add(fcp.getName());
			}
		}

		return placeNames;
	}

	public static OneOfTripSearchCriteriaOrigin getTripSearchCriteriaOrigin(List<OneOfPlaceResponsePlacesItems> places,
			String nameOfPlace) {
		Object obj = getObjectInPlaces(places, nameOfPlace);
		return obj != null ? (OneOfTripSearchCriteriaOrigin) obj : null;
	}

	public static OneOfTripSearchCriteriaDestination getTripSearchCriteriaDestination(
			List<OneOfPlaceResponsePlacesItems> places, String nameOfPlace) {
		Object obj = getObjectInPlaces(places, nameOfPlace);
		return obj != null ? (OneOfTripSearchCriteriaDestination) obj : null;
	}

	private static Object getObjectInPlaces(List<OneOfPlaceResponsePlacesItems> places, String nameOfPlace) {
		for (OneOfPlaceResponsePlacesItems place : places) {
			if (place instanceof StopPlace) {
				StopPlace sp = (StopPlace) place;
				if (StringUtils.equals(nameOfPlace, sp.getName())) {
					return sp.getRef();
				}
			} else if (place instanceof Address) {
				Address address = (Address) place;
				if (StringUtils.equals(nameOfPlace, address.getName())) {
					return address.getRef();
				}
			} else if (place instanceof PointOfInterest) {
				PointOfInterest poi = (PointOfInterest) place;
				if (StringUtils.equals(nameOfPlace, poi.getName())) {
					return poi.getRef();
				}
			} else if (place instanceof FareConnectionPoint) {
				FareConnectionPoint fcp = (FareConnectionPoint) place;
				if (StringUtils.equals(nameOfPlace, fcp.getName())) {
					return fcp.getRef();
				}
			}
		}

		return null;
	}
}
