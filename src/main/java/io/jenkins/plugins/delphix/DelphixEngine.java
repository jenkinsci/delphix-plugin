/**
 * Copyright (c) 2015, 2018 by Delphix. All rights reserved. Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jenkins.plugins.delphix;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Used for interacting with a Delphix Engine */
public class DelphixEngine {
  private static final Logger LOGGER = Logger.getLogger(DelphixEngine.class.getName());

  /*
   * Miscellaneous constants
   */
  private static final String PROTOCOL = "http://";
  private static final String ENCODING = "UTF-8";
  private static final String CONTENT_TYPE = "application/json";
  private static final String OK_STATUS = "OK";

  /*
   * Paths to endpoints on Delphix Engine
   */
  private static final String PATH_SESSION = "/resources/json/delphix/session";
  private static final String PATH_LOGIN = "/resources/json/delphix/login";
  private static final String PATH_ENVIRONMENT = "/resources/json/delphix/environment";

  /*
   * Content for POST requests to Delphix Engine
   */
  private static final String CONTENT_SESSION =
      "{\"type\": \"APISession\",\"version\": "
      + "{\"type\": \"APIVersion\",\"major\": %s,\"minor\": %s,\"micro\": %s}}";
  private static final String CONTENT_LOGIN =
      "{\"type\": \"LoginRequest\",\"username\": \"%s\",\"password\": \"%s\"}";
  private static final String CONTENT_ADD_UNIX_ENVIRONMENT =
      "{\"type\": \"HostEnvironmentCreateParameters\",\"primaryUser\":"
      + " {\"type\": \"EnvironmentUser\","
      + "\"name\": \"%s\",\"credential\": {\"type\": \"PasswordCredential\",\"password\": \"%s\"}},"
      + "\"hostEnvironment\": {\"type\": \"UnixHostEnvironment\"},\"hostParameters\": {\"type\": "
      + "\"UnixHostCreateParameters\",\"host\": {\"type\": \"UnixHost\",\"address\": "
      + "\"%s\",\"toolkitPath\": \"%s\"}}}";
  public static final String CONTENT_LATEST_POINT = "LATEST_POINT";
  public static final String CONTENT_LATEST_SNAPSHOT = "LATEST_SNAPSHOT";
  public static final String CONTENT_SYNC_HOOK =
      "{\"operations\":{\"preSync\":%s,\"postSync\":%s, \"type\": \"%s\"}," + "\"type\":\"%s\"}";
  public static final String CONTENT_REFRESH_HOOK =
      "{\"operations\":{\"preRefresh\":%s,\"postRefresh\":%s, \"type\": \"%s\"},"
      + "\"type\":\"%s\"}";
  public static final String CONTENT_ROLLBACK_HOOK =
      "{\"operations\":{\"preRollback\":%s,\"postRollback\":%s, \"type\": \"%s\"},"
      + "\"type\":\"%s\"}";
  public static final String CONTENT_COMPATIBLE_REPOSITORIES =
      "{\"environment\": \"%s\", \"timeflowPointParameters\":%s,"
      + "\"type\":\"ProvisionCompatibilityParameters\"}";

  /*
   * Fields used in JSON requests and responses
   */
  protected static final String FIELD_RESULT = "result";
  protected static final String FIELD_JOB = "job";
  protected static final String FIELD_NAME = "name";
  protected static final String FIELD_REFERENCE = "reference";
  private static final String FIELD_STATUS = "status";

  /* Address of the Delphix Engine */
  private final String engineAddress;

  /* Username for logging into engine */
  private final String engineUsername;

  /* Password of user */
  private final String enginePassword;

  /*
   * Http client used for sending requests to engine
   */
  private final HttpClient client;

  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * Constructor for Delphix Engine.
   *
   * @param engineAddress  [description]
   * @param engineUsername [description]
   * @param enginePassword [description]
   */
  @DataBoundConstructor
  public DelphixEngine(String engineAddress, String engineUsername, String enginePassword) {
    this.engineAddress = engineAddress;
    this.engineUsername = engineUsername;
    this.enginePassword = enginePassword;

    RequestConfig.Builder requestBuilder = RequestConfig.custom();
    requestBuilder = requestBuilder.setConnectTimeout(60 * 1000);
    requestBuilder = requestBuilder.setConnectionRequestTimeout(60 * 1000);
    requestBuilder = requestBuilder.setSocketTimeout(60 * 1000);

    HttpClientBuilder builder = HttpClientBuilder.create();
    builder.setDefaultRequestConfig(requestBuilder.build());
    client = builder.build();
  }

  /**
   * Loaded Engine Constructor.
   *
   * @param engine DelphixEngine
   */
  public DelphixEngine(DelphixEngine engine) {
    this.engineAddress = engine.engineAddress;
    this.engineUsername = engine.engineUsername;
    this.enginePassword = engine.enginePassword;

    RequestConfig.Builder requestBuilder = RequestConfig.custom();
    requestBuilder = requestBuilder.setConnectTimeout(60 * 1000);
    requestBuilder = requestBuilder.setConnectionRequestTimeout(60 * 1000);
    requestBuilder = requestBuilder.setSocketTimeout(60 * 1000);

    HttpClientBuilder builder = HttpClientBuilder.create();
    builder.setDefaultRequestConfig(requestBuilder.build());
    client = builder.build();
  }

  /**
   * Build engine URL based on whether or not they have
   * http or https specified in the engineAddress
   *
   * @param  engineAddress String
   * @param  path          String
   * @return               String
   */
  private String buildUrl(String engineAddress, String path) {
      if (engineAddress.startsWith("http") || engineAddress.startsWith("https")) {
          return engineAddress + path;
      }
      return PROTOCOL + engineAddress + path;
  }

  /**
   * Send POST to Delphix Engine and return the result.
   *
   * @param path String
   * @param content String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  protected JsonNode enginePost(final String path, final String content)
      throws IOException, DelphixEngineException {
    // Log requests
    if (!content.contains("LoginRequest")) {
      LOGGER.log(Level.WARNING, path + ":" + content);
    }

    // Build and send request
    HttpPost request = new HttpPost(buildUrl(engineAddress, path));
    try {
      request.setEntity(new ByteArrayEntity(content.getBytes(ENCODING)));
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e);
    }
    request.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
    HttpResponse response = client.execute(request);

    // Get result of request
    String result = EntityUtils.toString(response.getEntity());
    JsonNode jsonResult = MAPPER.readTree(result);
    EntityUtils.consume(response.getEntity());
    if (!jsonResult.get(FIELD_STATUS).asText().equals(OK_STATUS)) {
      throw new DelphixEngineException(jsonResult.get("error").get("details").toString());
    }

    // Log result
    if (!content.contains("LoginRequest")) {
      LOGGER.log(Level.WARNING, jsonResult.toString());
    }
    return jsonResult;
  }

  /**
   * Send GET to Delphix Engine and return the result.
   *
   * @param path String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  protected JsonNode engineGet(final String path) throws IOException, DelphixEngineException {
    // Log requests
    LOGGER.log(Level.WARNING, path);

    // Build and send request
    HttpGet request = new HttpGet(buildUrl(engineAddress, path));
    request.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
    HttpResponse response = client.execute(request);

    // Get result of request
    String result = EntityUtils.toString(response.getEntity());
    JsonNode jsonResult = MAPPER.readTree(result);
    EntityUtils.consume(response.getEntity());
    if (!jsonResult.get(FIELD_STATUS).asText().equals(OK_STATUS)) {
      throw new DelphixEngineException(jsonResult.get("error").get("details").asText());
    }

    // Log result
    LOGGER.log(Level.WARNING, jsonResult.toString());
    return jsonResult;
  }

  /**
   * Login to Delphix Engine Will throw a DelphixEngineException if the login fails due to bad
   * username or password.
   *
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public void login() throws IOException, DelphixEngineException {
    // Get session with 1.7.0
    enginePost(PATH_SESSION, String.format(CONTENT_SESSION, "1", "7", "0"));

    // Login
    enginePost(PATH_LOGIN, String.format(CONTENT_LOGIN, engineUsername, enginePassword));
  }

  /**
   * Create and discover an environment.
   *
   * @param address String
   * @param user String
   * @param password String
   * @param toolkit String
   * @return String
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public String createEnvironment(String address, String user, String password, String toolkit)
      throws IOException, DelphixEngineException {
    JsonNode result =
        enginePost(
            PATH_ENVIRONMENT,
            String.format(CONTENT_ADD_UNIX_ENVIRONMENT, user, password, address, toolkit));
    return result.get(FIELD_JOB).asText();
  }

  public String getEngineAddress() {
    return engineAddress;
  }

  public String getEngineUsername() {
    return engineUsername;
  }

  public String getEnginePassword() {
    return enginePassword;
  }

  /**
   * Build Engine Dropdown list for different Build Steps.
   *
   * @return ListBoxModel
   */
  public static ListBoxModel fillEnginesForDropdown() {
    ArrayList<Option> options = new ArrayList<Option>();

    // Loop through all engines added to Jenkins
    for (DelphixEngine engine : GlobalConfiguration.getPluginClassDescriptor().getEngines()) {
      DelphixEngine delphixEngine = new DelphixEngine(engine);
      try {
        // login to engine
        try {
          delphixEngine.login();

          options.add(
              new Option(delphixEngine.getEngineAddress(), delphixEngine.getEngineAddress()));
        } catch (DelphixEngineException e) {
          // Add message to drop down if unable to login to engine
          options.add(
              new Option(
                  Messages.getMessage(
                      Messages.UNABLE_TO_LOGIN, new String[] {delphixEngine.getEngineAddress()}),
                  "NULL"));
          continue;
        }
      } catch (IOException e) {
        // Add message to drop down if unable to connect to engine
        options.add(
            new Option(
                Messages.getMessage(
                    Messages.UNABLE_TO_CONNECT, new String[] {delphixEngine.getEngineAddress()}),
                "NULL"));
      }
    }

    // If there are no engines state that in the drop down
    if (GlobalConfiguration.getPluginClassDescriptor().getEngines().size() == 0) {
      // Add message to drop down if no engines in Jenkins
      options.add(new Option(Messages.getMessage(Messages.NO_ENGINES), "NULL"));
    }
    return new ListBoxModel(options);
  }
}
