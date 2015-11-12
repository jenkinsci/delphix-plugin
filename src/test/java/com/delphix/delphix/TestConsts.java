/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.delphix.delphix;

/**
 * Constants that are used across test classes.
 */
public class TestConsts {

    // Engines and login details for test resources
    public static final String oracleEngine = "jenkinspluginoracle.dc1.delphix.com";
    public static final String oracleUser = "delphix_admin";
    public static final String oraclePassword = "delphix";
    public static final String mssqlEngine = "jenkinspluginmssql.dcenter.delphix.com";
    public static final String mssqlUser = "delphix_admin";
    public static final String mssqlPassword = "delphix";

    // Oracle test objects
    public static final String oracleGroup = "GROUP-1";
    public static final String oracleSource = "ORACLE_DB_CONTAINER-1";
    public static final String oracleVDB1 = "ORACLE_DB_CONTAINER-2";
    public static final String oracleVDB2 = "ORACLE_DB_CONTAINER-3";
    public static final String oracleEnvironment = "UNIX-HOST_ENVIRONMENT-1";

    // MSSQL test objects
    public static final String mssqlGroup = "GROUP-1";
    public static final String mssqlSource = "MSSQL_DB_CONTAINER-1";
    public static final String mssqlVDB = "MSSQL_DB_CONTAINER-3";
    public static final String mssqlEnvironment = "WINDOWS-HOST_ENVIRONMENT-1";
}
