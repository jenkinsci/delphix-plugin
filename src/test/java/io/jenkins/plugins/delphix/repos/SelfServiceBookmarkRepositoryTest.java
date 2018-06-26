package io.jenkins.plugins.delphix.repos;

import io.jenkins.plugins.delphix.objects.SelfServiceBookmark;
import java.util.LinkedHashMap;
import java.io.IOException;
import io.jenkins.plugins.delphix.DelphixEngine;
import io.jenkins.plugins.delphix.DelphixEngineException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertEquals;
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

  @Test public void canListBookmarks() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"ListResult\",\"status\":\"OK\",\"result\":[{\"type\":\"JSBookmark\",\"reference\":\"JS_BOOKMARK-1\",\"namespace\":null,\"name\":\"Initial Bookmark\",\"branch\":\"JS_BRANCH-1\",\"timestamp\":\"2018-04-30T14:31:18.801Z\",\"description\":null,\"tags\":[],\"shared\":true,\"container\":null,\"template\":\"JS_DATA_TEMPLATE-1\",\"containerName\":null,\"templateName\":\"CTO-SDLC\",\"usable\":true,\"checkoutCount\":10,\"bookmarkType\":\"DATA_TEMPLATE\",\"expiration\":null,\"creationTime\":\"2018-04-30T14:31:18.477Z\"}],\"job\":null,\"action\":null,\"total\":19,\"overflow\":false}";
    when(delphixEngine.engineGet(anyString())).thenReturn(formatResult(result));

    LinkedHashMap<String, SelfServiceBookmark> bookmarks = bookmarkRepo.listBookmarks();
    SelfServiceBookmark bookmark = bookmarks.get("JS_BOOKMARK-1");
    assertEquals(bookmark.getName(), "Initial Bookmark");
  }

  @Test public void canCreate() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JS_BOOKMARK-3\",\"action\":\"ACTION-138\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = bookmarkRepo.create("name", "branch", "sourceDataLayout");
    assertEquals(response.get("action").asText(), "ACTION-138");
  }

  @Test public void canDelete() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JS_BOOKMARK-3\",\"action\":\"ACTION-138\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = bookmarkRepo.delete("name");
    assertEquals(response.get("action").asText(), "ACTION-138");
  }

  @Test public void canShare() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JS_BOOKMARK-3\",\"action\":\"ACTION-138\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = bookmarkRepo.share("name");
    assertEquals(response.get("action").asText(), "ACTION-138");
  }

  @Test public void canUnshare() throws IOException, DelphixEngineException {
    String result = "{\"type\":\"OKResult\",\"status\":\"OK\",\"result\":\"\",\"job\":\"JS_BOOKMARK-3\",\"action\":\"ACTION-138\"}";
    when(delphixEngine.enginePost(anyString(), anyString())).thenReturn(formatResult(result));

    JsonNode response = bookmarkRepo.unshare("name");
    assertEquals(response.get("action").asText(), "ACTION-138");
  }
}
