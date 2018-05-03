/**
 * Copyright (c) 2015, 2018 by Delphix. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jenkins.plugins.delphix;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used for interacting with a Delphix Engine
 */
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
    private static final String PATH_CANCEL_JOB = "/resources/json/delphix/job/%s/cancel";
    private static final String PATH_JOB = "/resources/json/delphix/job/%s";
    private static final String PATH_ENVIRONMENT = "/resources/json/delphix/environment";
    private static final String PATH_SYSTEM_INFO = "/resources/json/delphix/system";
    private static final String PATH_SELFSERVICE = "/resources/json/delphix/jetstream/container";
    private static final String PATH_REFRESH_SELFSERVICECONTAINER = "/resources/json/delphix/jetstream/container/%s/refresh";
    private static final String PATH_RESTORE_SELFSERVICECONTAINER = "/resources/json/delphix/jetstream/container/%s/restore";
    private static final String PATH_RESET_SELFSERVICECONTAINER = "/resources/json/delphix/jetstream/container/%s/reset";

    /*
     * Content for POST requests to Delphix Engine
     */
    private static final String CONTENT_SESSION = "{\"type\": \"APISession\",\"version\": " +
            "{\"type\": \"APIVersion\",\"major\": %s,\"minor\": %s,\"micro\": %s}}";
    private static final String CONTENT_LOGIN =
            "{\"type\": \"LoginRequest\",\"username\": \"%s\",\"password\": \"%s\"}";
    private static final String CONTENT_ADD_UNIX_ENVIRONMENT =
            "{\"type\": \"HostEnvironmentCreateParameters\",\"primaryUser\": {\"type\": \"EnvironmentUser\"," +
                    "\"name\": \"%s\",\"credential\": {\"type\": \"PasswordCredential\",\"password\": \"%s\"}}," +
                    "\"hostEnvironment\": {\"type\": \"UnixHostEnvironment\"},\"hostParameters\": {\"type\": " +
                    "\"UnixHostCreateParameters\",\"host\": {\"type\": \"UnixHost\",\"address\": " +
                    "\"%s\",\"toolkitPath\": \"%s\"}}}";
    public static final String CONTENT_LATEST_POINT = "LATEST_POINT";
    public static final String CONTENT_LATEST_SNAPSHOT = "LATEST_SNAPSHOT";
    public static final String CONTENT_SYNC_HOOK =
            "{\"operations\":{\"preSync\":%s,\"postSync\":%s, \"type\": \"%s\"}," +
                    "\"type\":\"%s\"}";
    public static final String CONTENT_REFRESH_HOOK =
            "{\"operations\":{\"preRefresh\":%s,\"postRefresh\":%s, \"type\": \"%s\"}," +
                    "\"type\":\"%s\"}";
    public static final String CONTENT_ROLLBACK_HOOK =
            "{\"operations\":{\"preRollback\":%s,\"postRollback\":%s, \"type\": \"%s\"}," +
                    "\"type\":\"%s\"}";
    public static final String CONTENT_COMPATIBLE_REPOSITORIES =
            "{\"environment\": \"%s\", \"timeflowPointParameters\":%s," +
                    "\"type\":\"ProvisionCompatibilityParameters\"}";
    public static final String CONTENT_REFRESH_SELFSERVICECONTAINER = "{}";
    public static final String CONTENT_RESTORE_SELFSERVICECONTAINER = "{}";
    public static final String CONTENT_RESET_SELFSERVICECONTAINER =
        "{\"type\":\"JSDataContainerResetParameters\",\"forceOption\":false}";

    /*
     * Fields used in JSON requests and responses
     */
    private static final String FIELD_EVENTS = "events";
    private static final String FIELD_JOB_STATE = "jobState";
    private static final String FIELD_RESULT = "result";
    private static final String FIELD_JOB = "job";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_REFERENCE = "reference";
    private static final String FIELD_TARGET = "target";
    private static final String FIELD_TARGET_NAME = "targetName";
    private static final String FIELD_ACTION_TYPE = "actionType";
    private static final String FIELD_TIMESTAMP = "timestamp";
    private static final String FIELD_MESSAGE_DETAILS = "messageDetails";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_API_VERSION = "apiVersion";
    private static final String FIELD_MAJOR = "major";
    private static final String FIELD_MINOR = "minor";
    private static final String FIELD_MICRO = "micro";

    /**
     * Address of the Delphix Engine
     */
    private final String engineAddress;

    /**
     * Username for logging into engine
     */
    private final String engineUsername;

    /**
     * Password of user
     */
    private final String enginePassword;

    /*
     * Http client used for sending requests to engine
     */
    private final HttpClient client;

    private static final ObjectMapper MAPPER = new ObjectMapper();

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
     * Send POST to Delphix Engine and return the result
     */
    private JsonNode enginePOST(final String path, final String content) throws IOException, DelphixEngineException {
        // Log requests
        if (!content.contains("LoginRequest")) {
            LOGGER.log(Level.WARNING, path + ":" + content);
        }

        // Build and send request
        HttpPost request = new HttpPost(PROTOCOL + engineAddress + path);
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
     * Send GET to Delphix Engine and return the result
     */
    private JsonNode engineGET(final String path) throws IOException, DelphixEngineException {
        // Log requests
        LOGGER.log(Level.WARNING, path);

        // Build and send request
        HttpGet request = new HttpGet(PROTOCOL + engineAddress + path);
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
     * Login to Delphix Engine Will throw a DelphixEngineException if the login
     * fails due to bad username or password
     *
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public void login() throws IOException, DelphixEngineException {
        // Get session with 1.0.0
        enginePOST(PATH_SESSION, String.format(CONTENT_SESSION, "1", "0", "0"));

        // Login
        enginePOST(PATH_LOGIN, String.format(CONTENT_LOGIN, engineUsername, enginePassword));

        // Find the most recent API session for this engine
        JsonNode version = engineGET(PATH_SYSTEM_INFO).get(FIELD_RESULT).get(FIELD_API_VERSION);

        // Get session with most recent API session
        enginePOST(PATH_SESSION, String.format(CONTENT_SESSION, version.get(FIELD_MAJOR), version.get(FIELD_MINOR),
                version.get(FIELD_MICRO)));

        // Login with most recent API session
        enginePOST(PATH_LOGIN, String.format(CONTENT_LOGIN, engineUsername, enginePassword));
    }

    /**
     * List self service containers in the Delphix Engine
     *
     * @return LinkedHashMap
     *
     * @throws ClientProtocolException [description]
     * @throws IOException             [description]
     * @throws DelphixEngineException  [description]
     */
    public LinkedHashMap<String, DelphixSelfService> listSelfServices()
            throws ClientProtocolException, IOException, DelphixEngineException {
        // Get containers
        LinkedHashMap<String, DelphixSelfService> environments = new LinkedHashMap<String, DelphixSelfService>();
        JsonNode environmentsJSON = engineGET(PATH_SELFSERVICE).get(FIELD_RESULT);

        // Loop through container list
        for (int i = 0; i < environmentsJSON.size(); i++) {
            JsonNode environmentJSON = environmentsJSON.get(i);
            DelphixSelfService environment = new DelphixSelfService(environmentJSON.get(FIELD_REFERENCE).asText(),
                    environmentJSON.get(FIELD_NAME).asText());
            environments.put(environment.getReference(), environment);
        }

        return environments;
    }

    /**
     * Cancel a job running on the Delphix Engine
     *
     * @param  jobRef                  String
     *
     * @throws ClientProtocolException [description]
     * @throws IOException             [description]
     * @throws DelphixEngineException  [description]
     */
    public void cancelJob(String jobRef) throws ClientProtocolException, IOException, DelphixEngineException {
        enginePOST(String.format(PATH_CANCEL_JOB, jobRef), "");
    }

    /**
     * Get the status of a job running on the Delphix Engine
     *
     * @param  job                     String
     *
     * @return                         JobStatus
     *
     * @throws ClientProtocolException [description]
     * @throws IOException             [description]
     * @throws DelphixEngineException  [description]
     */
    public JobStatus getJobStatus(String job) throws ClientProtocolException, IOException, DelphixEngineException {
        // Get job status
        JsonNode result = engineGET(String.format(PATH_JOB, job));

        // Parse JSON to construct object
        JsonNode jobStatus = result.get(FIELD_RESULT);
        JsonNode events = jobStatus.get(FIELD_EVENTS);
        JsonNode recentEvent = events.get(events.size() - 1);
        JobStatus.StatusEnum status = JobStatus.StatusEnum.valueOf(jobStatus.get(FIELD_JOB_STATE).asText());
        String summary =
                recentEvent.get(FIELD_TIMESTAMP).asText() + " - " + recentEvent.get(FIELD_MESSAGE_DETAILS).asText();
        String target = jobStatus.get(FIELD_TARGET).asText();
        String targetName = jobStatus.get(FIELD_TARGET_NAME).asText();
        String actionType = jobStatus.get(FIELD_ACTION_TYPE).asText();
        return new JobStatus(status, summary, target, targetName, actionType);
    }

    /**
     * Create and discover an environment
     *
     * @param  address                String
     * @param  user                   String
     * @param  password               String
     * @param  toolkit                String
     *
     * @return                        String
     *
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public String createEnvironment(String address, String user, String password, String toolkit)
            throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(PATH_ENVIRONMENT,
                String.format(CONTENT_ADD_UNIX_ENVIRONMENT, user, password, address, toolkit));
        return result.get(FIELD_JOB).asText();
    }

    /**
     * Refreshes a Self Service Container
     *
     * @param  environmentRef         String
     * @return                        String
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public String refreshSelfServiceContainer(String environmentRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(String.format(PATH_REFRESH_SELFSERVICECONTAINER, environmentRef),
                CONTENT_REFRESH_SELFSERVICECONTAINER);
        return result.get(FIELD_JOB).asText();
    }

    /**
     * Restore a Self Service Container
     *
     * @param  environmentRef         String
     * @return                        String
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public String restoreSelfServiceContainer(String environmentRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(String.format(PATH_RESTORE_SELFSERVICECONTAINER, environmentRef),
                CONTENT_RESTORE_SELFSERVICECONTAINER);
        return result.get(FIELD_JOB).asText();
    }

    /**
     * Reset a Self Service Container
     * @param  environmentRef         String
     * @return                        String
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public String resetSelfServiceContainer(String environmentRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(String.format(PATH_RESET_SELFSERVICECONTAINER, environmentRef),
                CONTENT_RESET_SELFSERVICECONTAINER);
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
}
