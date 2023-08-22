package com.axonivy.connector.sbb.test.trip;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.axonivy.connector.sbb.trip.GetTripsDataHeaders;
import com.axonivy.connector.sbb.trip.GetTripsDataIn;
import com.axonivy.connector.sbb.trip.GetTripsDataParameters;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.engine.client.sub.SubProcessCallResult;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.scripting.objects.Date;
import ch.ivyteam.ivy.scripting.objects.List;
import ch.sbb.app.b2p.client.Link;
import ch.sbb.app.b2p.client.LocationIdentity;
import ch.sbb.app.b2p.client.Note;
import ch.sbb.app.b2p.client.Segment;
import ch.sbb.app.b2p.client.Stop;
import ch.sbb.app.b2p.client.TransportProduct;
import ch.sbb.app.b2p.client.Trip;

@IvyProcessTest
@SuppressWarnings("unchecked")
public class TestGetTrips {

  private static final BpmProcess GET_TRIPS_PROCESS = BpmProcess.path("GetTrips");
  private static final BpmElement GET_TRIPS_START = GET_TRIPS_PROCESS
          .elementName("call(GetTripsDataIn)");
  private static final BpmElement GET_TRIPS = GET_TRIPS_PROCESS.elementName("Get Trips");

  @Test
  public void call_mockedApi_returnsMockedResponse(BpmClient bpmClient) {
    // Arguments
    GetTripsDataHeaders getTripsDataHeaders = new GetTripsDataHeaders();
    getTripsDataHeaders.setConversationId("ConversationId");
    getTripsDataHeaders.setAcceptLanguage("AcceptLanguage");
    getTripsDataHeaders.setScrollContext("ScrollContext");

    List<Integer> getTripsDataParametersViaId = List.create(Integer.class);
    Date getTripsDataParametersDate = new Date();

    GetTripsDataParameters getTripsDataParameters = new GetTripsDataParameters();
    getTripsDataParameters.setOriginId(1);
    getTripsDataParameters.setDestinationId(2);
    getTripsDataParameters.setViaId(getTripsDataParametersViaId);
    getTripsDataParameters.setDate(getTripsDataParametersDate);
    getTripsDataParameters.setTime("Time");
    getTripsDataParameters.setArrivalDeparture("ArrivalDeparture");
    getTripsDataParameters.setTrainType("TrainType");
    getTripsDataParameters.setAlternateMatch("AlternateMatch");
    getTripsDataParameters.setIncludeBefore(3);
    getTripsDataParameters.setIncludeAfter(4);
    getTripsDataParameters.setCalculateEco(true);

    GetTripsDataIn getTripsDataIn = new GetTripsDataIn();
    getTripsDataIn.setHeaders(getTripsDataHeaders);
    getTripsDataIn.setParameters(getTripsDataParameters);

    // Mocks
    List<Trip> trips = List.create(Trip.class);

    bpmClient
            .mock()
            .element(GET_TRIPS)
            .with(in -> {
              try {
                GetTripsDataIn inGetTripsDataIn = (GetTripsDataIn) in.get("in");

                GetTripsDataHeaders inGetTripsDataInHeaders = (GetTripsDataHeaders) inGetTripsDataIn
                        .get("headers");
                assertThat(inGetTripsDataInHeaders).isEqualTo(getTripsDataHeaders);
                assertThat(inGetTripsDataInHeaders.get("conversationId")).isEqualTo("ConversationId");
                assertThat(inGetTripsDataInHeaders.get("acceptLanguage")).isEqualTo("AcceptLanguage");
                assertThat(inGetTripsDataInHeaders.get("scrollContext")).isEqualTo("ScrollContext");

                GetTripsDataParameters inGetTripsDataInParameters = (GetTripsDataParameters) inGetTripsDataIn
                        .get("parameters");
                assertThat(inGetTripsDataInParameters).isEqualTo(getTripsDataParameters);
                assertThat(inGetTripsDataInParameters.getOriginId()).isEqualTo(1);
                assertThat(inGetTripsDataInParameters.getDestinationId()).isEqualTo(2);
                assertThat(inGetTripsDataInParameters.getViaId()).isSameAs(getTripsDataParametersViaId);
                assertThat(inGetTripsDataInParameters.getDate()).isSameAs(getTripsDataParametersDate);
                assertThat(inGetTripsDataInParameters.getTime()).isEqualTo("Time");
                assertThat(inGetTripsDataInParameters.getArrivalDeparture()).isEqualTo("ArrivalDeparture");
                assertThat(inGetTripsDataInParameters.getTrainType()).isEqualTo("TrainType");
                assertThat(inGetTripsDataInParameters.getAlternateMatch()).isEqualTo("AlternateMatch");
                assertThat(inGetTripsDataInParameters.getIncludeBefore()).isEqualTo(3);
                assertThat(inGetTripsDataInParameters.getIncludeAfter()).isEqualTo(4);
                assertThat(inGetTripsDataInParameters.getCalculateEco()).isTrue();

                in.set("trips", trips);
                return in;
              } catch (NoSuchFieldException ex) {
                throw new RuntimeException(ex);
              }
            });

    // Run
    SubProcessCallResult result = bpmClient
            .start()
            .subProcess(GET_TRIPS_START)
            .execute(getTripsDataIn)
            .subResult();

    // Assert
    assertThat(result.param("trips", List.class)).isSameAs(trips);
  }

  @Test
  public void call_realApi_returnsRealResponse(BpmClient bpmClient) {
    // Arguments
    GetTripsDataHeaders getTripsDataHeaders = new GetTripsDataHeaders();

    Date getTripsDataParametersDate = new Date("2000-01-01");

    GetTripsDataParameters getTripsDataParameters = new GetTripsDataParameters();
    getTripsDataParameters.setOriginId(1);
    getTripsDataParameters.setDestinationId(2);
    getTripsDataParameters.setDate(getTripsDataParametersDate);
    getTripsDataParameters.setTime("00:00");

    GetTripsDataIn getTripsDataIn = new GetTripsDataIn();
    getTripsDataIn.setHeaders(getTripsDataHeaders);
    getTripsDataIn.setParameters(getTripsDataParameters);

    // Run
    SubProcessCallResult result = bpmClient
            .start()
            .subProcess(GET_TRIPS_START)
            .execute(getTripsDataIn)
            .subResult();

    // Assert
    List<Trip> trips = result.param("trips", List.class);
    assertThat(trips.size()).isEqualTo(1);

    Trip trip = trips.get(0);

    java.util.List<Link> links = trip.getLinks();
    assertThat(links.size()).isEqualTo(5);

    Link link000 = links.get(0);
    assertThat(link000.getRel()).isEqualTo("prices");
    assertThat(link000.getVersion()).isNull();
    assertThat(link000.getHref()).isEqualTo("/v2/prices");
    assertThat(link000.getMethod()).isEqualTo("POST");
    assertThat(link000.getContentTypes()).isEqualTo(java.util.List.of("application/json"));

    Link link001 = links.get(1);
    assertThat(link001.getRel()).isEqualTo("trip-offers");
    assertThat(link001.getVersion()).isNull();
    assertThat(link001.getHref()).isEqualTo(
            """
                    /trip-offers?tripId=wQg8CgJWMRKRBSVDMiVCNkhLSQkJgFQlMjRBJTNEMSU0ME8lM0RCZXJuJTQwWCUzRDc0MzkxMwEZdFklM0Q0Njk0ODgzNCU0MEwlM0Q4NTA3\
                    MDAwJTQwYQE-EDI4JTQwOksAEEFhcmF1DUwgODA1MTI2OSU0CUwANwFaADYdTAwyMTEzPkwANDIwMjAwODIzMTAzNCUyGQ8BKywyNElSKzE2KysrJTIBHQAxCQcNBhBD\
                    MiVBNz7aAP6PAC4mATRaJUMzJUJDcmljaCtIQhHkBDU0AcgZ5BA3ODE3NzkwADNKMAEV5AgxMjMy5AAENTIJ5AQzN2bkAAxCNktDAexAQjYlMjNWRSUyMzAlMjNDRiUh\
                    NAULAEERFABNDQkMU0lDVAEfCCUyMwk6BEtDSkMAMEVSRyUyMzIlMjNISU4NOjhFQ0slMjMzNjYzOTQlN0MuCQAINDcyCRIJCQgwJTcFBBA2NTcwMQkZBDM4ASIBDSQt\
                    MjE0NzQ4MzYzCScoMjMa3gEKOAoSCgdNUygSB1NUQVRJT04SGSEemC0wOC0yM1QxMDozNDowMCswMjowMCIHUExBTk5FRAobCg4KAzEzMhU2FRsMKAEKHw1XDDAyMThK\
                    IQAIAgo6DSFBfxUhABoyeAAMMToxM0Z4ALgoAxIYEAMaBjAwMDAxMSICSVIqAjE2MgQyMTY5Kg5QVUJMSUNfSk9VUk5FWRrFAVZpADbhAAgxOjJSaQAVxgwyMTE5FaUZ\
                    5wAEFV1BYRUhPsYAADUhODo-ARwoBRIaCAMQBTbIABwzNzIEMjI2Nz7IAA==&passengers=${passengerInfos}\
                    """);
    assertThat(link001.getMethod()).isEqualTo("GET");
    assertThat(link001.getContentTypes()).isEqualTo(java.util.List.of("application/json"));

    Link link002 = links.get(2);
    assertThat(link002.getRel()).isEqualTo("group-trip-offers");
    assertThat(link002.getVersion()).isNull();
    assertThat(link002.getHref()).isEqualTo(
            """
                    /group-trip-offers?tripId=wQg8CgJWMRKRBSVDMiVCNkhLSQkJgFQlMjRBJTNEMSU0ME8lM0RCZXJuJTQwWCUzRDc0MzkxMwEZdFklM0Q0Njk0ODgzNCU0MEwlM0\
                    Q4NTA3MDAwJTQwYQE-EDI4JTQwOksAEEFhcmF1DUwgODA1MTI2OSU0CUwANwFaADYdTAwyMTEzPkwANDIwMjAwODIzMTAzNCUyGQ8BKywyNElSKzE2KysrJTIBHQAxCQ\
                    cNBhBDMiVBNz7aAP6PAC4mATRaJUMzJUJDcmljaCtIQhHkBDU0AcgZ5BA3ODE3NzkwADNKMAEV5AgxMjMy5AAENTIJ5AQzN2bkAAxCNktDAexAQjYlMjNWRSUyMzAlMj\
                    NDRiUhNAULAEERFABNDQkMU0lDVAEfCCUyMwk6BEtDSkMAMEVSRyUyMzIlMjNISU4NOjhFQ0slMjMzNjYzOTQlN0MuCQAINDcyCRIJCQgwJTcFBBA2NTcwMQkZBDM4AS\
                    IBDSQtMjE0NzQ4MzYzCScoMjMa3gEKOAoSCgdNUygSB1NUQVRJT04SGSEemC0wOC0yM1QxMDozNDowMCswMjowMCIHUExBTk5FRAobCg4KAzEzMhU2FRsMKAEKHw1XDD\
                    AyMThKIQAIAgo6DSFBfxUhABoyeAAMMToxM0Z4ALgoAxIYEAMaBjAwMDAxMSICSVIqAjE2MgQyMTY5Kg5QVUJMSUNfSk9VUk5FWRrFAVZpADbhAAgxOjJSaQAVxgwyMT\
                    E5FaUZ5wAEFV1BYRUhPsYAADUhODo-ARwoBRIaCAMQBTbIABwzNzIEMjI2Nz7IAA==&passengerGroups=${passengerGroupInfos}\
                    """);
    assertThat(link002.getMethod()).isEqualTo("GET");
    assertThat(link002.getContentTypes()).isEqualTo(java.util.List.of("application/json"));

    Link link003 = links.get(3);
    assertThat(link003.getRel()).isEqualTo("online-offers");
    assertThat(link003.getVersion()).isNull();
    assertThat(link003.getHref()).isEqualTo(
            """
                    https://www.sbb.ch/en/buying/pages/fahrplan/fahrplan.xhtml?recon=wQg8CgJWMRKRBSVDMiVCNkhLSQkJgFQlMjRBJTNEMSU0ME8lM0RCZXJuJTQwWCU\
                    zRDc0MzkxMwEZdFklM0Q0Njk0ODgzNCU0MEwlM0Q4NTA3MDAwJTQwYQE-EDI4JTQwOksAEEFhcmF1DUwgODA1MTI2OSU0CUwANwFaADYdTAwyMTEzPkwANDIwMjAwODI\
                    zMTAzNCUyGQ8BKywyNElSKzE2KysrJTIBHQAxCQcNBhBDMiVBNz7aAP6PAC4mATRaJUMzJUJDcmljaCtIQhHkBDU0AcgZ5BA3ODE3NzkwADNKMAEV5AgxMjMy5AAENTI\
                    J5AQzN2bkAAxCNktDAexAQjYlMjNWRSUyMzAlMjNDRiUhNAULAEERFABNDQkMU0lDVAEfCCUyMwk6BEtDSkMAMEVSRyUyMzIlMjNISU4NOjhFQ0slMjMzNjYzOTQlN0M\
                    uCQAINDcyCRIJCQgwJTcFBBA2NTcwMQkZBDM4ASIBDSQtMjE0NzQ4MzYzCScoMjMa3gEKOAoSCgdNUygSB1NUQVRJT04SGSEemC0wOC0yM1QxMDozNDowMCswMjowMCI\
                    HUExBTk5FRAobCg4KAzEzMhU2FRsMKAEKHw1XDDAyMThKIQAIAgo6DSFBfxUhABoyeAAMMToxM0Z4ALgoAxIYEAMaBjAwMDAxMSICSVIqAjE2MgQyMTY5Kg5QVUJMSUN\
                    fSk9VUk5FWRrFAVZpADbhAAgxOjJSaQAVxgwyMTE5FaUZ5wAEFV1BYRUhPsYAADUhODo-ARwoBRIaCAMQBTbIABwzNzIEMjI2Nz7IAA==&datum=2000-01-01+01:00\
                    &zeit=0000\
                    """);
    assertThat(link003.getMethod()).isEqualTo("GET");
    assertThat(link003.getContentTypes()).isEqualTo(java.util.List.of("text/html"));

    Link link004 = links.get(4);
    assertThat(link004.getRel()).isEqualTo("mobile-offers");
    assertThat(link004.getVersion()).isNull();
    assertThat(link004.getHref()).isEqualTo(
            """
                    https://app.sbbmobile.ch/tripoffer?appid=bookingAPI&recon=wQg8CgJWMRKRBSVDMiVCNkhLSQkJgFQlMjRBJTNEMSU0ME8lM0RCZXJuJTQwWCUzRDc0Mz\
                    kxMwEZdFklM0Q0Njk0ODgzNCU0MEwlM0Q4NTA3MDAwJTQwYQE-EDI4JTQwOksAEEFhcmF1DUwgODA1MTI2OSU0CUwANwFaADYdTAwyMTEzPkwANDIwMjAwODIzMTAzNC\
                    UyGQ8BKywyNElSKzE2KysrJTIBHQAxCQcNBhBDMiVBNz7aAP6PAC4mATRaJUMzJUJDcmljaCtIQhHkBDU0AcgZ5BA3ODE3NzkwADNKMAEV5AgxMjMy5AAENTIJ5AQzN2\
                    bkAAxCNktDAexAQjYlMjNWRSUyMzAlMjNDRiUhNAULAEERFABNDQkMU0lDVAEfCCUyMwk6BEtDSkMAMEVSRyUyMzIlMjNISU4NOjhFQ0slMjMzNjYzOTQlN0MuCQAIND\
                    cyCRIJCQgwJTcFBBA2NTcwMQkZBDM4ASIBDSQtMjE0NzQ4MzYzCScoMjMa3gEKOAoSCgdNUygSB1NUQVRJT04SGSEemC0wOC0yM1QxMDozNDowMCswMjowMCIHUExBTk\
                    5FRAobCg4KAzEzMhU2FRsMKAEKHw1XDDAyMThKIQAIAgo6DSFBfxUhABoyeAAMMToxM0Z4ALgoAxIYEAMaBjAwMDAxMSICSVIqAjE2MgQyMTY5Kg5QVUJMSUNfSk9VUk\
                    5FWRrFAVZpADbhAAgxOjJSaQAVxgwyMTE5FaUZ5wAEFV1BYRUhPsYAADUhODo-ARwoBRIaCAMQBTbIABwzNzIEMjI2Nz7IAA==&date=2000-01-01+01:00&class=2\
                    """);
    assertThat(link004.getMethod()).isEqualTo("GET");
    assertThat(link004.getContentTypes()).isEqualTo(java.util.List.of("text/html"));

    assertThat(trip.getTripId()).isEqualTo(
            """
                    wQg8CgJWMRKRBSVDMiVCNkhLSQkJgFQlMjRBJTNEMSU0ME8lM0RCZXJuJTQwWCUzRDc0MzkxMwEZdFklM0Q0Njk0ODgzNCU0MEwlM0Q4NTA3MDAwJTQwYQE-EDI4JTQw\
                    OksAEEFhcmF1DUwgODA1MTI2OSU0CUwANwFaADYdTAwyMTEzPkwANDIwMjAwODIzMTAzNCUyGQ8BKywyNElSKzE2KysrJTIBHQAxCQcNBhBDMiVBNz7aAP6PAC4mATRa\
                    JUMzJUJDcmljaCtIQhHkBDU0AcgZ5BA3ODE3NzkwADNKMAEV5AgxMjMy5AAENTIJ5AQzN2bkAAxCNktDAexAQjYlMjNWRSUyMzAlMjNDRiUhNAULAEERFABNDQkMU0lD\
                    VAEfCCUyMwk6BEtDSkMAMEVSRyUyMzIlMjNISU4NOjhFQ0slMjMzNjYzOTQlN0MuCQAINDcyCRIJCQgwJTcFBBA2NTcwMQkZBDM4ASIBDSQtMjE0NzQ4MzYzCScoMjMa\
                    3gEKOAoSCgdNUygSB1NUQVRJT04SGSEemC0wOC0yM1QxMDozNDowMCswMjowMCIHUExBTk5FRAobCg4KAzEzMhU2FRsMKAEKHw1XDDAyMThKIQAIAgo6DSFBfxUhABoy\
                    eAAMMToxM0Z4ALgoAxIYEAMaBjAwMDAxMSICSVIqAjE2MgQyMTY5Kg5QVUJMSUNfSk9VUk5FWRrFAVZpADbhAAgxOjJSaQAVxgwyMTE5FaUZ5wAEFV1BYRUhPsYAADUh\
                    ODo-ARwoBRIaCAMQBTbIABwzNzIEMjI2Nz7IAA==\
                    """);
    assertThat(trip.isAlternative()).isFalse();
    assertThat(trip.isValid()).isFalse();

    java.util.List<Segment> segments = trip.getSegments();
    assertThat(segments.size()).isEqualTo(1);

    Segment segment = segments.get(0);
    assertThat(segment.getType()).isEqualTo(Segment.TypeEnum.PUBLIC_JOURNEY);

    java.util.List<Stop> stops = segment.getStops();
    assertThat(stops.size()).isEqualTo(2);

    Stop stop000 = stops.get(0);

    LocationIdentity id2_000 = stop000.getId2();
    assertThat(id2_000.getValue()).isEqualTo("8507000");
    assertThat(id2_000.getType()).isEqualTo("UIC");
    assertThat(id2_000.getExternalId()).isNull();

    assertThat(stop000.getName()).isEqualTo("Bern");
    assertThat(stop000.getDepartureDateTime())
            .isEqualTo(OffsetDateTime.of(1999, 12, 31, 23, 0, 0, 0, ZoneOffset.of("Z")));
    assertThat(stop000.getDepartureDateTimeRt()).isNull();
    assertThat(stop000.getDepartureDelayText()).isNull();
    assertThat(stop000.getArrivalDateTime()).isNull();
    assertThat(stop000.getArrivalDateTimeRt()).isNull();
    assertThat(stop000.getArrivalDelayText()).isNull();
    assertThat(stop000.getDepartureTrack()).isEqualTo("7");
    assertThat(stop000.getDepartureTrackRt()).isNull();
    assertThat(stop000.isDeparturePlatformChanged()).isFalse();
    assertThat(stop000.isDelayUndefined()).isFalse();
    assertThat(stop000.getArrivalTrack()).isNull();
    assertThat(stop000.getArrivalTrackRt()).isNull();
    assertThat(stop000.isArrivalPlatformChanged()).isFalse();
    assertThat(stop000.getStopStatus()).isEqualTo(Stop.StopStatusEnum.PLANNED);
    assertThat(stop000.isRequestStop()).isFalse();
    assertThat(stop000.getBoardingAlightingStatus())
            .isEqualTo(Stop.BoardingAlightingStatusEnum.BOARDING_ALIGHTING);
    assertThat(stop000.getRouteIndex()).isNull();
    assertThat(stop000.isTariffBorder()).isFalse();

    Stop stop001 = stops.get(1);

    LocationIdentity id2_001 = stop001.getId2();
    assertThat(id2_001.getValue()).isEqualTo("8503000");
    assertThat(id2_001.getType()).isEqualTo("UIC");
    assertThat(id2_001.getExternalId()).isNull();

    assertThat(stop001.getName().matches("Z.rich HB")).isTrue();
    assertThat(stop001.getDepartureDateTime()).isNull();
    assertThat(stop001.getDepartureDateTimeRt()).isNull();
    assertThat(stop001.getDepartureDelayText()).isNull();
    assertThat(stop001.getArrivalDateTime())
            .isEqualTo(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.of("Z")));
    assertThat(stop001.getArrivalDateTimeRt()).isNull();
    assertThat(stop001.getArrivalDelayText()).isNull();
    assertThat(stop001.getDepartureTrack()).isNull();
    assertThat(stop001.getDepartureTrackRt()).isNull();
    assertThat(stop001.isDeparturePlatformChanged()).isFalse();
    assertThat(stop001.isDelayUndefined()).isFalse();
    assertThat(stop001.getArrivalTrack()).isEqualTo("33");
    assertThat(stop001.getArrivalTrackRt()).isEqualTo("19");
    assertThat(stop001.isArrivalPlatformChanged()).isTrue();
    assertThat(stop001.getStopStatus()).isEqualTo(Stop.StopStatusEnum.PLANNED);
    assertThat(stop001.isRequestStop()).isFalse();
    assertThat(stop001.getBoardingAlightingStatus()).isEqualTo(Stop.BoardingAlightingStatusEnum.ALIGHTING);
    assertThat(stop001.getRouteIndex()).isNull();
    assertThat(stop001.isTariffBorder()).isFalse();

    TransportProduct transportProduct = segment.getTransportProduct();
    assertThat(transportProduct.getName()).isNull();
    assertThat(transportProduct.getCategoryShortForm()).isEqualTo("IC");
    assertThat(transportProduct.getCategoryLongForm()).isEqualTo("Intercity");
    assertThat(transportProduct.getLine()).isEqualTo("1");
    assertThat(transportProduct.getNumber()).isEqualTo("721");
    assertThat(transportProduct.getVehicleType()).isEqualTo(TransportProduct.VehicleTypeEnum.TRAIN);

    java.util.List<Note> attributes = segment.getAttributes();
    assertThat(attributes.size()).isEqualTo(6);

    Note attribute000 = attributes.get(0);
    assertThat(attribute000.getKey()).isEqualTo("RR");
    assertThat(attribute000.getValue()).isEqualTo("Restaurant");
    assertThat(attribute000.getPriority()).isNull();
    assertThat(attribute000.getRouteIndexFrom()).isNull();
    assertThat(attribute000.getRouteIndexTo()).isNull();

    Note attribute001 = attributes.get(1);
    assertThat(attribute001.getKey()).isEqualTo("CC");
    assertThat(attribute001.getValue()).isEqualTo("Couchette");
    assertThat(attribute001.getPriority()).isNull();
    assertThat(attribute001.getRouteIndexFrom()).isNull();
    assertThat(attribute001.getRouteIndexTo()).isNull();

    Note attribute002 = attributes.get(2);
    assertThat(attribute002.getKey()).isEqualTo("WS");
    assertThat(attribute002.getValue()).isEqualTo("Bistro");
    assertThat(attribute002.getPriority()).isNull();
    assertThat(attribute002.getRouteIndexFrom()).isNull();
    assertThat(attribute002.getRouteIndexTo()).isNull();

    Note attribute003 = attributes.get(3);
    assertThat(attribute003.getKey()).isEqualTo("BZ");
    assertThat(attribute003.getValue()).isEqualTo("Business Zone");
    assertThat(attribute003.getPriority()).isNull();
    assertThat(attribute003.getRouteIndexFrom()).isNull();
    assertThat(attribute003.getRouteIndexTo()).isNull();

    Note attribute004 = attributes.get(4);
    assertThat(attribute004.getKey()).isEqualTo("FA");
    assertThat(attribute004.getValue()).isEqualTo("Familien Abteil");
    assertThat(attribute004.getPriority()).isNull();
    assertThat(attribute004.getRouteIndexFrom()).isNull();
    assertThat(attribute004.getRouteIndexTo()).isNull();

    Note attribute005 = attributes.get(5);
    assertThat(attribute005.getKey()).isEqualTo("RZ");
    assertThat(attribute005.getValue()).isEqualTo("Ruhezone");
    assertThat(attribute005.getPriority()).isNull();
    assertThat(attribute005.getRouteIndexFrom()).isNull();
    assertThat(attribute005.getRouteIndexTo()).isNull();

    assertThat(segment.getMessages().isEmpty());
    assertThat(segment.getDirection()).isEqualTo("St. Gallen");
    assertThat(segment.getJourneyStatus()).isEqualTo(Segment.JourneyStatusEnum.PLANNED);
    assertThat(segment.isCancelled()).isFalse();
    assertThat(segment.isPartiallyCancelled()).isFalse();
    assertThat(segment.isReachable()).isTrue();
    assertThat(segment.isRedirected()).isFalse();

    assertThat(trip.getSearchHint()).isNull();
    assertThat(trip.getScrollCheckSum()).isNull();
    assertThat(trip.getEcoBalance()).isNull();
  }
}
