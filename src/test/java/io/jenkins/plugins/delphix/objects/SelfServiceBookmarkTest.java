package io.jenkins.plugins.delphix.objects;

import java.io.IOException;
import io.jenkins.plugins.delphix.objects.SelfServiceBookmark;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;

public class SelfServiceBookmarkTest {
  private SelfServiceBookmark selfServiceBookmark = new SelfServiceBookmark(
    "type",
    "reference",
    "namespace",
    "name",
    "branch",
    "timestamp",
    "description",
    true,
    "container",
    "template",
    "containerName",
    "templateName",
    true,
    10,
    "bookmarkType",
    true,
    "creationTime"
  );

  @Test public void canGetType() {
    assertEquals(selfServiceBookmark.getType(), "type");
  }

  @Test public void canGetReference() {
    assertEquals(selfServiceBookmark.getReference(), "reference");
  }

  @Test public void canGetNamespace() {
    assertEquals(selfServiceBookmark.getNamespace(), "namespace");
  }

  @Test public void canGetName() {
    assertEquals(selfServiceBookmark.getName(), "name");
  }

  @Test public void canGetBranch() {
    assertEquals(selfServiceBookmark.getBranch(), "branch");
  }

  @Test public void canGetTimestamp() {
    assertEquals(selfServiceBookmark.getTimestamp(), "timestamp");
  }

  @Test public void canGetDescription() {
    assertEquals(selfServiceBookmark.getDescription(), "description");
  }

  @Test public void canGetShared() {
    assertTrue(selfServiceBookmark.getShared());
  }

  @Test public void canGetContainer() {
    assertEquals(selfServiceBookmark.getContainer(), "container");
  }

  @Test public void canGetTemplate() {
    assertEquals(selfServiceBookmark.getTemplate(), "template");
  }

  @Test public void canGetContainerName() {
    assertEquals(selfServiceBookmark.getContainerName(), "containerName");
  }

  @Test public void canGetTemplateName() {
    assertEquals(selfServiceBookmark.getTemplateName(), "templateName");
  }

  @Test public void canGetUsable() {
    assertTrue(selfServiceBookmark.getUsable());
  }

  @Test public void canGetCheckoutCount() {
    assertEquals((Integer)10, selfServiceBookmark.getCheckoutCount());
  }

  @Test public void canGetBookmarkType() {
    assertEquals(selfServiceBookmark.getBookmarkType(), "bookmarkType");
  }

  @Test public void canGetExpiration() {
    assertTrue(selfServiceBookmark.getExpiration());
  }

  @Test public void canGetCreationTime() {
    assertEquals(selfServiceBookmark.getCreationTime(), "creationTime");
  }

  @Test public void canLoadFromJSON() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String result = "{\"type\":\"JSBookmark\",\"reference\":\"JS_BOOKMARK-32\",\"namespace\":null,\"name\":\"Created by Jenkins: Job #237\",\"branch\":\"JS_BRANCH-3\",\"timestamp\":\"2018-05-24T22:02:01.922Z\",\"description\":null,\"shared\":false,\"container\":\"JS_DATA_CONTAINER-1\",\"template\":\"JS_DATA_TEMPLATE-1\",\"containerName\":\"CTO-Staging\",\"templateName\":\"CTO-SDLC\",\"usable\":true,\"checkoutCount\":1,\"bookmarkType\":\"DATA_CONTAINER\",\"expiration\":null,\"creationTime\":\"2018-05-24T22:01:59.604Z\"}";
    JsonNode json = mapper.readTree(result);
    SelfServiceBookmark selfServiceBookmark = SelfServiceBookmark.fromJson(json);
    assertThat(selfServiceBookmark, instanceOf(SelfServiceBookmark.class));
  }
}
