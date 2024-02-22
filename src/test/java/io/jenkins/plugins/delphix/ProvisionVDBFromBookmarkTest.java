// package io.jenkins.plugins.delphix;


// import com.cloudbees.plugins.credentials.CredentialsProvider;
// import com.cloudbees.plugins.credentials.CredentialsScope;
// import com.cloudbees.plugins.credentials.domains.Domain;
// import hudson.model.FreeStyleBuild;
// import hudson.model.FreeStyleProject;
// import hudson.util.Secret;
// import jenkins.model.GlobalConfiguration;
// import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
// import org.junit.*;
// import org.jvnet.hudson.test.JenkinsRule;

// public class ProvisionVDBFromBookmarkTest {

//   @Rule
//   public JenkinsRule jenkins = new JenkinsRule();

//   @Before
//   public void init() throws Exception {
//     DelphixGlobalConfiguration globalConfig1 =
//         GlobalConfiguration.all().get(DelphixGlobalConfiguration.class);
//     globalConfig1.setDctUrl("HOST");
//     globalConfig1.setSslCheck(true);


//     globalConfig1.save();

//     StringCredentialsImpl c =
//         new StringCredentialsImpl(CredentialsScope.USER, "test123", "description",
//             Secret.fromString(
//                 "KEY"));
//     CredentialsProvider.lookupStores(jenkins).iterator().next().addCredentials(Domain.global(), c);
//   }

//   @Test
//   public void testProvisionWithPolling() throws Exception {
//     FreeStyleProject project = jenkins.createFreeStyleProject();
//     ProvisionVDBFromBookmark builder =
//         new ProvisionVDBFromBookmark("5057a92229644028bee9206f4f32965c");
//     //    builder.setSourceDataId("4-ORACLE_DB_CONTAINER-6");
//     builder.setCredentialId("test123");
//     builder.setAutoSelectRepository(true);
//     builder.setName("vdb_test_1");
//     String jsonString =
//         "{\"appdata_source_params\": {\"mountLocation\": \"/dlpx_mnt\", \"postgresPort\": 5435}}";
//     builder.setJsonParam(jsonString);
//     project.getBuildersList().add(builder);
//     FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
//     jenkins.assertLogContains("Current Job Status: COMPLETED", build);
//   }

//   @After
//   public void destroy() throws Exception {
//     FreeStyleProject project = jenkins.createFreeStyleProject();
//     DeleteVDB builder1 = new DeleteVDB();
//     builder1.setName("vdb_test_1");
//     builder1.setCredentialId("test123");
//     project.getBuildersList().add(builder1);
//     FreeStyleBuild build1 = jenkins.buildAndAssertSuccess(project);
//     jenkins.assertLogContains("Current Job Status: COMPLETED", build1);

//   }
// }