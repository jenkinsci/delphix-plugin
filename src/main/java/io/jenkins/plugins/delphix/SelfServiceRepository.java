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
import io.jenkins.plugins.delphix.objects.SelfServiceContainer;

import java.io.IOException;
import java.util.LinkedHashMap;
import org.apache.http.client.ClientProtocolException;
import org.kohsuke.stapler.DataBoundConstructor;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Used for interacting with a Delphix Engine
 */
public class SelfServiceRepository extends DelphixEngine {

    private static final String PATH_ROOT = "/resources/json/delphix/jetstream/container";

    @DataBoundConstructor
    public SelfServiceRepository(String engineAddress, String engineUsername, String enginePassword) {
        super(engineAddress, engineUsername, enginePassword);
    }

    public SelfServiceRepository(DelphixEngine engine) {
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
    public LinkedHashMap<String, SelfServiceContainer> listSelfServices()
            throws ClientProtocolException, IOException, DelphixEngineException {
        // Get containers
        LinkedHashMap<String, SelfServiceContainer> environments = new LinkedHashMap<String, SelfServiceContainer>();
        JsonNode environmentsJSON = engineGET(PATH_ROOT).get(FIELD_RESULT);

        // Loop through container list
        for (int i = 0; i < environmentsJSON.size(); i++) {
            JsonNode environmentJSON = environmentsJSON.get(i);
            SelfServiceContainer environment = SelfServiceContainer.fromJson(environmentJSON);
            environments.put(environment.getReference(), environment);
        }

        return environments;
    }

    /**
     * Get Self Service Container by Refernce
     *
     * @param  containerRef           String
     * @return                        SelfServiceContainer
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public SelfServiceContainer getSelfServiceContainer(String containerRef) throws IOException, DelphixEngineException {
        JsonNode result = engineGET(String.format(
            PATH_ROOT + "/%s", containerRef)
        ).get(FIELD_RESULT);
        SelfServiceContainer container = SelfServiceContainer.fromJson(result);
        return container;
    }

    /**
     * Refreshes a Self Service Container
     *
     * @param  containerRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode refreshSelfServiceContainer(String containerRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(String.format(
            PATH_ROOT + "/%s/refresh", containerRef),
            new SelfServiceRequest("JSDataContainerRefreshParameters", false, "").toJson()
        );
        return result;
    }

    /**
     * Restore a Self Service Container
     *
     * @param  containerRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode restoreSelfServiceContainer(String containerRef, String bookmark) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "/%s/restore", containerRef),
            new SelfServiceRequest("JSDataContainerRestoreParameters", false, bookmark).toJson()
        );
        return result;
    }

    /**
     * Reset a Self Service Container
     * @param  containerRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode resetSelfServiceContainer(String containerRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "/%s/reset", containerRef),
            new SelfServiceRequest("JSDataContainerResetParameters", false, "").toJson()
        );
        return result;
    }

    /**
     * Enable a Self Service Container that has been Disabled.
     * @param  containerRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode enableSelfServiceContainer(String containerRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "/%s/enable", containerRef),
            "{}"
        );
        return result;
    }

    /**
     * Disable a Self Service Container
     * @param  containerRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode disableSelfServiceContainer(String containerRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "/%s/disable", containerRef),
            "{}"
        );
        return result;
    }

    /**
     * Recover a Self Service Container from the INCONSISTENT state
     * @param  containerRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode recoverSelfServiceContainer(String containerRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "/%s/recover", containerRef),
            "{}"
        );
        return result;
    }

    /**
     * Undo the given operation. This is only valid for RESET, RESTORE, UNDO, and REFRESH operations.
     *
     * @param  containerRef         String
     * @param  actionRef              String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode undoSelfServiceContainer(String containerRef, String actionRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "/%s/undo", containerRef),
            new SelfServiceRequest("JSDataContainerUndoParameters", false, actionRef).toJson()
        );
        return result;
    }

    /**
     * Lock the container to prevent other users from performing any opeartions on it.
     * @param  containerRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode lockSelfServiceContainer(String containerRef, String userRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "/%s/lock", containerRef),
            new SelfServiceRequest("JSDataContainerLockParameters", false, userRef).toJson()
        );
        return result;
    }

    /**
     * Unlock the container to let other users perform opeartions on it.
     *
     * @param  containerRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode unlockSelfServiceContainer(String containerRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "/%s/unlock", containerRef),
            "{}"
        );
        return result;
    }
}
