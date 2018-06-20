package io.jenkins.plugins.delphix.repos;

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

public class SelfServiceBookmarkRepositoryTest {

  DelphixEngine delphixEngine = mock(DelphixEngine.class);
  SelfServiceBookmarkRepository bookmarkRepo = new SelfServiceBookmarkRepository(delphixEngine);

  private JsonNode formatResult(String result) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(result);
  }

  @Test public void canList() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"ListResult\",\"status\":\"OK\",\"result\":[{\"type\":\"JSBookmark\",\"reference\":\"JS_BOOKMARK-1\",\"namespace\":null,\"name\":\"Initial Bookmark\",\"branch\":\"JS_BRANCH-1\",\"timestamp\":\"2018-04-30T14:31:18.801Z\",\"description\":null,\"tags\":[],\"shared\":true,\"container\":null,\"template\":\"JS_DATA_TEMPLATE-1\",\"containerName\":null,\"templateName\":\"CTO-SDLC\",\"usable\":true,\"checkoutCount\":10,\"bookmarkType\":\"DATA_TEMPLATE\",\"expiration\":null,\"creationTime\":\"2018-04-30T14:31:18.477Z\"}],\"job\":null,\"action\":null,\"total\":19,\"overflow\":false}";
    when(delphixEngine.engineGet(anyString())).thenReturn(formatResult(result));

    JsonNode response = bookmarkRepo.list();
    JsonNode object = response.get(0);
    assertEquals(object.get("type").asText(), "JSBookmark");
  }

  /*
  @Test public void canRefresh() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JOB-1206\",\"action\":\"ACTION-8535\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = bookmarkRepo.refresh("container-ref");
    assertEquals(response.get("action").asText(), "ACTION-8535");
  }
  */
}
