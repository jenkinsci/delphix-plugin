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
import io.jenkins.plugins.delphix.objects.SelfServiceBookmark;

import java.io.IOException;
import java.util.LinkedHashMap;
import org.apache.http.client.ClientProtocolException;
import org.kohsuke.stapler.DataBoundConstructor;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Used for interacting with a Delphix Engine
 */
public class SelfServiceRepository extends DelphixEngine {

    private static final String PATH_ROOT = "/resources/json/delphix/jetstream/";

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
        JsonNode environmentsJSON = engineGET(PATH_ROOT + "container").get(FIELD_RESULT);

        // Loop through container list
        for (int i = 0; i < environmentsJSON.size(); i++) {
            JsonNode environmentJSON = environmentsJSON.get(i);
            SelfServiceContainer environment = new SelfServiceContainer(environmentJSON.get(FIELD_REFERENCE).asText(),
                    environmentJSON.get(FIELD_NAME).asText());
            environments.put(environment.getReference(), environment);
        }

        return environments;
    }

    /**
     * List Bookmarks in the Delphix Engine
     *
     * @return LinkedHashMap
     *
     * @throws ClientProtocolException [description]
     * @throws IOException             [description]
     * @throws DelphixEngineException  [description]
     */
    public LinkedHashMap<String, SelfServiceBookmark> listBookmarks()
        throws ClientProtocolException, IOException, DelphixEngineException {
            LinkedHashMap<String, SelfServiceBookmark> bookmarks = new LinkedHashMap<String, SelfServiceBookmark>();
            JsonNode bookmarksJSON = engineGET(PATH_ROOT + "bookmark").get(FIELD_RESULT);

            for (int i = 0; i < bookmarksJSON.size(); i++) {
                JsonNode bookmarkJSON = bookmarksJSON.get(i);
                SelfServiceBookmark bookmark = new SelfServiceBookmark(
                        bookmarkJSON.get(FIELD_REFERENCE).asText(),
                        bookmarkJSON.get(FIELD_NAME).asText());
                bookmarks.put(bookmark.getReference(), bookmark);
            }

            return bookmarks;
    }

    /**
     * Refreshes a Self Service Container
     *
     * @param  environmentRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode refreshSelfServiceContainer(String environmentRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(String.format(
            PATH_ROOT + "container/%s/refresh", environmentRef),
            new SelfServiceRequest("JSDataContainerRefreshParameters", false, "").toJson()
        );
        return result;
    }

    /**
     * Restore a Self Service Container
     *
     * @param  environmentRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode restoreSelfServiceContainer(String environmentRef, String bookmark) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "container/%s/restore", environmentRef),
            new SelfServiceRequest("JSDataContainerRestoreParameters", false, bookmark).toJson()
        );
        return result;
    }

    /**
     * Reset a Self Service Container
     * @param  environmentRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode resetSelfServiceContainer(String environmentRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "container/%s/reset", environmentRef),
            new SelfServiceRequest("JSDataContainerResetParameters", false, "").toJson()
        );
        return result;
    }

    /**
     * Enable a Self Service Container that has been Disabled.
     * @param  environmentRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode enableSelfServiceContainer(String environmentRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "container/%s/enable", environmentRef),
            "{}"
        );
        return result;
    }

    /**
     * Disable a Self Service Container
     * @param  environmentRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode disableSelfServiceContainer(String environmentRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "container/%s/disable", environmentRef),
            "{}"
        );
        return result;
    }

    /**
     * Recover a Self Service Container from the INCONSISTENT state
     * @param  environmentRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode recoverSelfServiceContainer(String environmentRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "container/%s/recover", environmentRef),
            "{}"
        );
        return result;
    }

    /**
     * Lock the container to prevent other users from performing any opeartions on it.
     * @param  environmentRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode lockSelfServiceContainer(String environmentRef, String userRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "container/%s/lock", environmentRef),
            new SelfServiceRequest("JSDataContainerLockParameters", false, userRef).toJson()
        );
        return result;
    }

    /**
     * Unlock the container to let other users perform opeartions on it.
     *
     * @param  environmentRef         String
     * @return                        JsonNode
     * @throws IOException            [description]
     * @throws DelphixEngineException [description]
     */
    public JsonNode unlockSelfServiceContainer(String environmentRef) throws IOException, DelphixEngineException {
        JsonNode result = enginePOST(
            String.format(PATH_ROOT + "container/%s/unlock", environmentRef),
            "{}"
        );
        return result;
    }
}
