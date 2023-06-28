# Delphix Plugin

[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/delphix-plugin/master)](https://ci.jenkins.io/job/Plugins/job/delphix-plugin/)
[![Jenkins Plugins](https://img.shields.io/jenkins/plugin/v/delphix.svg)](https://plugins.jenkins.io/delphix)
[![License](https://img.shields.io/github/license/jenkinsci/delphix-plugin.svg)](LICENSE)

The Delphix plugin allows Jenkins to connect to Data Control Tower (DCT) and execute operations.

#### Table of Contents
1.  [Description](#description)
2.  [Installation](#installation)
3.  [Delphix Engine Requirements](#requirements)
4.  [Usage](#usage)
5.  [Links](#links)
6.  [Contribute](#contribute)
    *   [Code of conduct](#code-of-conduct)
    *   [Community Guidelines](#community-guidelines)
    *   [Contributor Agreement](#contributor-agreement)
7.  [Reporting Issues](#reporting-issues)
8.  [Statement of Support](#statement-of-support)
9.  [License](#license)

## <a id="description"></a>Description

This plugin is designed to automate routine and/or trigger jobs with DCT.

## <a id="installation"></a>Installation

Install through Jenkins Plugin Manager or download [here](https://plugins.jenkins.io/delphix).

## <a id="requirements"></a>Delphix Engine Requirements

Delphix Engine v6.0.14.2+ or higher.

## <a id="usage"></a>Usage

#### Global Configuration

After the plugin has been installed, DCT will need to be connected. Go to System Configuration page, scroll down to the Delphix section. Enter your DCT URL.

#### Credentials

The API KEY has to be saved on Jenkins’s side as a Secret text. In Jenkins, go to Credentials > Global > Add Credentials, select the Secret text type, and fill in the inputs.
Secret must contains the API KEY generated from DCT.ID input is the ID that you want for the credential, the value will be used in the Delphix Plugin to perfomr operations.

#### Available Operations

*   Provision VDB by Snapshot
*   Provision VDB by Bookmark
*   Delete VDB

#### Advanced Settings

It is possible to share assets created between build steps. The provision operation has its output saved in a properties file.

## <a id="links"></a>Links

*   [Delphix Plugin Jenkins Wiki Page](https://wiki.jenkins.io/display/JENKINS/Delphix+Plugin)
*   [Jenkins Plugin Delphix Page](https://plugins.jenkins.io/delphix)

## <a id="contribute"></a>Contribute

1.  Fork the project.
2.  Make your bug fix or new feature.
3.  Add tests for your code.
4.  Send a pull request.

Contributions must be signed as `User Name <user@email.com>`. Make sure to [set up Git with user name and email address](https://git-scm.com/book/en/v2/Getting-Started-First-Time-Git-Setup). Bug fixes should branch from the current stable branch. New features should be based on the `master` branch.

#### <a id="code-of-conduct"></a>Code of Conduct

This project operates under the [Delphix Code of Conduct](https://delphix.github.io/code-of-conduct.html). By participating in this project you agree to abide by its terms.

#### <a id="contributor-agreement"></a>Contributor Agreement

All contributors are required to sign the Delphix Contributor agreement prior to contributing code to an open source repository. This process is handled automatically by [cla-assistant](https://cla-assistant.io/). Simply open a pull request and a bot will automatically check to see if you have signed the latest agreement. If not, you will be prompted to do so as part of the pull request process. Read the full [Delphix Contributor License Agreement](https://delphix.github.io/contributor-agreement.html).

## <a id="reporting_issues"></a>Reporting Issues


Please submit bug reports, suggestions and pull requests to the [GitHub issue tracker](https://github.com/jenkinsci/delphix-plugin/issues).

## <a id="statement-of-support"></a>Statement of Support

This software is provided as-is, without warranty of any kind or commercial support through Delphix. See the associated license for additional details. Questions, issues, feature requests, and contributions should be directed to the community as outlined in the [Delphix Community Guidelines](https://delphix.github.io/community-guidelines.html).

## <a id="license"></a>License

This is code is licensed under the Apache License 2.0. Full license is available [here](./LICENSE).
