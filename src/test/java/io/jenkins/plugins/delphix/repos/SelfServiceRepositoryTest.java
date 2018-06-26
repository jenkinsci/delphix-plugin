package io.jenkins.plugins.delphix.repos;

import java.util.LinkedHashMap;
import java.io.IOException;
import io.jenkins.plugins.delphix.objects.SelfServiceContainer;
import io.jenkins.plugins.delphix.DelphixEngine;
import io.jenkins.plugins.delphix.DelphixEngineException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class SelfServiceRepositoryTest {

  DelphixEngine delphixEngine = mock(DelphixEngine.class);
  SelfServiceRepository containerRepo = new SelfServiceRepository(delphixEngine);

  private JsonNode formatResult(String result) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(result);
  }

  @Test public void canListBookmarks() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"ListResult\",\"status\":\"OK\",\"result\":[{\"type\":\"JSDataContainer\",\"reference\":\"JS_DATA_CONTAINER-1\",\"namespace\":null,\"name\":\"Staging\",\"notes\":null,\"properties\":{},\"activeBranch\":\"JS_BRANCH-2\",\"lastUpdated\":\"2018-06-25T20:58:39.175Z\",\"firstOperation\":\"JS_OPERATION-2\",\"lastOperation\":\"JS_OPERATION-3\",\"template\":\"JS_DATA_TEMPLATE-1\",\"state\":\"ONLINE\",\"operationCount\":2,\"owner\":null}],\"job\":null,\"action\":null,\"total\":1,\"overflow\":false}";
    when(delphixEngine.engineGet(anyString())).thenReturn(formatResult(result));

    LinkedHashMap<String, SelfServiceContainer> containers = containerRepo.listSelfServices();
    SelfServiceContainer container = containers.get("JS_DATA_CONTAINER-1");
    assertEquals(container.getName(), "Staging");
  }

  @Test public void canGet() throws IOException, DelphixEngineException {
    String result = "{\"result\":{\"type\":\"JSDataContainer\",\"reference\":\"JS_DATA_CONTAINER-2\",\"namespace\":null,\"name\":\"CTO-Develop\",\"notes\":null,\"properties\":{},\"activeBranch\":\"JS_BRANCH-4\",\"lastUpdated\":\"2018-06-19T15:53:45.469Z\",\"firstOperation\":\"JS_OPERATION-6\",\"lastOperation\":\"JS_OPERATION-200\",\"template\":\"JS_DATA_TEMPLATE-1\",\"state\":\"ONLINE\",\"operationCount\":95,\"owner\":null}}";
    when(delphixEngine.engineGet(anyString())).thenReturn(formatResult(result));

    SelfServiceContainer container = containerRepo.get("container");
    assertThat(container, instanceOf(SelfServiceContainer.class));
  }

  @Test public void canRefresh() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JOB-1206\",\"action\":\"ACTION-8535\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = containerRepo.refresh("container-ref");
    assertEquals(response.get("action").asText(), "ACTION-8535");
  }

  @Test public void canRecover() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JOB-1206\",\"action\":\"ACTION-8535\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = containerRepo.recover("container-ref");
    assertEquals(response.get("action").asText(), "ACTION-8535");
  }

  @Test public void canRestore() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JOB-1206\",\"action\":\"ACTION-8535\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = containerRepo.restore("container-ref", "bookmark-ref");
    assertEquals(response.get("action").asText(), "ACTION-8535");
  }

  @Test public void canReset() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JOB-1206\",\"action\":\"ACTION-8535\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = containerRepo.reset("container-ref");
    assertEquals(response.get("action").asText(), "ACTION-8535");
  }

  @Test public void canEnable() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JOB-1206\",\"action\":\"ACTION-8535\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = containerRepo.enable("container-ref");
    assertEquals(response.get("action").asText(), "ACTION-8535");
  }

  @Test public void canDisable() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JOB-1206\",\"action\":\"ACTION-8535\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = containerRepo.disable("container-ref");
    assertEquals(response.get("action").asText(), "ACTION-8535");
  }

  @Test public void canUndo() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JOB-1206\",\"action\":\"ACTION-8535\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = containerRepo.undo("container-ref", "action-ref");
    assertEquals(response.get("action").asText(), "ACTION-8535");
  }

  @Test public void canLock() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JOB-1206\",\"action\":\"ACTION-8535\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = containerRepo.lock("container-ref", "user-ref");
    assertEquals(response.get("action").asText(), "ACTION-8535");
  }

  @Test public void canUnlock() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JOB-1206\",\"action\":\"ACTION-8535\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = containerRepo.unlock("container-ref");
    assertEquals(response.get("action").asText(), "ACTION-8535");
  }
}
