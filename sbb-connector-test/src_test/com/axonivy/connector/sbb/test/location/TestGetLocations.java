package com.axonivy.connector.sbb.test.location;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.axonivy.connector.sbb.location.GetLocationsDataHeaders;
import com.axonivy.connector.sbb.location.GetLocationsDataIn;
import com.axonivy.connector.sbb.location.GetLocationsDataParameters;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.engine.client.sub.SubProcessCallResult;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.scripting.objects.List;
import ch.sbb.app.b2p.client.Location;
import ch.sbb.app.b2p.client.Location.VehicleTypesEnum;

@IvyProcessTest
@SuppressWarnings("unchecked")
class TestGetLocations {

  private static final BpmProcess GET_LOCATIONS_PROCESS = BpmProcess.path("GetLocations");
  private static final BpmElement GET_LOCATIONS_START = GET_LOCATIONS_PROCESS
          .elementName("call(GetLocationsDataIn)");
  private static final BpmElement GET_LOCATIONS = GET_LOCATIONS_PROCESS.elementName("Get Locations");

  @Test
  void call_mockedApi_returnsMockedResponse(BpmClient bpmClient) {
    // Arguments
    GetLocationsDataHeaders getLocationsDataHeaders = new GetLocationsDataHeaders();
    getLocationsDataHeaders.setConversationId("ConversationId");
    getLocationsDataHeaders.setAcceptLanguage("AcceptLanguage");

    GetLocationsDataParameters getLocationsDataParameters = new GetLocationsDataParameters();
    getLocationsDataParameters.setName("Name");

    GetLocationsDataIn getLocationsDataIn = new GetLocationsDataIn();
    getLocationsDataIn.setHeaders(getLocationsDataHeaders);
    getLocationsDataIn.setParameters(getLocationsDataParameters);

    // Mocks
    List<Location> locations = List.create(Location.class);

    bpmClient
            .mock()
            .element(GET_LOCATIONS)
            .with(in -> {
              try {
                GetLocationsDataIn inGetLocationsDataIn = (GetLocationsDataIn) in.get("in");

                GetLocationsDataHeaders inGetLocationsDataInHeaders = (GetLocationsDataHeaders) inGetLocationsDataIn
                        .get("headers");
                assertThat(inGetLocationsDataInHeaders).isEqualTo(getLocationsDataHeaders);
                assertThat(inGetLocationsDataInHeaders.get("conversationId")).isEqualTo("ConversationId");
                assertThat(inGetLocationsDataInHeaders.get("acceptLanguage")).isEqualTo("AcceptLanguage");

                GetLocationsDataParameters inGetLocationsDataInParameters = (GetLocationsDataParameters) inGetLocationsDataIn
                        .get("parameters");
                assertThat(inGetLocationsDataInParameters).isEqualTo(getLocationsDataParameters);
                assertThat(inGetLocationsDataInParameters.get("name")).isEqualTo("Name");

                in.set("locations", locations);
                return in;
              } catch (NoSuchFieldException ex) {
                throw new RuntimeException(ex);
              }
            });

    // Run
    SubProcessCallResult result = bpmClient
            .start()
            .subProcess(GET_LOCATIONS_START)
            .execute(getLocationsDataIn)
            .subResult();

    // Assert
    assertThat(result.param("locations", List.class)).isSameAs(locations);
  }

  @Test
  void call_realApi_returnsRealResponse(BpmClient bpmClient) {
    // Arguments
    GetLocationsDataHeaders getLocationsDataHeaders = new GetLocationsDataHeaders();

    GetLocationsDataParameters getLocationsDataParameters = new GetLocationsDataParameters();
    getLocationsDataParameters.setName("Name");

    GetLocationsDataIn getLocationsDataIn = new GetLocationsDataIn();
    getLocationsDataIn.setHeaders(getLocationsDataHeaders);
    getLocationsDataIn.setParameters(getLocationsDataParameters);

    // Run
    SubProcessCallResult result = bpmClient
            .start()
            .subProcess(GET_LOCATIONS_START)
            .execute(getLocationsDataIn)
            .subResult();

    // Assert
    List<Location> locations = result.param("locations", List.class);
    assertThat(locations.size()).isEqualTo(1);

    Location location = locations.get(0);
    assertThat(location.getLinks()).isEmpty();
    assertThat(location.getId()).isEqualTo(8507000);
    assertThat(location.getName()).isEqualTo("Bern");
    assertThat(location.getCoordinates().getLatitude()).isEqualTo(46.948825);
    assertThat(location.getCoordinates().getLongitude()).isEqualTo(7.439122);
    assertThat(location.getType()).isEqualTo("STATION");
    assertThat(location.isTariffBorder()).isFalse();
    assertThat(location.getWeight()).isNull();
    assertThat(location.getVehicleTypes()).isEqualTo(java.util.List.of(VehicleTypesEnum.TRAIN, VehicleTypesEnum.TRAMWAY));
  }
}
