/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 */

package com.delphix.delphix;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import com.delphix.delphix.DelphixContainer.ContainerType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used for interacting with a Delphix Engine
 */
public class DelphixEngine {

    /*
     * Miscellaneous constants
     */
    private static final String PROTOCOL = "http://";
    private static final String ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "application/json";
    private static final String ERROR_RESULT = "ErrorResult";

    /*
     * Paths to endpoints on Delphix Engine
     */
    private static final String PATH_SESSION = "/resources/json/delphix/session";
    private static final String PATH_LOGIN = "/resources/json/delphix/login";
    private static final String PATH_DATABASE = "/resources/json/delphix/database";
    private static final String PATH_REFRESH = "/resources/json/delphix/database/%s/refresh";
    private static final String PATH_SYNC = "/resources/json/delphix/database/%s/sync";
    private static final String PATH_CANCEL_JOB = "/resources/json/delphix/job/%s/cancel";
    private static final String PATH_CONTAINER = "/resources/json/delphix/database/%s";
    private static final String PATH_JOB = "/resources/json/delphix/job/%s";

    /*
     * Content for POST requests to Delphix Engine
     */
    private static final String CONTENT_SESSION = "{\"type\": \"APISession\",\"version\": " +
            "{\"type\": \"APIVersion\",\"major\": 1,\"minor\": 6,\"micro\": 0}}";
    private static final String CONTENT_LOGIN =
            "{\"type\": \"LoginRequest\",\"username\": \"%s\",\"password\": \"%s\"}";
    private static final String CONTENT_REFRESH = "{\"type\": \"%s\", \"timeflowPointParameters\": {" +
            "\"type\": \"TimeflowPointSemantic\",\"container\": \"%s\"" + "}}";
    private static final String CONTENT_SYNC = "{\"type\": \"%s\"}";

    /*
     * Fields used in JSON requests and responses
     */
    private static final String FIELD_EVENTS = "events";
    private static final String FIELD_JOB_STATE = "jobState";
    private static final String FIELD_RESULT = "result";
    private static final String FIELD_PROVISION_CONTAINER = "provisionContainer";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_JOB = "job";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_REFERENCE = "reference";
    private static final String FIELD_TARGET = "target";
    private static final String FIELD_TIMESTAMP = "timestamp";
    private static final String FIELD_MESSAGE_DETAILS = "messageDetails";

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
        if (jsonResult.get(FIELD_TYPE).asText().equals(ERROR_RESULT)) {
            throw new DelphixEngineException(jsonResult.get("error").get("details").asText());
        }
        return jsonResult;
    }

    /**
     * Send GET to Delphix Engine and return the result
     */
    private JsonNode engineGET(final String path) throws IOException, DelphixEngineException {
        // Build and send request
        HttpGet request = new HttpGet(PROTOCOL + engineAddress + path);
        request.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
        HttpResponse response = client.execute(request);

        // Get result of request
        String result = EntityUtils.toString(response.getEntity());
        JsonNode jsonResult = MAPPER.readTree(result);
        EntityUtils.consume(response.getEntity());
        if (jsonResult.get(FIELD_TYPE).asText().equals(ERROR_RESULT)) {
            throw new DelphixEngineException(jsonResult.get("error").get("details").asText());
        }
        return jsonResult;
    }

    /**
     * Login to Delphix Engine
     * Will throw a DelphixEngineException if the login fails due to bad username or password
     */
    public void login() throws IOException, DelphixEngineException {
        // Get session
        enginePOST(PATH_SESSION, CONTENT_SESSION);

        // Login
        enginePOST(PATH_LOGIN, String.format(CONTENT_LOGIN, engineUsername, enginePassword));
    }

    /**
     * List containers in the Delphix Engine
     */
    public ArrayList<DelphixContainer> listContainers()
            throws ClientProtocolException, IOException, DelphixEngineException {
        // Get containers
        ArrayList<DelphixContainer> containers = new ArrayList<DelphixContainer>();
        JsonNode containersJSON = engineGET(PATH_DATABASE).get(FIELD_RESULT);

        // Loop through container list
        for (int i = 0; i < containersJSON.size(); i++) {
            JsonNode containerJSON = containersJSON.get(i);
            ContainerType type;

            /*
             * Set the type of the container.
             * Versions of Delphix before 4.4 classify transformation containers and restoration datasets as VDBs.  They
             *  are differentiated in 4.4.
             */
            if (containerJSON.get(FIELD_PROVISION_CONTAINER).asText().equals("null")) {
                type = ContainerType.SOURCE;
            } else {
                type = ContainerType.VDB;
            }

            // Create container object from JSON result
            DelphixContainer container = new DelphixContainer(engineAddress, containerJSON.get(FIELD_NAME).asText(),
                    containerJSON.get(FIELD_REFERENCE).asText(), type);
            containers.add(container);
        }

        return containers;
    }

    /**
     * Cancel a job running on the Delphix Engine
     */
    public void cancelJob(String jobRef) throws ClientProtocolException, IOException, DelphixEngineException {
        enginePOST(String.format(PATH_CANCEL_JOB, jobRef), "");
    }

    /**
     * Get the status of a job running on the Delphix Engine
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
        return new JobStatus(status, summary, target);
    }

    /**
     * Refresh a virtual database on the Delphix Engine
     */
    public String refresh(String vdbRef) throws IOException, DelphixEngineException {
        // Construct parameters to send to engine
        String type;
        if (getContainerType(vdbRef).equals("OracleDatabaseContainer")) {
            type = "OracleRefreshParameters";
        } else {
            type = "RefreshParameters";
        }

        // Do refresh
        JsonNode result = enginePOST(String.format(PATH_REFRESH, vdbRef), String.format(CONTENT_REFRESH, type,
                getParentContainer(vdbRef)));
        return result.get(FIELD_JOB).asText();
    }

    /**
     * Get the parent of a virtual database on the Delphix Engine
     */
    private String getParentContainer(String vdbRef) throws IOException, DelphixEngineException {
        JsonNode result = engineGET(String.format(PATH_CONTAINER, vdbRef));
        JsonNode container = result.get(FIELD_RESULT);
        return container.get(FIELD_PROVISION_CONTAINER).asText();
    }

    /**
     * Get the type of a container on the Delphix Engine
     */
    private String getContainerType(String containerRef) throws IOException, DelphixEngineException {
        JsonNode result = engineGET(String.format(PATH_CONTAINER, containerRef));
        JsonNode container = result.get(FIELD_RESULT);
        return container.get(FIELD_TYPE).asText();
    }

    /**
     * Run a sync operation for a source on the Delphix Engine
     */
    public String sync(String sourceRef) throws IOException, DelphixEngineException {
        // Construct parameters to send to engine
        String type = getContainerType(sourceRef);
        type = type.replace("Container", "");
        type = type.replace("Database", "");
        type = type + "SyncParameters";

        // Do sync
        JsonNode result = enginePOST(String.format(PATH_SYNC, sourceRef), String.format(CONTENT_SYNC, type));
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
