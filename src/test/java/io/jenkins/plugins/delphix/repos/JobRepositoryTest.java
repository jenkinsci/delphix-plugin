package io.jenkins.plugins.delphix.repos;

import java.io.IOException;
import io.jenkins.plugins.delphix.objects.Job;
import io.jenkins.plugins.delphix.DelphixEngine;
import io.jenkins.plugins.delphix.DelphixEngineException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class JobRepositoryTest {

  DelphixEngine delphixEngine = mock(DelphixEngine.class);
  JobRepository jobRepo = new JobRepository(delphixEngine);

  @Test public void canGet() throws IOException, DelphixEngineException {
    ObjectMapper mapper = new ObjectMapper();
    String result = "{\"result\": {\"type\":\"Job\",\"reference\":\"JOB-1191\",\"namespace\":null,\"name\":null,\"actionType\":\"JETSTREAM_USER_CONTAINER_REFRESH\",\"target\":\"JS_DATA_CONTAINER-2\",\"targetObjectType\":\"JSDataContainer\",\"jobState\":\"FAILED\",\"startTime\":\"2018-06-19T15:53:41.173Z\",\"updateTime\":\"2018-06-19T15:53:45.367Z\",\"suspendable\":false,\"cancelable\":true,\"queued\":false,\"user\":\"USER-2\",\"emailAddresses\":null,\"title\":\"Refresh Jet Stream data container.\",\"percentComplete\":0.0,\"targetName\":\"targetName\",\"parentActionState\":\"FAILED\",\"parentAction\":\"ACTION-8438\"}}";
    JsonNode json = mapper.readTree(result);

    when(delphixEngine.engineGet(anyString())).thenReturn(json);

    Job job = jobRepo.get("ACTION-8468");
    assertThat(job, instanceOf(Job.class));
  }

}
