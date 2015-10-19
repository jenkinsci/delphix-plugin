<img src="https://cloud.githubusercontent.com/assets/4017543/10583682/371a1daa-7643-11e5-9956-4e4a0e7d2036.png" width="300">
#Delphix Jenkins Plugin

Connect Jenkins jobs to the Delphix Engine

##Features
- Refresh VDB build step
- Sync source build step
- Support for multiple engines
- Job logging
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
