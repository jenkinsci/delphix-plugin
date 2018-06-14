/**
 * Copyright (c) 2018 by Delphix. All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jenkins.plugins.delphix.repos;

import com.fasterxml.jackson.databind.JsonNode;
import io.jenkins.plugins.delphix.DelphixEngine;
import io.jenkins.plugins.delphix.DelphixEngineException;
import io.jenkins.plugins.delphix.SelfServiceRequest;
import io.jenkins.plugins.delphix.objects.SelfServiceContainer;
import org.apache.http.client.ClientProtocolException;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.LinkedHashMap;

/* Used for interacting with a Delphix Engine. */
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
   * List self service containers in the Delphix Engine.
   *
   * @return LinkedHashMap
   * @throws ClientProtocolException [description]
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public LinkedHashMap<String, SelfServiceContainer> listSelfServices()
      throws ClientProtocolException, IOException, DelphixEngineException {
    // Get containers
    LinkedHashMap<String, SelfServiceContainer> environments =
        new LinkedHashMap<String, SelfServiceContainer>();
    JsonNode environmentsJson = engineGet(PATH_ROOT).get(FIELD_RESULT);

    // Loop through container list
    for (int i = 0; i < environmentsJson.size(); i++) {
      JsonNode environmentJson = environmentsJson.get(i);
      SelfServiceContainer environment = SelfServiceContainer.fromJson(environmentJson);
      environments.put(environment.getReference(), environment);
    }

    return environments;
  }

  /**
   * Get Self Service Container by Refernce.
   *
   * @param containerRef String
   * @return SelfServiceContainer
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public SelfServiceContainer get(String containerRef) throws IOException, DelphixEngineException {
    JsonNode result = engineGet(String.format(PATH_ROOT + "/%s", containerRef)).get(FIELD_RESULT);
    SelfServiceContainer container = SelfServiceContainer.fromJson(result);
    return container;
  }

  /**
   * Refreshes a Self Service Container.
   *
   * @param containerRef String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode refresh(String containerRef) throws IOException, DelphixEngineException {
    JsonNode result =
        enginePost(
            String.format(PATH_ROOT + "/%s/refresh", containerRef),
            new SelfServiceRequest("JSDataContainerRefreshParameters", false, "").toJson());
    return result;
  }

  /**
   * Restore a Self Service Container.
   *
   * @param containerRef String
   * @param bookmark String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode restore(String containerRef, String bookmark)
      throws IOException, DelphixEngineException {
    JsonNode result =
        enginePost(
            String.format(PATH_ROOT + "/%s/restore", containerRef),
            new SelfServiceRequest("JSTimelinePointBookmarkInput", false, bookmark).toJson());
    return result;
  }

  /**
   * Reset a Self Service Container.
   *
   * @param containerRef String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode reset(String containerRef) throws IOException, DelphixEngineException {
    JsonNode result =
        enginePost(
            String.format(PATH_ROOT + "/%s/reset", containerRef),
            new SelfServiceRequest("JSDataContainerResetParameters", false, "").toJson());
    return result;
  }

  /**
   * Enable a Self Service Container that has been Disabled.
   *
   * @param containerRef String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode enable(String containerRef) throws IOException, DelphixEngineException {
    JsonNode result = enginePost(String.format(PATH_ROOT + "/%s/enable", containerRef), "{}");
    return result;
  }

  /**
   * Disable a Self Service Container.
   *
   * @param containerRef String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode disable(String containerRef) throws IOException, DelphixEngineException {
    JsonNode result = enginePost(String.format(PATH_ROOT + "/%s/disable", containerRef), "{}");
    return result;
  }

  /**
   * Recover a Self Service Container from the INCONSISTENT state.
   *
   * @param containerRef String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode recover(String containerRef) throws IOException, DelphixEngineException {
    JsonNode result = enginePost(String.format(PATH_ROOT + "/%s/recover", containerRef), "{}");
    return result;
  }

  /**
   * Undo the given operation. This is only valid for RESET, RESTORE, UNDO, and REFRESH operations.
   *
   * @param containerRef String
   * @param actionRef String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode undo(String containerRef, String actionRef)
      throws IOException, DelphixEngineException {
    JsonNode result =
        enginePost(
            String.format(PATH_ROOT + "/%s/undo", containerRef),
            new SelfServiceRequest("JSDataContainerUndoParameters", false, actionRef).toJson());
    return result;
  }

  /**
   * Lock the container to prevent other users from performing any opeartions on it.
   *
   * @param containerRef String
   * @param userRef String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode lock(String containerRef, String userRef)
      throws IOException, DelphixEngineException {
    JsonNode result =
        enginePost(
            String.format(PATH_ROOT + "/%s/lock", containerRef),
            new SelfServiceRequest("JSDataContainerLockParameters", false, userRef).toJson());
    return result;
  }

  /**
   * Unlock the container to let other users perform opeartions on it.
   *
   * @param containerRef String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode unlock(String containerRef) throws IOException, DelphixEngineException {
    JsonNode result = enginePost(String.format(PATH_ROOT + "/%s/unlock", containerRef), "{}");
    return result;
  }
}
