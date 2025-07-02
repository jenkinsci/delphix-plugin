package io.jenkins.plugins.util;

import java.io.IOException;
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
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.constant.Constant;
import io.jenkins.plugins.delphix.Messages;
import io.jenkins.plugins.properties.DelphixProperties;

public class Helper {

    private TaskListener listener;

    public Helper(TaskListener listener) {
        this.listener = listener;
    }

    public List<String> getFileList(Path rootDir, String pattern) throws IOException {
        List<String> matchesList = new ArrayList<>();
        FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<>() {
            @NonNull
            @Override
            public FileVisitResult visitFile(Path file, @NonNull BasicFileAttributes attribs)
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

    public VDB displayVDBDetails(DctSdkUtil dctSdkUtil, String vdbId)
            throws ApiException, Exception {
        VDB vdbDetails = dctSdkUtil.getVDBDetails(vdbId);
        if (vdbDetails == null) {
            throw new Exception("VDB " + vdbId + " Not Found");
        }
        else {
            this.listener.getLogger().println(Messages.Vdb_Id(vdbDetails.getId()));
            this.listener.getLogger().println(Messages.Vdb_Name(vdbDetails.getName()));
            this.listener.getLogger()
                    .println(Messages.Vdb_DatabaseType(vdbDetails.getDatabaseType()));
            this.listener.getLogger()
                    .println(Messages.Vdb_DatabaseVersion(vdbDetails.getDatabaseVersion()));
            this.listener.getLogger().println(Messages.Vdb_IpAdress(vdbDetails.getIpAddress()));
            this.listener.getLogger().println(Messages.Vdb_Status(vdbDetails.getStatus()));
        }
        return vdbDetails;
    }

    public void saveToProperties(VDB vdbDetails, FilePath workspace, TaskListener listener,
            String fileNameSuffix) {
        try {
            String fileName = fileNameSuffix != null
                    ? Constant.UNIQUE_FILE_NAME + fileNameSuffix + Constant.PROPERTIES
                    : Constant.FILE_NAME + Constant.PROPERTIES;
            this.listener.getLogger().println(Messages.ProvisionVDB_Save(fileName));
            DelphixProperties delphixProps = new DelphixProperties(workspace, fileName, listener);
            delphixProps.setVDBDetails(convertObjectToMapUsingGson(vdbDetails));
        }
        catch (Exception e) {
            this.listener.getLogger().println("Error writing results to properties file");
            throw e;
        }
    }

    public void displayAndSave(DctSdkUtil dctSdkUtil, String vdbId, FilePath workspace,
            TaskListener listener, String fileNameSuffix) throws ApiException, Exception {
        VDB vdbDetails = displayVDBDetails(dctSdkUtil, vdbId);
        if (vdbDetails != null)
            saveToProperties(vdbDetails, workspace, listener, fileNameSuffix);
    }
}
