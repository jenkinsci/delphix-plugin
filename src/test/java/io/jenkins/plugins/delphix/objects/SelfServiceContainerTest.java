package io.jenkins.plugins.delphix.objects;

import java.io.IOException;
import io.jenkins.plugins.delphix.objects.SelfServiceContainer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;

public class SelfServiceContainerTest {
  private SelfServiceContainer selfServiceContainer = new SelfServiceContainer(
    "type",
    "name",
    "activeBranch",
    "firstOperation",
    "lastOperation",
    "lastUpdated",
    "notes",
    90,
    "properties",
    "reference",
    "state",
    "template"
  );

  @Test public void canGetType() {
    assertEquals(selfServiceContainer.getType(), "type");
  }

  @Test public void canGetName() {
    assertEquals(selfServiceContainer.getName(), "name");
  }

  @Test public void canGetActiveBranch() {
    assertEquals(selfServiceContainer.getActiveBranch(), "activeBranch");
  }

  @Test public void canGetFirstOperation() {
    assertEquals(selfServiceContainer.getFirstOperation(), "firstOperation");
  }

  @Test public void canGetLastOperation() {
    assertEquals(selfServiceContainer.getLastOperation(), "lastOperation");
  }

  @Test public void canGetLastUpdated() {
    assertEquals(selfServiceContainer.getLastUpdated(), "lastUpdated");
  }

  @Test public void canGetNotes() {
    assertEquals(selfServiceContainer.getNotes(), "notes");
  }

  @Test public void canGetOperationCount() {
    assertEquals((Integer)90, selfServiceContainer.getOperationCount());
  }

  @Test public void canGetProperties() {
    assertEquals(selfServiceContainer.getProperties(), "properties");
  }

  @Test public void canGetReference() {
    assertEquals(selfServiceContainer.getReference(), "reference");
  }

  @Test public void canGetState() {
    assertEquals(selfServiceContainer.getState(), "state");
  }

  @Test public void canGetTemplate() {
    assertEquals(selfServiceContainer.getTemplate(), "template");
  }

  @Test public void canLoadFromJSON() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String result = "{\"type\":\"JSDataContainer\",\"reference\":\"JS_DATA_CONTAINER-2\",\"namespace\":null,\"name\":\"CTO-Develop\",\"notes\":null,\"properties\":{},\"activeBranch\":\"JS_BRANCH-4\",\"lastUpdated\":\"2018-06-19T15:53:45.469Z\",\"firstOperation\":\"JS_OPERATION-6\",\"lastOperation\":\"JS_OPERATION-200\",\"template\":\"JS_DATA_TEMPLATE-1\",\"state\":\"ONLINE\",\"operationCount\":95,\"owner\":null}";
    JsonNode json = mapper.readTree(result);
    SelfServiceContainer selfServiceContainer = SelfServiceContainer.fromJson(json);
    assertThat(selfServiceContainer, instanceOf(SelfServiceContainer.class));
  }
}
