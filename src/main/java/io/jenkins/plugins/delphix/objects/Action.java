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

/** Tracks information about the status of a action. */
public class Action {

  private final String type;
  private final String reference;
  private final String namespace;
  private final String title;
  private final String details;
  private final String startTime;
  private final String endTime;
  private final String user;
  private final String userAgent;
  private final String parentAction;
  private final String actionType;
  private final String state;
  private final String workSource;
  private final String workSourceName;
  private final String report;
  private final String failureDescription;
  private final String failureAction;
  private final String failureMessageCode;

  /**
   * Constructor for Action.
   *
   * @param type               String
   * @param reference          String
   * @param namespace          String
   * @param title              String
   * @param details            String
   * @param startTime          String
   * @param endTime            String
   * @param user               String
   * @param userAgent          String
   * @param parentAction       String
   * @param actionType         String
   * @param state              String
   * @param workSource         String
   * @param workSourceName     String
   * @param report             String
   * @param failureDescription String
   * @param failureAction      String
   * @param failureMessageCode String
   */
  public Action(
      String type,
      String reference,
      String namespace,
      String title,
      String details,
      String startTime,
      String endTime,
      String user,
      String userAgent,
      String parentAction,
      String actionType,
      String state,
      String workSource,
      String workSourceName,
      String report,
      String failureDescription,
      String failureAction,
      String failureMessageCode) {
    this.type = type;
    this.reference = reference;
    this.namespace = namespace;
    this.title = title;
    this.details = details;
    this.startTime = startTime;
    this.endTime = endTime;
    this.user = user;
    this.userAgent = userAgent;
    this.parentAction = parentAction;
    this.actionType = actionType;
    this.state = state;
    this.workSource = workSource;
    this.workSourceName = workSourceName;
    this.report = report;
    this.failureDescription = failureDescription;
    this.failureAction = failureAction;
    this.failureMessageCode = failureMessageCode;
  }

  public String getType() {
    return this.type;
  }

  public String getReference() {
    return this.reference;
  }

  public String getNamespace() {
    return this.namespace;
  }

  public String getTitle() {
    return this.title;
  }

  public String getDetails() {
    return this.details;
  }

  public String getStartTime() {
    return this.startTime;
  }

  public String getEndTime() {
    return this.endTime;
  }

  public String getUser() {
    return this.user;
  }

  public String getUserAgent() {
    return this.userAgent;
  }

  public String getParentAction() {
    return this.parentAction;
  }

  public String getActionType() {
    return this.actionType;
  }

  public String getState() {
    return this.state;
  }

  public String getWorkSource() {
    return this.workSource;
  }

  public String getWorkSourceName() {
    return this.workSourceName;
  }

  public String getReport() {
    return this.report;
  }

  public String getFailureDescription() {
    return this.failureDescription;
  }

  public String getFailureAction() {
    return this.failureAction;
  }

  public String getFailureMessageCode() {
    return this.failureMessageCode;
  }

  /**
   * Create new Action from JsonNode.
   *
   * @param  json JsonNode
   * @return      Action
   */
  public static Action fromJson(JsonNode json) {
    Action action =
        new Action(
            json.get("type").asText(),
            json.get("reference").asText(),
            json.get("namespace").asText(),
            json.get("title").asText(),
            json.get("details").asText(),
            json.get("startTime").asText(),
            json.get("endTime").asText(),
            json.get("user").asText(),
            json.get("userAgent").asText(),
            json.get("parentAction").asText(),
            json.get("actionType").asText(),
            json.get("state").asText(),
            json.get("workSource").asText(),
            json.get("workSourceName").asText(),
            json.get("report").asText(),
            json.get("failureDescription").asText(),
            json.get("failureAction").asText(),
            json.get("failureMessageCode").asText());
    return action;
  }
}
