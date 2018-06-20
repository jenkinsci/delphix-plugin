package io.jenkins.plugins.delphix.repos;

import java.io.IOException;
import io.jenkins.plugins.delphix.objects.SelfServiceContainer;
import io.jenkins.plugins.delphix.DelphixEngine;
import io.jenkins.plugins.delphix.DelphixEngineException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class SelfServiceRepositoryTest {

  DelphixEngine delphixEngine = mock(DelphixEngine.class);
  SelfServiceRepository containerRepo = new SelfServiceRepository(delphixEngine);

  @Test public void canGet() throws IOException, DelphixEngineException {
    ObjectMapper mapper = new ObjectMapper();
    String result = "{\"result\":{\"type\":\"JSDataContainer\",\"reference\":\"JS_DATA_CONTAINER-2\",\"namespace\":null,\"name\":\"CTO-Develop\",\"notes\":null,\"properties\":{},\"activeBranch\":\"JS_BRANCH-4\",\"lastUpdated\":\"2018-06-19T15:53:45.469Z\",\"firstOperation\":\"JS_OPERATION-6\",\"lastOperation\":\"JS_OPERATION-200\",\"template\":\"JS_DATA_TEMPLATE-1\",\"state\":\"ONLINE\",\"operationCount\":95,\"owner\":null}}";
    JsonNode json = mapper.readTree(result);

    when(delphixEngine.engineGet(anyString())).thenReturn(json);

    SelfServiceContainer container = containerRepo.get("container");
    assertThat(container, instanceOf(SelfServiceContainer.class));
  }

}
