package com.axonivy.connector.sbb.auth;

import javax.ws.rs.Priorities;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import ch.ivyteam.ivy.rest.client.FeatureConfig;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2BearerFilter;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2TokenRequester.AuthContext;
import ch.ivyteam.ivy.rest.client.oauth2.uri.OAuth2UriProperty;

public class OAuth2Feature implements Feature {

  public static interface Default {
    String AUTH_URI = "https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/oauth2/v2.0/token";
  }

  public static interface Property {
    String AUTH_CLIENT_ID = "AUTH.clientId";
    String AUTH_CLIENT_SECRET = "AUTH.clientSecret";
    String AUTH_TOKEN_ENDPOINT = "AUTH.tokenEndpoint";
    String AUTH_SCOPE = "AUTH.scope";
  }

  @Override
  public boolean configure(FeatureContext context) {
    var config = new FeatureConfig(context.getConfiguration(), OAuth2Feature.class);
    if (config.read(Property.AUTH_CLIENT_ID).get().equals("DEMO")) {
      return false;
    }

    var swissMobiltyApiUri = new OAuth2UriProperty(config, Property.AUTH_TOKEN_ENDPOINT, Default.AUTH_URI);

    var oauth2 = new OAuth2BearerFilter(
            ctxt -> requestToken(ctxt),
            swissMobiltyApiUri);
    context.register(oauth2, Priorities.AUTHORIZATION);
    return true;
  }

  private static Response requestToken(AuthContext ctxt) {
    FeatureConfig config = ctxt.config;
    Form form = createTokenPayload(config);
    var response = ctxt.target.request()
            .post(Entity.form(form));
    return response;
  }

  static Form createTokenPayload(FeatureConfig config) {
    Form form = new Form();
    form.param("client_id", config.readMandatory(Property.AUTH_CLIENT_ID));
    form.param("client_secret", config.readMandatory(Property.AUTH_CLIENT_SECRET));
    form.param("grant_type", "client_credentials");
    form.param("scope", config.readMandatory(Property.AUTH_SCOPE));
    return form;
  }
}
