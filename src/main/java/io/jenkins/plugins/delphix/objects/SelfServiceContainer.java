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

package io.jenkins.plugins.delphix.objects;

import com.fasterxml.jackson.databind.JsonNode;

/* Represents a group in the Delphix Engine */
public class SelfServiceContainer {

  private final String type;
  private final String name;
  private final String activeBranch;
  private final String firstOperation;
  private final String lastOperation;
  private final String lastUpdated;
  private final String notes;
  private final Number operationCount;
  private final String properties;
  private final String reference;
  private final String state;
  private final String template;

  /**
   * Constructor for SelfServiceContainer.
   *
   * @param type           String
   * @param name           String
   * @param activeBranch   String
   * @param firstOperation String
   * @param lastOperation  String
   * @param lastUpdated    String
   * @param notes          String
   * @param operationCount Number
   * @param properties     String
   * @param reference      String
   * @param state          String
   * @param template       String
   */
  public SelfServiceContainer(
      String type,
      String name,
      String activeBranch,
      String firstOperation,
      String lastOperation,
      String lastUpdated,
      String notes,
      Number operationCount,
      String properties,
      String reference,
      String state,
      String template) {
    this.type = type;
    this.name = name;
    this.activeBranch = activeBranch;
    this.firstOperation = firstOperation;
    this.lastOperation = lastOperation;
    this.lastUpdated = lastUpdated;
    this.notes = notes;
    this.operationCount = operationCount;
    this.properties = properties;
    this.reference = reference;
    this.state = state;
    this.template = template;
  }

  public String getType() {
    return this.type;
  }

  public String getName() {
    return this.name;
  }

  public String getActiveBranch() {
    return this.activeBranch;
  }

  public String getFirstOperation() {
    return this.firstOperation;
  }

  public String getLastOperation() {
    return this.lastOperation;
  }

  public String getLastUpdated() {
    return this.lastUpdated;
  }

  public String getNotes() {
    return this.notes;
  }

  public Number getOperationCount() {
    return this.operationCount;
  }

  public String getProperties() {
    return this.properties;
  }

  public String getReference() {
    return this.reference;
  }

  public String getState() {
    return this.state;
  }

  public String getTemplate() {
    return this.template;
  }

  /**
   * Create new SelfServiceContainer from JsonNode.
   *
   * @param  json JsonNode
   * @return      SelfServiceContainer
   */
  public static SelfServiceContainer fromJson(JsonNode json) {
    SelfServiceContainer container =
        new SelfServiceContainer(
            json.get("type").asText(),
            json.get("name").asText(),
            json.get("activeBranch").asText(),
            json.get("firstOperation").asText(),
            json.get("lastOperation").asText(),
            json.get("lastUpdated").asText(),
            json.get("notes").asText(),
            json.get("operationCount").asInt(),
            json.get("properties").asText(),
            json.get("reference").asText(),
            json.get("state").asText(),
            json.get("template").asText());
    return container;
  }
}
