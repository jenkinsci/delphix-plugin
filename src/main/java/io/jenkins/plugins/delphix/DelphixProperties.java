/**
 * Copyright (c) 2018 by Delphix. All rights reserved.
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

package io.jenkins.plugins.delphix;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.File;
import java.util.Properties;

public class DelphixProperties {
    private final File file;
    private final Properties properties;
    private final TaskListener listener;
    private final String engineRef = "engine";
    private final String bookmarkOperation = "bookmark.operation";
    private final String containerOperation = "container.operation";
    private final String bookmarkRef = "bookmark.reference";
    private final String containerRef = "container.reference";

    public DelphixProperties(FilePath workspace, TaskListener listener) {
        this.properties = new Properties();
        this.file = new File(workspace + "/delphix.properties");
        this.listener = listener;
        try {
            this.file.createNewFile();
        } catch (IOException e) {
            this.listener.getLogger().print(e.getMessage());
        }
        this.loadProperties();
    }

    private void loadProperties() {
        try {
            FileInputStream fileInput = new FileInputStream(this.file);
            this.properties.load(fileInput);
            fileInput.close();
        } catch (FileNotFoundException e) {
            this.listener.getLogger().print(e.getMessage());
        } catch (IOException e) {
            this.listener.getLogger().print(e.getMessage());
        }
    }

    private String read(String key) {
        return this.properties.getProperty(key).replaceAll("^\"|\"$", "");
    }

    private void write(String key, String value) {
        try {
            this.properties.setProperty(key, value);
            FileOutputStream fileOut = new FileOutputStream(this.file, false);
            this.properties.store(fileOut, "Delphix Properties");
            fileOut.close();
        } catch (IOException e) {
            this.listener.getLogger().print(e.getMessage());
        }
    }

    public String getEngine() {
        return this.read(this.engineRef);
    }

    public String getContainerOperation() {
        return this.read(this.containerOperation);
    }

    public String getBookmarkOperation() {
        return this.read(this.bookmarkOperation);
    }

    public String getContainerReference() {
        return this.read(this.containerRef);
    }

    public String getBookmarkReference() {
        return this.read(this.bookmarkRef);
    }

    public void setEngine(String engine) {
        this.write(this.engineRef, engine);
    }

    public void setContainerOperation(String operation) {
        this.write(this.containerOperation, operation);
    }

    public void setBookmarkOperation(String operation) {
        this.write(this.bookmarkOperation, operation);
    }

    public void setContainerReference(String container) {
        this.write(this.containerRef, container);
    }

    public void setBookmarkReference(String bookmark) {
        this.write(this.bookmarkRef, bookmark);
    }
}
