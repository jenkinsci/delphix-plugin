package io.jenkins.plugins.delphix.repos;

import java.io.IOException;
import io.jenkins.plugins.delphix.objects.User;
import io.jenkins.plugins.delphix.DelphixEngine;
import io.jenkins.plugins.delphix.DelphixEngineException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

  DelphixEngine delphixEngine = mock(DelphixEngine.class);
  UserRepository userRepo = new UserRepository(delphixEngine);

  @Test public void canGet() throws IOException, DelphixEngineException {
    ObjectMapper mapper = new ObjectMapper();
    String result = "{\"result\":{\"type\":\"User\",\"reference\":\"USER-2\",\"namespace\":null,\"name\":\"name\",\"userType\":\"DOMAIN\",\"emailAddress\":\"emailAddress\",\"enabled\":true,\"firstName\":null,\"lastName\":null,\"isDefault\":true,\"mobilePhoneNumber\":null,\"workPhoneNumber\":null,\"homePhoneNumber\":null,\"authenticationType\":\"NATIVE\",\"principal\":\"principal\",\"credential\":null,\"publicKey\":null,\"sessionTimeout\":30,\"locale\":\"en-US\",\"passwordUpdateRequested\":false}}";
    JsonNode json = mapper.readTree(result);

    when(delphixEngine.engineGet(anyString())).thenReturn(json);

    User user = userRepo.getCurrent();
    assertThat(user, instanceOf(User.class));
  }

}
