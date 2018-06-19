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
import io.jenkins.plugins.delphix.objects.Job;

import java.io.IOException;

/* Used for interacting with Jobs in a Delphix Engine */
public class JobRepository {

  private static final String PATH_ROOT = "/resources/json/delphix/job";
  private DelphixEngine delphixEngine;

  public JobRepository(DelphixEngine engine) {
    delphixEngine = engine;
  }

  /**
   * Cancel a job running on the Delphix Engine.
   *
   * @param jobRef String
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public void cancel(String jobRef) throws IOException, DelphixEngineException {
    delphixEngine.enginePost(String.format(PATH_ROOT + "/%s/cancel", jobRef), "{}");
  }

  /**
   * Get the status of a job running on the Delphix Engine.
   *
   * @param jobRef String
   * @return Job
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public Job get(String jobRef) throws IOException, DelphixEngineException {
    JsonNode result = delphixEngine.engineGet(String.format(PATH_ROOT + "/%s", jobRef));
    JsonNode jobStatus = result.get("result");
    return Job.fromJson(jobStatus);
  }
}
