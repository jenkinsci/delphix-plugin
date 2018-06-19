package io.jenkins.plugins.delphix.objects;

import java.io.IOException;
import io.jenkins.plugins.delphix.objects.Job;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;

public class JobTest {
  Job.StatusEnum statusEnum = Job.StatusEnum.valueOf("FAILED");
  private Job job = new Job(
    statusEnum,
    "type",
    "reference",
    "namespace",
    "name",
    "actionType",
    "target",
    "targetObjectType",
    "jobState",
    "startTime",
    "updateTime",
    true,
    true,
    true,
    "user",
    "emailAddresses",
    "title",
    100,
    "targetName",
    "parentActionState",
    "parentAction"
  );

  @Test public void canGetStatus() {
    assertEquals(job.getStatus(), statusEnum);
  }

  @Test public void canGetType() {
    assertEquals(job.getType(), "type");
  }

  @Test public void canGetReference() {
    assertEquals(job.getReference(), "reference");
  }

  @Test public void canGetNamespace() {
    assertEquals(job.getNamespace(), "namespace");
  }

  @Test public void canGetName() {
    assertEquals(job.getName(), "name");
  }

  @Test public void canGetActionType() {
    assertEquals(job.getActionType(), "actionType");
  }

  @Test public void canGetTarget() {
    assertEquals(job.getTarget(), "target");
  }

  @Test public void canGetTargetObjectType() {
    assertEquals(job.getTargetObjectType(), "targetObjectType");
  }

  @Test public void canGetJobState() {
    assertEquals(job.getJobState(), "jobState");
  }

  @Test public void canGetStartTime() {
    assertEquals(job.getStartTime(), "startTime");
  }

  @Test public void canGetUpdateTime() {
    assertEquals(job.getUpdateTime(), "updateTime");
  }

  @Test public void canGetSuspendable() {
    assertTrue(job.getSuspendable());
  }

  @Test public void canGetCancelable() {
    assertTrue(job.getCancelable());
  }

  @Test public void canGetQueued() {
    assertTrue(job.getQueued());
  }

  @Test public void canGetUser() {
    assertEquals(job.getUser(), "user");
  }

  @Test public void canGetEmailAddresses() {
    assertEquals(job.getEmailAddresses(), "emailAddresses");
  }

  @Test public void canGetTitle() {
    assertEquals(job.getTitle(), "title");
  }

  @Test public void canGetPercentComplete() {
    assertEquals((Integer)100, job.getPercentComplete());
  }

  @Test public void canGetTargetName() {
    assertEquals(job.getTargetName(), "targetName");
  }

  @Test public void canGetParentActionState() {
    assertEquals(job.getParentActionState(), "parentActionState");
  }

  @Test public void canGetParentAction() {
    assertEquals(job.getParentAction(), "parentAction");
  }

  @Test public void canLoadFromJSON() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String result = "{\"type\":\"Job\",\"reference\":\"JOB-1191\",\"namespace\":null,\"name\":null,\"actionType\":\"JETSTREAM_USER_CONTAINER_REFRESH\",\"target\":\"JS_DATA_CONTAINER-2\",\"targetObjectType\":\"JSDataContainer\",\"jobState\":\"FAILED\",\"startTime\":\"2018-06-19T15:53:41.173Z\",\"updateTime\":\"2018-06-19T15:53:45.367Z\",\"suspendable\":false,\"cancelable\":true,\"queued\":false,\"user\":\"USER-2\",\"emailAddresses\":null,\"title\":\"Refresh Jet Stream data container.\",\"percentComplete\":0.0,\"targetName\":\"targetName\",\"parentActionState\":\"FAILED\",\"parentAction\":\"ACTION-8438\"}";
    JsonNode json = mapper.readTree(result);
    Job job = Job.fromJson(json);
    assertThat(job, instanceOf(Job.class));
  }
}
