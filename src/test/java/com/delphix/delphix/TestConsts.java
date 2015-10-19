/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
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
    public static final String oracleSource = "ORACLE_DB_CONTAINER-1";
    public static final String oracleVDB1 = "ORACLE_DB_CONTAINER-3";
    public static final String oracleVDB2 = "ORACLE_DB_CONTAINER-5";

    // MSSQL test objects
    public static final String mssqlSource = "MSSQL_DB_CONTAINER-1";
    public static final String mssqlVDB = "MSSQL_DB_CONTAINER-5";
}
