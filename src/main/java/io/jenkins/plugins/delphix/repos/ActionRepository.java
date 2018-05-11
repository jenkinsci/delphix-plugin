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

package io.jenkins.plugins.delphix.repos;

import io.jenkins.plugins.delphix.objects.Action;
import io.jenkins.plugins.delphix.DelphixEngine;
import io.jenkins.plugins.delphix.DelphixEngineException;

import java.io.IOException;
import org.kohsuke.stapler.DataBoundConstructor;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Used for interacting with Actions in a Delphix Engine
 */
public class ActionRepository extends DelphixEngine {

    private static final String PATH_ROOT = "/resources/json/delphix/action";

    @DataBoundConstructor
    public ActionRepository(String engineAddress, String engineUsername, String enginePassword) {
        super(engineAddress, engineUsername, enginePassword);
    }

    public ActionRepository(DelphixEngine engine) {
        super(engine);
    }

    /**
     * Get the Status of an Action on the Delphix Engine
     *
     * @param  action                  String
     *
     * @return                         Action
     *
     * @throws IOException             [description]
     * @throws DelphixEngineException  [description]
     */
    public Action get(String action) throws IOException, DelphixEngineException {
        JsonNode result = engineGET(String.format(PATH_ROOT + "/%s", action));
        JsonNode actionStatus = result.get(FIELD_RESULT);
        return Action.fromJson(actionStatus);
    }
}
