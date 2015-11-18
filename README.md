<img src="https://cloud.githubusercontent.com/assets/4017543/10583682/371a1daa-7643-11e5-9956-4e4a0e7d2036.png" width="300">
#Delphix Jenkins Plugin

Connect Jenkins jobs to the Delphix Engine

##Features
- Support for multiple engines
<br><img src="https://cloud.githubusercontent.com/assets/4017543/10584240/f1b55362-7645-11e5-9914-2bae20c3e0fc.png" width="700">
- Refresh Virtual Database build step
<br>Can refresh all in group or individual virtual database
<br>Ability to retry
<br><img src="https://cloud.githubusercontent.com/assets/4017543/10860086/149121c0-7f21-11e5-829c-1f525a01130b.png" width="700">
- Sync source build step
<br>Can sync all in group or individual source
<br>Ability to retry
<br><img src="https://cloud.githubusercontent.com/assets/4017543/10860087/1491f4d8-7f21-11e5-9560-479d80589281.png" width="700">
- Provision Virtual Database build step
<br>Can choose name of new virtual database
<br><img src="https://cloud.githubusercontent.com/assets/4017543/10860085/148d043c-7f21-11e5-8c3f-7be0f67f1cbc.png" width="700">
- Delete dSource or Virtual Database build step
<br>Ability to retry
<br><img src="https://cloud.githubusercontent.com/assets/4017543/10860207/a6cd251a-7f22-11e5-80e0-0293b575a92a.png" width="700">
- Refresh environment build step
<br><img src="https://cloud.githubusercontent.com/assets/4017543/10860084/148ceee8-7f21-11e5-99cd-c0be60c031a3.png" width="700">
- Add single host unix environment build step
<br><img src="https://cloud.githubusercontent.com/assets/4017543/11255033/e43007b2-8df8-11e5-9aac-87ef6abf88b6.png" width="700">
- Delete environment build step
<br><img src="https://cloud.githubusercontent.com/assets/4017543/11255038/e8621ffa-8df8-11e5-8a5f-b0858e9909e7.png" width="700">
- Hook operations refresh virtual database and sync source build steps
<br> Pre and post hooks supported.
<br> Support for multiple hooks and ordering of hook execution.
<br> Hook scripts loaded from local filesystem and set on Delphix Engine before running operation.  So you can store your hooks in source control.  Have them check out as part of your job and have them set on the engine whenever your job runs automatically.  Just push changes to your hooks to source control to update your hooks for your next build run.
<br><img src="https://cloud.githubusercontent.com/assets/4017543/11255172/9c6d4498-8df9-11e5-9604-b535ed5e2b73.png" width="700">

- Job logging
<br><img src="https://cloud.githubusercontent.com/assets/4017543/10860247/eda87b38-7f22-11e5-87d8-f8dd85c32ec2.png" width="700">
- Job cancellation

##Build and Install
To build
```
gradle jpi
```
Copy the resulting delphix.hpi into $JENKINS_HOME/plugins

##Start test Jenkins server with plugin installed
```
gradle server
```

##Tests
```
gradle check
```
Will run checkstyle, findbugs, and all of the integration tests and report any errors.  The integration tests require two Delphix Engines with VDBs available, and the specifications are in TestConsts.java.  The code coverage results for testing will be stored in build/reports/jacoco.

##Contributing
Contributions are welcome. There are guides online for developing Jenkins plugins.  Feel free to contact the developer.

##Authors
Peter Vilim - peter.vilim@delphix.com
