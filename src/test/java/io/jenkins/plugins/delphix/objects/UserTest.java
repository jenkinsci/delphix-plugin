package io.jenkins.plugins.delphix.objects;

import java.io.IOException;
import io.jenkins.plugins.delphix.objects.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;

public class UserTest {
  private User user = new User(
    "type",
    "reference",
    "namespace",
    "name",
    "userType",
    "emailAddress",
    true,
    "firstName",
    "lastName",
    "passwordUpdateRequested",
    true,
    "mobilePhoneNumber",
    "workPhoneNumber",
    "homePhoneNumber",
    "authenticationType",
    "principal",
    "credential",
    "publicKey",
    1000,
    "locale"
  );

  @Test public void canGetType() {
    assertEquals(user.getType(), "type");
  }

  @Test public void canGetReference() {
    assertEquals(user.getReference(), "reference");
  }

  @Test public void canGetNamespace() {
    assertEquals(user.getNamespace(), "namespace");
  }

  @Test public void canGetName() {
    assertEquals(user.getName(), "name");
  }

  @Test public void canGetUserType() {
    assertEquals(user.getUserType(), "userType");
  }

  @Test public void canGetEmailAddress() {
    assertEquals(user.getEmailAddress(), "emailAddress");
  }

  @Test public void canGetEnabled() {
    assertTrue(user.getEnabled());
  }

  @Test public void canGetFirstName() {
    assertEquals(user.getFirstName(), "firstName");
  }

  @Test public void canGetLastName() {
    assertEquals(user.getLastName(), "lastName");
  }

  @Test public void canGetPasswordUpdateRequest() {
    assertEquals(user.getPasswordUpdateRequest(), "passwordUpdateRequested");
  }

  @Test public void canGetIsDefault() {
    assertTrue(user.getIsDefault());
  }

  @Test public void canGetMobilePhoneNumber() {
    assertEquals(user.getMobilePhoneNumber(), "mobilePhoneNumber");
  }

  @Test public void canGetWorkPhoneNumber() {
    assertEquals(user.getWorkPhoneNumber(), "workPhoneNumber");
  }

  @Test public void canGetHomePhoneNumber() {
    assertEquals(user.getHomePhoneNumber(), "homePhoneNumber");
  }

  @Test public void canGetAuthenticationType() {
    assertEquals(user.getAuthenticationType(), "authenticationType");
  }

  @Test public void canGetPrincipal() {
    assertEquals(user.getPrincipal(), "principal");
  }

  @Test public void canGetCredential() {
    assertEquals(user.getCredential(), "credential");
  }

  @Test public void canGetPublicKey() {
    assertEquals(user.getPublicKey(), "publicKey");
  }

  @Test public void canGetSessionTimeout() {
    assertEquals((Integer)1000, user.getSessionTimeout());
  }

  @Test public void canGetLocale() {
    assertEquals(user.getLocale(), "locale");
  }

  @Test public void canLoadFromJSON() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String result = "{\"type\":\"User\",\"reference\":\"USER-2\",\"namespace\":null,\"name\":\"name\",\"userType\":\"DOMAIN\",\"emailAddress\":\"emailAddress\",\"enabled\":true,\"firstName\":null,\"lastName\":null,\"isDefault\":true,\"mobilePhoneNumber\":null,\"workPhoneNumber\":null,\"homePhoneNumber\":null,\"authenticationType\":\"NATIVE\",\"principal\":\"principal\",\"credential\":null,\"publicKey\":null,\"sessionTimeout\":30,\"locale\":\"en-US\",\"passwordUpdateRequested\":false}";
    JsonNode json = mapper.readTree(result);
    User user = User.fromJson(json);
    assertThat(user, instanceOf(User.class));
  }
}
