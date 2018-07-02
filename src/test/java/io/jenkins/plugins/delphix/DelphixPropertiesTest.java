package io.jenkins.plugins.delphix;

import java.io.PrintStream;
import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.Functions;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.tasks.BatchFile;
import hudson.tasks.CommandInterpreter;
import hudson.tasks.Shell;
import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

public class DelphixPropertiesTest {

  @Rule public JenkinsRule rule = new JenkinsRule();
  TaskListener listener = mock(TaskListener.class);
  PrintStream printStream = mock(PrintStream.class);

  @Test public void canInstantiate() throws Exception {
    FreeStyleProject project = rule.createFreeStyleProject();
    FreeStyleBuild build = project.scheduleBuild2(0).get();
    File file = new File(build.getWorkspace() + "/delphix.properties");

    DelphixProperties props = new DelphixProperties(file, listener);
    assertThat(props, instanceOf(DelphixProperties.class));
  }

  @Test(expected = IOException.class)
  public void canNotLoadFile() throws IOException {
    File file = mock(File.class);
    when(file.createNewFile()).thenThrow(new IOException(""));
    when(listener.getLogger()).thenReturn(printStream);

    new DelphixProperties(file, listener);
  }

  @Test
  public void canWriteEngine() throws Exception {
    FreeStyleProject project = rule.createFreeStyleProject();
    FreeStyleBuild build = project.scheduleBuild2(0).get();
    File file = new File(build.getWorkspace() + "/delphix.properties");

    DelphixProperties props = new DelphixProperties(file, listener);
    props.setEngine("engine");
    assertEquals("engine", props.getEngine());
  }

  @Test
  public void canWriteBookmarkReference() throws Exception {
    FreeStyleProject project = rule.createFreeStyleProject();
    FreeStyleBuild build = project.scheduleBuild2(0).get();
    File file = new File(build.getWorkspace() + "/delphix.properties");

    DelphixProperties props = new DelphixProperties(file, listener);
    props.setBookmarkReference("engine");
    assertEquals("engine", props.getBookmarkReference());
  }

  @Test
  public void canWriteBookmarkOperation() throws Exception {
    FreeStyleProject project = rule.createFreeStyleProject();
    FreeStyleBuild build = project.scheduleBuild2(0).get();
    File file = new File(build.getWorkspace() + "/delphix.properties");

    DelphixProperties props = new DelphixProperties(file, listener);
    props.setBookmarkOperation("engine");
    assertEquals("engine", props.getBookmarkOperation());
  }

  @Test
  public void canWriteContainerReference() throws Exception {
    FreeStyleProject project = rule.createFreeStyleProject();
    FreeStyleBuild build = project.scheduleBuild2(0).get();
    File file = new File(build.getWorkspace() + "/delphix.properties");

    DelphixProperties props = new DelphixProperties(file, listener);
    props.setContainerReference("engine");
    assertEquals("engine", props.getContainerReference());
  }

  @Test
  public void canWriteContainerOperation() throws Exception {
    FreeStyleProject project = rule.createFreeStyleProject();
    FreeStyleBuild build = project.scheduleBuild2(0).get();
    File file = new File(build.getWorkspace() + "/delphix.properties");

    DelphixProperties props = new DelphixProperties(file, listener);
    props.setContainerOperation("engine");
    assertEquals("engine", props.getContainerOperation());
  }
}
