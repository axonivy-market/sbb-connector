package com.axonivy.connector.sbb.test;

import org.junit.jupiter.api.BeforeEach;

import com.axonivy.connector.sbb.test.constant.Constant;

import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;

@IvyProcessTest
public class BaseTest {

	@BeforeEach
	void setup(AppFixture fixture) {
		fixture.var(Constant.CLIENT_ID_VARIABLE, Constant.CLIENT_ID_VALUE);
		fixture.var(Constant.JOURNEY_URI_VARIABLE, Constant.JOURNEY_URI_VALUE);
	}
}
