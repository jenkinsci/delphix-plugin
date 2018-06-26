package io.jenkins.plugins.delphix;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SelfServiceRequestTest {

  @Test public void canGetDefault(){
    SelfServiceRequest request = new SelfServiceRequest("default", false, "");
    String response = request.toJson();

    assertEquals("{\"type\":\"default\",}", response);
  }
}
