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

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * These tests cover the DelphixEngine class and test the various calls to our API.
 */
public class DelphixEngineTest {

    DelphixEngine oracleEngine;
    DelphixEngine mssqlEngine;

    /**
     * Setup an Oracle and MSSQL engine to use in tests.
     */
    @Before
    public void setup() {
        oracleEngine = new DelphixEngine(TestConsts.oracleEngine, TestConsts.oracleUser, TestConsts.oraclePassword);
        mssqlEngine = new DelphixEngine(TestConsts.mssqlEngine, TestConsts.mssqlUser, TestConsts.mssqlPassword);
    }

    /**
     * Test the constructor of the class.
     */
    @Test
    public void constructorTest() {
        Assert.assertEquals(oracleEngine.getEngineAddress(), TestConsts.oracleEngine);
        Assert.assertEquals(oracleEngine.getEngineUsername(), TestConsts.oracleUser);
        Assert.assertEquals(oracleEngine.getEnginePassword(), TestConsts.oraclePassword);
        DelphixEngine engine1copy = new DelphixEngine(oracleEngine);
        Assert.assertEquals(engine1copy.getEngineAddress(), TestConsts.oracleEngine);
        Assert.assertEquals(engine1copy.getEngineUsername(), TestConsts.oracleUser);
        Assert.assertEquals(engine1copy.getEnginePassword(), TestConsts.oraclePassword);
    }

    /**
     * Test login to the engine which is successful if no exceptions are thrown.
     */
    @Test
    public void engineLoginSuccess() throws IOException, DelphixEngineException {
        oracleEngine.login();
    }

    /**
     * Test a bad login to the engine which will throw an expected exception.
     */
    @Test(expected = DelphixEngineException.class)
    public void engineLoginFailure() throws IOException, DelphixEngineException {
        DelphixEngine engine = new DelphixEngine(TestConsts.oracleEngine, TestConsts.oracleUser, "badpassword");
        engine.login();
    }

    /**
     * Test trying to login to an engine that doesn't exist which will throw an expected exception.
     */
    @Test(expected = IOException.class)
    public void engineBadAddress() throws IOException, DelphixEngineException {
        DelphixEngine engine = new DelphixEngine("badaddress", TestConsts.oracleUser, TestConsts.oraclePassword);
        engine.login();
    }

    /**
     * List containers on the engine and check that the right size list is returned.
     */
    @Test
    public void listContainersTest() throws ClientProtocolException, IOException, DelphixEngineException {
        oracleEngine.login();
        ArrayList<DelphixContainer> containers =
                new ArrayList<DelphixContainer>(oracleEngine.listContainers().values());
        Assert.assertEquals(3, containers.size());
    }

    /**
     * Test refreshing an Oracle VDB and make sure the job completes.
     */
    @Test
    public void refreshOracleVDBTest() throws IOException, DelphixEngineException, InterruptedException {
        oracleEngine.login();
        String job = oracleEngine.refreshContainer(TestConsts.oracleVDB1);
        while (oracleEngine.getJobStatus(job).getStatus().equals(JobStatus.StatusEnum.RUNNING)) {
            Thread.sleep(1000);
        }
        Assert.assertTrue(oracleEngine.getJobStatus(job).getStatus().equals(JobStatus.StatusEnum.COMPLETED));
    }

    /**
     * Test refreshing a MSSQL VDB and make sure the job completes.
     */
    @Test
    public void refreshMssqlVDBTest() throws IOException, DelphixEngineException, InterruptedException {
        mssqlEngine.login();
        String job = mssqlEngine.refreshContainer(TestConsts.mssqlVDB);
        while (mssqlEngine.getJobStatus(job).getStatus().equals(JobStatus.StatusEnum.RUNNING)) {
            Thread.sleep(1000);
        }
        Assert.assertTrue(mssqlEngine.getJobStatus(job).getStatus().equals(JobStatus.StatusEnum.COMPLETED));
    }

    /**
     * Try to cancel a running refresh job and make sure that the job is cancelled.
     */
    @Test
    public void cancelJobTest() throws IOException, DelphixEngineException, InterruptedException {
        oracleEngine.login();
        String job = oracleEngine.refreshContainer(TestConsts.oracleVDB2);
        Thread.sleep(2000);
        Assert.assertTrue(oracleEngine.getJobStatus(job).getStatus().equals(JobStatus.StatusEnum.RUNNING));
        oracleEngine.cancelJob(job);
        while (oracleEngine.getJobStatus(job).getStatus().equals(JobStatus.StatusEnum.RUNNING)) {
            Thread.sleep(1000);
        }
        Assert.assertTrue(oracleEngine.getJobStatus(job).getStatus().equals(JobStatus.StatusEnum.CANCELED));
    }

    /**
     * Try to sync an Oracle dSource and make sure the job completes.
     */
    @Test
    public void syncOracleSourceTest() throws IOException, DelphixEngineException, InterruptedException {
        oracleEngine.login();
        String job = oracleEngine.sync(TestConsts.oracleSource);
        while (oracleEngine.getJobStatus(job).getStatus().equals(JobStatus.StatusEnum.RUNNING)) {
            Thread.sleep(1000);
        }
        Assert.assertTrue(oracleEngine.getJobStatus(job).getStatus().equals(JobStatus.StatusEnum.COMPLETED));
    }

    /**
     * Try to sync a MSSQL dSource and make sure the job completes.
     */
    @Test
    public void syncMssqlSourceTest() throws IOException, DelphixEngineException, InterruptedException {
        mssqlEngine.login();
        String job = mssqlEngine.sync(TestConsts.mssqlSource);
        while (mssqlEngine.getJobStatus(job).getStatus().equals(JobStatus.StatusEnum.RUNNING)) {
            Thread.sleep(1000);
        }
        Assert.assertTrue(mssqlEngine.getJobStatus(job).getStatus().equals(JobStatus.StatusEnum.COMPLETED));
    }

    /**
     * Try to get the job status of a job that doesn't exist and expect the appropriate exception.
     */
    @Test(expected = DelphixEngineException.class)
    public void getUnknownJob() throws ClientProtocolException, IOException, DelphixEngineException {
        oracleEngine.login();
        oracleEngine.getJobStatus("badjob");
    }
}
