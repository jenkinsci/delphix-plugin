package io.jenkins.plugins.util;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.delphix.dct.ApiException;
import com.delphix.dct.models.VDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hudson.FilePath;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.delphix.Messages;

public class Helper {

    private PrintStream logger;

    public Helper(PrintStream logger) {
        this.logger = logger;
    }

    public List<String> getFileList(Path rootDir, String pattern) throws IOException {
        List<String> matchesList = new ArrayList<String>();
        FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attribs)
                    throws IOException {
                FileSystem fs = FileSystems.getDefault();
                PathMatcher matcher = fs.getPathMatcher(pattern);
                Path name = file.getFileName();
                if (matcher.matches(name)) {
                    matchesList.add(name.toString());
                }
                return FileVisitResult.CONTINUE;
            }
        };
        Files.walkFileTree(rootDir, matcherVisitor);
        return matchesList;
    }

    public Map<String, Object> convertObjectToMapUsingGson(VDB vdb) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(vdb),
                new TypeToken<HashMap<String, Object>>() {}.getType());
    }

    public VDB displayVDBDetails(DctSdkUtil dctSdkUtil, String vdbId) throws ApiException {
        VDB vdbDetails = dctSdkUtil.getVDBDetails(vdbId);
        this.logger.println(Messages.Vdb_Id(vdbDetails.getId()));
        this.logger.println(Messages.Vdb_Name(vdbDetails.getName()));
        this.logger.println(Messages.Vdb_DatabaseType(vdbDetails.getDatabaseType()));
        this.logger.println(Messages.Vdb_DatabaseVersion(vdbDetails.getDatabaseVersion()));
        this.logger.println(Messages.Vdb_IpAdress(vdbDetails.getIpAddress()));
        this.logger.println(Messages.Vdb_Status(vdbDetails.getStatus()));
        return vdbDetails;
    }

    public void saveToProperties(VDB vdbDetails, FilePath workspace, TaskListener listener,
            String fileNameSuffix) {
        String fileName = fileNameSuffix != null
                ? Constant.UNIQUE_FILE_NAME + fileNameSuffix + Constant.PROPERTIES
                : Constant.FILE_NAME + Constant.PROPERTIES;
        this.logger.println(Messages.ProvisionVDB_Save(fileName));
        DelphixProperties delphixProps = new DelphixProperties(workspace, fileName, listener);
        delphixProps.setVDBDetails(convertObjectToMapUsingGson(vdbDetails));
    }

    public Boolean waitForPolling(DctSdkUtil dctSdkUtil, Run<?, ?> run, String jobId)
            throws ApiException {
        this.logger.println(Messages.Poll_Wait());
        boolean fail = dctSdkUtil.waitForPolling(jobId);
        if (fail) {
            // logger.println(Messages.ProvisionVDB_Fail());
            run.setResult(Result.FAILURE);
        }
        else {
            // logger.println(Messages.ProvisionVDB_Complete());
        }
        return fail;
    }

    // public void pollingWithSave(DctSdkUtil dctSdkUtil, ApiClient defaultClient,
    // TaskListener listener, Run<?, ?> run, FilePath workspace, String jobId, String vdbId,
    // Boolean save, String fileNameSuffix) throws ApiException {
    // Boolean pollResult = waitForPolling(dctSdkUtil, defaultClient, listener, run, jobId);
    // if (!pollResult) {
    // VDB vdbDetails = displayVDBDetails(dctSdkUtil, defaultClient, vdbId, listener);
    // if (save) {
    // String fileName = fileNameSuffix != null
    // ? Constant.UNIQUE_FILE_NAME + fileNameSuffix + Constant.PROPERTIES
    // : Constant.FILE_NAME + Constant.PROPERTIES;
    // logger.println(Messages.ProvisionVDB_Save(fileName));
    // saveToProperties(vdbDetails, workspace, listener, fileName);
    // }
    // }
    // }
}
