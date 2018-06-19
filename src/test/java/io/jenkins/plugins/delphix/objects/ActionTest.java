package io.jenkins.plugins.delphix.objects;

import java.io.IOException;
import io.jenkins.plugins.delphix.objects.Action;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;

public class ActionTest {
  private Action action = new Action(
          "type",
          "reference",
          "namespace",
          "title",
          "details",
          "startTime",
          "endTime",
          "user",
          "userAgent",
          "parentAction",
          "actionType",
          "state",
          "workSource",
          "workSourceName",
          "report",
          "failureDescription",
          "failureAction",
          "failureMessageCode"
  );

  @Test public void canGetType() {
    assertEquals(action.getType(), "type");
  }

  @Test public void canGetReference() {
    assertEquals(action.getReference() , "reference");
  }

  @Test public void canGetNamespace() {
    assertEquals(action.getNamespace() , "namespace");
  }

  @Test public void canGetTitle() {
    assertEquals(action.getTitle() , "title");
  }

  @Test public void canGetDetails() {
    assertEquals(action.getDetails() , "details");
  }

  @Test public void canGetStartTime() {
    assertEquals(action.getStartTime() , "startTime");
  }

  @Test public void canGetEndTime() {
    assertEquals(action.getEndTime() , "endTime");
  }

  @Test public void canGetUser() {
    assertEquals(action.getUser() , "user");
  }

  @Test public void canGetUserAgent() {
    assertEquals(action.getUserAgent() , "userAgent");
  }

  @Test public void canGetParentAction() {
    assertEquals(action.getParentAction() , "parentAction");
  }
  @Test public void canGetActionType() {
    assertEquals(action.getActionType() , "actionType");
  }

  @Test public void canGetState() {
    assertEquals(action.getState() , "state");
  }

  @Test public void canGetWorkSource() {
    assertEquals(action.getWorkSource() , "workSource");
  }

  @Test public void canGetWorkSourceName() {
    assertEquals(action.getWorkSourceName() , "workSourceName");
  }

  @Test public void canGetReport() {
    assertEquals(action.getReport() , "report");
  }

  @Test public void canGetFailureDescription() {
    assertEquals(action.getFailureDescription() , "failureDescription");
  }

  @Test public void canGetFailureAction() {
    assertEquals(action.getFailureAction() , "failureAction");
  }

  @Test public void canGetFailureMessageCode() {
    assertEquals(action.getFailureMessageCode() , "failureMessageCode");
  }

  @Test public void canLoadFromJSON() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String result = "{\"type\":\"type\",\"reference\":\"reference\",\"namespace\":null,\"title\":\"title\",\"details\":\"details\",\"startTime\":\"2018-06-19T15:53:40.858Z\",\"endTime\":null,\"user\":\"USER-2\",\"userAgent\":\"userAgent\",\"parentAction\":null,\"actionType\":\"actionType\",\"state\":\"WAITING\",\"workSource\":\"workSource\",\"workSourceName\":\"workSourceName\",\"report\":null,\"failureDescription\":null,\"failureAction\":null,\"failureMessageCode\":null},\"job\":null,\"action\":null}";
    JsonNode json = mapper.readTree(result);
    Action action = Action.fromJson(json);
    assertThat(action, instanceOf(Action.class));
  }

}
