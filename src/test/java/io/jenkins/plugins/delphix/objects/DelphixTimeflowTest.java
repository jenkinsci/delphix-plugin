package io.jenkins.plugins.delphix.objects;

import io.jenkins.plugins.delphix.objects.DelphixTimeflow;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DelphixTimeflowTest {
  private DelphixTimeflow delphixTimeflow = new DelphixTimeflow(
    "reference",
    "name",
    "timestamp",
    "container"
  );

  @Test public void canGetName() {
    assertEquals(delphixTimeflow.getName(), "name");
  }

  @Test public void canGetReference() {
    assertEquals(delphixTimeflow.getReference(), "reference");
  }

  @Test public void canGetTimestamp() {
    assertEquals(delphixTimeflow.getTimestamp(), "timestamp");
  }

  @Test public void canGetContainer() {
    assertEquals(delphixTimeflow.getContainer(), "container");
  }

}
