package io.jenkins.plugins.delphix;

import java.io.PrintStream;
import hudson.model.TaskListener;
import hudson.model.Run;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DelphixBuilderTest {

    @Rule public JenkinsRule rule = new JenkinsRule();
    TaskListener listener = mock(TaskListener.class);
    PrintStream printStream = mock(PrintStream.class);
    DelphixEngine delphixEngine = mock(DelphixEngine.class);
    Run<?,?> runner = mock(Run.class);
    private class BuilderTest extends DelphixBuilder { }
    BuilderTest builder = new BuilderTest();

    private JsonNode formatResult(String result) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(result);
    }

    @Test public void canCheckActionIsNotFinished() throws IOException, DelphixEngineException {
      String result = "{\"result\":{\"type\":\"type\",\"reference\":\"reference\",\"namespace\":null,\"title\":\"title\",\"details\":\"details\",\"startTime\":\"2018-06-19T15:53:40.858Z\",\"endTime\":null,\"user\":\"USER-2\",\"userAgent\":\"userAgent\",\"parentAction\":null,\"actionType\":\"actionType\",\"state\":\"WAITING\",\"workSource\":\"workSource\",\"workSourceName\":\"workSourceName\",\"report\":null,\"failureDescription\":null,\"failureAction\":null,\"failureMessageCode\":null},\"job\":null,\"action\":null}}";
      when(delphixEngine.engineGet(anyString())).thenReturn(formatResult(result));

      BuilderTest builder = new BuilderTest();
      Boolean checkAction = builder.checkActionIsFinished(listener, delphixEngine, formatResult(result));
      assertFalse(checkAction);
    }

    @Test public void canCheckActionIsFinished() throws IOException, DelphixEngineException {
      String result = "{\"result\":{\"type\":\"type\",\"reference\":\"reference\",\"namespace\":null,\"title\":\"title\",\"details\":\"details\",\"startTime\":\"2018-06-19T15:53:40.858Z\",\"endTime\":null,\"user\":\"USER-2\",\"userAgent\":\"userAgent\",\"parentAction\":null,\"actionType\":\"actionType\",\"state\":\"COMPLETED\",\"workSource\":\"workSource\",\"workSourceName\":\"workSourceName\",\"report\":null,\"failureDescription\":null,\"failureAction\":null,\"failureMessageCode\":null},\"job\":null,\"action\":null}}";
      when(delphixEngine.engineGet(anyString())).thenReturn(formatResult(result));
      when(listener.getLogger()).thenReturn(printStream);

      Boolean checkAction = builder.checkActionIsFinished(listener, delphixEngine, formatResult(result));
      assertTrue(checkAction);
    }

    @Test public void canNotLogin() throws IOException, DelphixEngineException {
    doThrow(new IOException("")).when(delphixEngine).login();
    when(listener.getLogger()).thenReturn(printStream);
    builder.checkActionIsFinished(listener, delphixEngine, formatResult("{}"));
    }

    @Test public void canNotFindEngine() throws IOException, DelphixEngineException {
      doThrow(new DelphixEngineException("")).when(delphixEngine).login();
      when(listener.getLogger()).thenReturn(printStream);
      builder.checkActionIsFinished(listener, delphixEngine, formatResult("{}"));
    }

    @Test public void canCheckJobStatus() throws IOException, DelphixEngineException {
        String result1 = "{\"result\": {\"type\":\"Job\",\"reference\":\"JOB-1191\",\"namespace\":null,\"name\":null,\"actionType\":\"JETSTREAM_USER_CONTAINER_REFRESH\",\"target\":\"JS_DATA_CONTAINER-2\",\"targetObjectType\":\"JSDataContainer\",\"jobState\":\"RUNNING\",\"startTime\":\"2018-06-19T15:53:41.173Z\",\"updateTime\":\"2018-06-19T15:53:45.367Z\",\"suspendable\":false,\"cancelable\":true,\"queued\":false,\"user\":\"USER-2\",\"emailAddresses\":null,\"title\":\"Refresh Jet Stream data container.\",\"percentComplete\":0.0,\"targetName\":\"targetName\",\"parentActionState\":\"FAILED\",\"parentAction\":\"ACTION-8438\"}}";
        String result2 = "{\"result\": {\"type\":\"Job\",\"reference\":\"JOB-1191\",\"namespace\":null,\"name\":null,\"actionType\":\"JETSTREAM_USER_CONTAINER_REFRESH\",\"target\":\"JS_DATA_CONTAINER-2\",\"targetObjectType\":\"JSDataContainer\",\"jobState\":\"RUNNING\",\"startTime\":\"2018-06-19T15:53:41.173Z\",\"updateTime\":\"2018-06-19T15:53:45.367Z\",\"suspendable\":false,\"cancelable\":true,\"queued\":false,\"user\":\"USER-2\",\"emailAddresses\":null,\"title\":\"Refresh Jet Stream data container.\",\"percentComplete\":10.0,\"targetName\":\"targetName\",\"parentActionState\":\"FAILED\",\"parentAction\":\"ACTION-8438\"}}";
        String result3 = "{\"result\": {\"type\":\"Job\",\"reference\":\"JOB-1191\",\"namespace\":null,\"name\":null,\"actionType\":\"JETSTREAM_USER_CONTAINER_REFRESH\",\"target\":\"JS_DATA_CONTAINER-2\",\"targetObjectType\":\"JSDataContainer\",\"jobState\":\"COMPLETED\",\"startTime\":\"2018-06-19T15:53:41.173Z\",\"updateTime\":\"2018-06-19T15:53:45.367Z\",\"suspendable\":false,\"cancelable\":true,\"queued\":false,\"user\":\"USER-2\",\"emailAddresses\":null,\"title\":\"Refresh Jet Stream data container.\",\"percentComplete\":10.0,\"targetName\":\"targetName\",\"parentActionState\":\"FAILED\",\"parentAction\":\"ACTION-8438\"}}";
        doReturn(formatResult(result1)).doReturn(formatResult(result2)).doReturn(formatResult(result3)).when(delphixEngine).engineGet(anyString());
        when(listener.getLogger()).thenReturn(printStream);

        builder.checkJobStatus(runner, listener, delphixEngine, "job", "engine", "action");
    }

    @Test public void canNotLoginForJobStatus() throws IOException, DelphixEngineException {
        doThrow(new IOException("")).when(delphixEngine).login();
        when(listener.getLogger()).thenReturn(printStream);
        builder.checkJobStatus(runner, listener, delphixEngine, "job", "engine", "action");
    }

    @Test public void canNotFindEngineForJobStatus() throws IOException, DelphixEngineException {
        doThrow(new DelphixEngineException("")).when(delphixEngine).login();
        when(listener.getLogger()).thenReturn(printStream);
        builder.checkJobStatus(runner, listener, delphixEngine, "job", "engine", "action");
    }

}
