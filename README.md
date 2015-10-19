<img src="https://cloud.githubusercontent.com/assets/4017543/10583682/371a1daa-7643-11e5-9956-4e4a0e7d2036.png" width="300">
#Delphix Jenkins Plugin

Connect Jenkins jobs to the Delphix Engine

##Features
- Support for multiple engines
<br><img src="https://cloud.githubusercontent.com/assets/4017543/10584240/f1b55362-7645-11e5-9914-2bae20c3e0fc.png" width="700">
- Refresh VDB build step
<br><img src="https://cloud.githubusercontent.com/assets/4017543/10584243/f5225748-7645-11e5-9d06-4494192b1fff.png" width="700">
- Sync source build step
<br><img src="https://cloud.githubusercontent.com/assets/4017543/10584245/f7e856a8-7645-11e5-827a-724c2db67c83.png" width="700">
- Job logging
<br><img src="https://cloud.githubusercontent.com/assets/4017543/10584318/68d71c82-7646-11e5-80ee-f79622efc723.png" width="700">
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
