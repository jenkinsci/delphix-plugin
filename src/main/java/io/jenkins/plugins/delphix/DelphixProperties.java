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

    public DelphixProperties(FilePath workspace, TaskListener listener) {
        this.properties = new Properties();
        this.file = new File(workspace + "/delphix.properties");
        this.listener = listener;
    }

    private String read(String key) {
        String result = "";
        try {
            FileInputStream fileInput = new FileInputStream(this.file);
            this.properties.load(fileInput);
            fileInput.close();
            result = this.properties.getProperty(key);
        } catch (FileNotFoundException e) {
            this.listener.getLogger().print(e.getMessage());
        } catch (IOException e) {
            this.listener.getLogger().print(e.getMessage());
        }
        return result.replaceAll("^\"|\"$", "");
    }

    private void write(String key, String value) {
        try {
            properties.setProperty(key, value);
            FileOutputStream fileOut = new FileOutputStream(this.file);
            properties.store(fileOut, "Delphix Properties");
            fileOut.close();
        } catch (IOException e) {
            this.listener.getLogger().print(e.getMessage());
        }
    }

    public String getContainer() {
        return this.read("container");
    }

    public String getBookmark() {
        return this.read("bookmark");
    }

    public void setContainer(String container) {
        this.write("container", container);
    }

    public void setBookmark(String bookmark) {
        this.write("bookmark", bookmark);
    }
}
