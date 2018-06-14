/**
 * Copyright (c) 2015 by Delphix. All rights reserved. Licensed under the Apache License, Version
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

package io.jenkins.plugins.delphix;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;
import hudson.model.InvisibleAction;

/* An action to publish a single environment variable. */
public class PublishEnvVarAction extends InvisibleAction implements EnvironmentContributingAction {

  private String key;

  private String value;

  public PublishEnvVarAction(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
    env.put(key, value);
  }
}
