/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
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

package com.delphix.delphix;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
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

import com.delphix.delphix.DelphixContainer.ContainerType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Used for interacting with a Delphix Engine
 */
public class DelphixEngine {

    public enum ContainerOperationType {
        REFRESH, SYNC, PROVISIONVDB, DELETECONTAINER
    }

    public enum EnvironmentOperationType {
        CREATE, REFRESH, DELETE
    }

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
    private static final String PATH_SOURCE = "/resources/json/delphix/source";
    private static final String PATH_TIMEFLOW = "/resources/json/delphix/timeflow";
    private static final String PATH_REFRESH = "/resources/json/delphix/database/%s/refresh";
    private static final String PATH_SYNC = "/resources/json/delphix/database/%s/sync";
    private static final String PATH_CANCEL_JOB = "/resources/json/delphix/job/%s/cancel";
    private static final String PATH_CONTAINER = "/resources/json/delphix/database/%s";
    private static final String PATH_JOB = "/resources/json/delphix/job/%s";
    private static final String PATH_PROVISION_DEFAULTS = "/resources/json/delphix/database/provision/defaults";
    private static final String PATH_PROVISION = "/resources/json/delphix/database/provision";
    private static final String PATH_GROUPS = "/resources/json/delphix/group";
    private static final String PATH_DELETE_CONTAINER = "/resources/json/delphix/database/%s/delete";
    private static final String PATH_REFRESH_ENVIRONMENT = "/resources/json/delphix/environment/%s/refresh";
    private static final String PATH_ENVIRONMENT = "/resources/json/delphix/environment";
    private static final String PATH_DELETE_ENVIRONMENT = "/resources/json/delphix/environment/%s/delete";

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
    private static final String CONTENT_PROVISION_DEFAULTS =
            "{\"type\": \"TimeflowPointSemantic\", \"container\": \"%s\"}";
    private static final String CONTENT_DELETE_CONTAINER = "{\"type\": \"DeleteParameters\"}";
    private static final String CONTENT_REFRESH_ENVIRONMENT = "{}";
    private static final String CONTENT_ADD_UNIX_ENVIRONMENT =
            "{\"type\": \"HostEnvironmentCreateParameters\",\"primaryUser\": {\"type\": \"EnvironmentUser\"," +
                    "\"name\": \"%s\",\"credential\": {\"type\": \"PasswordCredential\",\"password\": \"%s\"}}," +
                    "\"hostEnvironment\": {\"type\": \"UnixHostEnvironment\"},\"hostParameters\": {\"type\": " +
                    "\"UnixHostCreateParameters\",\"host\": {\"type\": \"UnixHost\",\"address\": " +
                    "\"%s\",\"toolkitPath\": \"%s\"}}}";
    private static final String CONTENT_DELETE_ENVIRONMENT = "{}";

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
    private static final String FIELD_TARGET_NAME = "targetName";
    private static final String FIELD_TIMESTAMP = "timestamp";
    private static final String FIELD_MESSAGE_DETAILS = "messageDetails";
    private static final String FIELD_GROUP = "group";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_CONTAINER = "container";
    private static final String FIELD_PARENT_POINT = "parentPoint";
    private static final String FIELD_CURRENT_TIMEFLOW = "currentTimeflow";
    private static final String FIELD_RUNTIME = "runtime";

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
            throw new DelphixEngineException(jsonResult.get("error").get("details").toString());
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
     * Login to Delphix Engine Will throw a DelphixEngineException if the login
     * fails due to bad username or password
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
    public LinkedHashMap<String, DelphixContainer> listContainers()
            throws ClientProtocolException, IOException, DelphixEngineException {
        // Get containers
        LinkedHashMap<String, DelphixContainer> containers = new LinkedHashMap<String, DelphixContainer>();
        JsonNode containersJSON = engineGET(PATH_DATABASE).get(FIELD_RESULT);

        // Loop through container list
        for (int i = 0; i < containersJSON.size(); i++) {
            JsonNode containerJSON = containersJSON.get(i);
            ContainerType type;

            /*
             * Set the type of the container. Versions of Delphix before 4.4
             * classify transformation containers and restoration datasets as
             * VDBs. They are differentiated in 4.4.
             */
            if (containerJSON.get(FIELD_PROVISION_CONTAINER).asText().equals("null")) {
                type = ContainerType.SOURCE;
            } else {
                type = ContainerType.VDB;
            }

            // Create container object from JSON result
            DelphixContainer container = new DelphixContainer(engineAddress, containerJSON.get(FIELD_NAME).asText(),
                    containerJSON.get(FIELD_REFERENCE).asText(), type, containerJSON.get(FIELD_GROUP).asText(),
                    containerJSON.get(FIELD_CURRENT_TIMEFLOW).asText());
            containers.put(container.getReference(), container);
        }

        return containers;
    }

    public ArrayList<DelphixGroup> listGroups() throws IOException, DelphixEngineException {
        // Get containers
        ArrayList<DelphixGroup> groups = new ArrayList<DelphixGroup>();
        JsonNode groupsJSON = engineGET(PATH_GROUPS).get(FIELD_RESULT);

        // Loop through group list
        for (int i = 0; i < groupsJSON.size(); i++) {
            JsonNode groupJSON = groupsJSON.get(i);

            // Create group object from JSON result
            DelphixGroup group = new DelphixGroup(groupJSON.get(FIELD_REFERENCE).asText(),
                    groupJSON.get(FIELD_NAME).asText());
            groups.add(group);
        }
        return groups;
    }

    /**
     * List sources in the Delphix Engine
     */
    public LinkedHashMap<String, DelphixSource> listSources()
            throws ClientProtocolException, IOException, DelphixEngineException {
        // Get containers
        LinkedHashMap<String, DelphixSource> sources = new LinkedHashMap<String, DelphixSource>();
        JsonNode sourcesJSON = engineGET(PATH_SOURCE).get(FIELD_RESULT);

        // Loop through container list
        for (int i = 0; i < sourcesJSON.size(); i++) {
            JsonNode sourceJSON = sourcesJSON.get(i);
            // Create container object from JSON result
            DelphixSource source = new DelphixSource(sourceJSON.get(FIELD_REFERENCE).asText(),
                    sourceJSON.get(FIELD_NAME).asText(), sourceJSON.get(FIELD_CONTAINER).asText(),
                    sourceJSON.get(FIELD_RUNTIME).get(FIELD_STATUS).asText());
            sources.put(source.getContainer(), source);
        }

        return sources;
    }

    /**
     * List timeflows in the Delphix Engine
     */
    public LinkedHashMap<String, DelphixTimeflow> listTimeflows()
            throws ClientProtocolException, IOException, DelphixEngineException {
        // Get containers
        LinkedHashMap<String, DelphixTimeflow> timeflows = new LinkedHashMap<String, DelphixTimeflow>();
        JsonNode timeflowsJSON = engineGET(PATH_TIMEFLOW).get(FIELD_RESULT);

        // Loop through container list
        for (int i = 0; i < timeflowsJSON.size(); i++) {
            JsonNode timeflowJSON = timeflowsJSON.get(i);
            // Create container object from JSON result
            JsonNode parentPoint = timeflowJSON.get(FIELD_PARENT_POINT);
            String timestamp = "N/A";
            if (!parentPoint.isNull()) {
                timestamp = parentPoint.get(FIELD_TIMESTAMP).asText();
            }
            DelphixTimeflow timeflow = new DelphixTimeflow(timeflowJSON.get(FIELD_REFERENCE).asText(),
                    timeflowJSON.get(FIELD_NAME).asText(), timestamp, timeflowJSON.get(FIELD_CONTAINER).asText());
            timeflows.put(timeflow.getReference(), timeflow);
        }

        return timeflows;
    }

    /**
     * List environments in the Delphix Engine
     */
    public LinkedHashMap<String, DelphixEnvironment> listEnvironments()
            throws ClientProtocolException, IOException, DelphixEngineException {
        // Get containers
        LinkedHashMap<String, DelphixEnvironment> environments = new LinkedHashMap<String, DelphixEnvironment>();
        JsonNode environmentsJSON = engineGET(PATH_ENVIRONMENT).get(FIELD_RESULT);

        // Loop through container list
        for (int i = 0; i < environmentsJSON.size(); i++) {
            JsonNode environmentJSON = environmentsJSON.get(i);
            DelphixEnvironment environment = new DelphixEnvironment(environmentJSON.get(FIELD_REFERENCE).asText(),
                    environmentJSON.get(FIELD_NAME).asText());
            environments.put(environment.getReference(), environment);
        }

        return environments;
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
        String targetName = jobStatus.get(FIELD_TARGET_NAME).asText();
        return new JobStatus(status, summary, target, targetName);
    }

    /**
     * Refresh a virtual database on the Delphix Engine
     */
    public String refreshContainer(String vdbRef) throws IOException, DelphixEngineException {
        // Construct parameters to send to engine
        String type;
        if (getContainerType(vdbRef).equals("OracleDatabaseContainer")) {
            type = "OracleRefreshParameters";
        } else {
            type = "RefreshParameters";
        }

        // Do refresh
        JsonNode result = enginePOST(String.format(PATH_REFRESH, vdbRef),
                String.format(CONTENT_REFRESH, type, getParentContainer(vdbRef)));
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

    private String getProvisionDefaults(String containerRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(PATH_PROVISION_DEFAULTS, String.format(CONTENT_PROVISION_DEFAULTS, containerRef));
        return result.get(FIELD_RESULT).toString();
    }

    @SuppressWarnings("unchecked")
    public String provisionVDB(String containerRef, String containerName) throws IOException, DelphixEngineException {
        String defaultParams = getProvisionDefaults(containerRef);
        // Strip out null values from provision parameters
        defaultParams = defaultParams.replaceAll("(\"[^\"]+\":null,?|,?\"[^\"]+\":null)", "");
        JsonNode params = MAPPER.readTree(defaultParams);
        if (!containerName.isEmpty()) {
            ObjectNode containerNode = (ObjectNode) params.get("container");
            containerNode.put("name", containerName);
        }
        JsonNode result;
        try {
            result = enginePOST(PATH_PROVISION, params.toString());
        } catch (DelphixEngineException e) {
            // Handle the case where some of the fields in the defaults are read only by removing those fields
            if (e.getMessage().contains("This field is read-only")) {
                JsonNode errors = MAPPER.readTree(e.getMessage());
                List<String> list1 = IteratorUtils.toList(errors.fieldNames());
                for (String field1 : list1) {
                    List<String> list2 = IteratorUtils.toList(errors.get(field1).fieldNames());
                    for (String field2 : list2) {
                        // Field1 is the outer field and field2 is the inner field
                        ObjectNode node = (ObjectNode) params.get(field1);
                        // Remove the inner field
                        node.remove(field2);
                    }
                }
                result = enginePOST(PATH_PROVISION, params.toString());
            } else {
                throw e;
            }
        }
        return result.get(FIELD_JOB).asText();

    }

    public String createEnvironment(String address, String user, String password, String toolkit)
            throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(PATH_ENVIRONMENT,
                String.format(CONTENT_ADD_UNIX_ENVIRONMENT, user, password, address, toolkit));
        return result.get(FIELD_JOB).asText();
    }

    public String deleteContainer(String containerRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(String.format(PATH_DELETE_CONTAINER, containerRef), CONTENT_DELETE_CONTAINER);
        return result.get(FIELD_JOB).asText();
    }

    public String refreshEnvironment(String environmentRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(String.format(PATH_REFRESH_ENVIRONMENT, environmentRef),
                CONTENT_REFRESH_ENVIRONMENT);
        return result.get(FIELD_JOB).asText();
    }

    public String deleteEnvironment(String environmentRef) throws IOException, DelphixEngineException {
        JsonNode result =
                enginePOST(String.format(PATH_DELETE_ENVIRONMENT, environmentRef), CONTENT_DELETE_ENVIRONMENT);
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
