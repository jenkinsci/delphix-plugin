/**
 * Copyright (c) 2018 by Delphix. All rights reserved.
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
import java.util.LinkedHashMap;
import org.apache.http.client.ClientProtocolException;
import org.kohsuke.stapler.DataBoundConstructor;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Used for interacting with a Delphix Engine
 */
public class SelfServiceEngine extends DelphixEngine {

    private static final String PATH_SELFSERVICE = "/resources/json/delphix/jetstream/container";
    private static final String PATH_REFRESH_SELFSERVICECONTAINER = "/resources/json/delphix/jetstream/container/%s/refresh";
    private static final String PATH_RESTORE_SELFSERVICECONTAINER = "/resources/json/delphix/jetstream/container/%s/restore";
    private static final String PATH_RESET_SELFSERVICECONTAINER = "/resources/json/delphix/jetstream/container/%s/reset";

    /*
     * Content for POST requests to Delphix Engine
     */
    public static final String CONTENT_REFRESH_SELFSERVICECONTAINER = "{}";
    public static final String CONTENT_RESTORE_SELFSERVICECONTAINER =
        "{\"type\":\"JSDataContainerRestoreParameters\",\"forceOption\":false,\"timelinePointParameters\":{\"type\":\"JSTimelinePointParameters\"}}";
    public static final String CONTENT_RESET_SELFSERVICECONTAINER =
        "{\"type\":\"JSDataContainerResetParameters\",\"forceOption\":false}";

    @DataBoundConstructor
    public SelfServiceEngine(String engineAddress, String engineUsername, String enginePassword) {
        super(engineAddress, engineUsername, enginePassword);
    }

    public SelfServiceEngine(DelphixEngine engine) {
        super(engine);
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
}
