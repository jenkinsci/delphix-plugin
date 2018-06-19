package io.jenkins.plugins.delphix.repos;

import java.io.IOException;
import io.jenkins.plugins.delphix.objects.Action;
import io.jenkins.plugins.delphix.DelphixEngine;
import io.jenkins.plugins.delphix.DelphixEngineException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ActionRepositoryTest {

  @Test public void canGet() throws IOException, DelphixEngineException {
    DelphixEngine delphixEngine = mock(DelphixEngine.class);
    ObjectMapper mapper = new ObjectMapper();
    String result = "{\"type\":\"type\",\"reference\":\"reference\",\"namespace\":null,\"title\":\"title\",\"details\":\"details\",\"startTime\":\"2018-06-19T15:53:40.858Z\",\"endTime\":null,\"user\":\"USER-2\",\"userAgent\":\"userAgent\",\"parentAction\":null,\"actionType\":\"actionType\",\"state\":\"WAITING\",\"workSource\":\"workSource\",\"workSourceName\":\"workSourceName\",\"report\":null,\"failureDescription\":null,\"failureAction\":null,\"failureMessageCode\":null},\"job\":null,\"action\":null}";
    JsonNode json = mapper.readTree(result);
    when(delphixEngine.engineGet(anyString())).thenReturn(json);

    ActionRepository actionRepo = new ActionRepository(delphixEngine);
    Action action = actionRepo.get("ACTION-8468");
    assertThat(action, instanceOf(Action.class));
  }

}
