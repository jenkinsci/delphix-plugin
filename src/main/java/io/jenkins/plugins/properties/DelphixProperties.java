package io.jenkins.plugins.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.constant.Constant;

public class DelphixProperties {
    private File file;
    private Properties properties;
    private TaskListener listener;

    /**
     * Constructor of Delphix Properties.
     *
     * @param workspace FilePath
     * @param listener TaskListener
     */
    public DelphixProperties(FilePath workspace, String fileName, TaskListener listener) {
        this.properties = new Properties();
        this.file = new File(workspace + "/" + fileName);
        this.listener = listener;

    }

    private void loadProperties() {
        try (FileInputStream fileInput = new FileInputStream(this.file)) {
            this.properties.load(fileInput);
        }
        catch (IOException e) {
            this.listener.getLogger().print(e.getMessage());
        }
    }

    private String read(String key) {
        this.loadProperties();
        return this.properties.getProperty(key).replaceAll("^\"|\"$", "");
    }

    private void writeMap(Map<String, Object> vdbDetails) {
        try {
            // boolean fileCreated = this.file.createNewFile();
            if (this.file.createNewFile())
                this.listener.getLogger().print("Properties file created");
            else
                this.listener.getLogger().print("Properties file already exists");
        }
        catch (IOException e) {
            this.listener.getLogger().print(e.getMessage());
        }
        try (FileOutputStream fileOut = new FileOutputStream(this.file, false);) {
            for (Map.Entry<String, Object> entry : vdbDetails.entrySet()) {
                this.properties.put(entry.getKey(), entry.getValue().toString());
            }
            this.properties.store(fileOut, "Delphix Properties");
        }
        catch (IOException e) {
            this.listener.getLogger().print(e.getMessage());
        }
    }

    public void setVDBDetails(Map<String, Object> vdbDetails) {
        this.writeMap(vdbDetails);
    }

    public String getVDB() {
        return this.read(Constant.ID);
    }
}
