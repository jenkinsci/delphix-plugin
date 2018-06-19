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
import io.jenkins.plugins.delphix.objects.User;

import java.io.IOException;

/* Used for interacting with a Delphix Engine */
public class UserRepository {

  private static final String PATH_ROOT = "/resources/json/delphix/user/";
  public DelphixEngine delphixEngine;

  public UserRepository(DelphixEngine engine) {
    delphixEngine = engine;
  }

  /**
   * Get the currently logged in user. This may be null if the Delphix Engine has not been
   * configured yet.
   *
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public User getCurrent() throws IOException, DelphixEngineException {
    JsonNode result = delphixEngine.engineGet(PATH_ROOT + "current").get("result");
    return User.fromJson(result);
  }
}
