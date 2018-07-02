/**
 * Copyright (c) 2018 by Delphix. All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jenkins.plugins.delphix;

import hudson.model.TaskListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class DelphixProperties {
  private final File file;
  private final Properties properties;
  private final TaskListener listener;
  static final String engineRef = "engine";
  static final String bookmarkOperation = "bookmark.operation";
  static final String containerOperation = "container.operation";
  static final String bookmarkRef = "bookmark.reference";
  static final String containerRef = "container.reference";

  /**
   * Constructor of Delphix Properties.
   *
   * @param workspace FilePath
   * @param listener  TaskListener
   */
  public DelphixProperties(File file, TaskListener listener) throws IOException {
    this.properties = new Properties();
    this.file = file;
    this.listener = listener;
    this.file.createNewFile();
    this.loadProperties();
  }

  private void loadProperties() throws IOException {
    FileInputStream fileInput = new FileInputStream(this.file);
    this.properties.load(fileInput);
  }

  private String read(String key) {
    return this.properties.getProperty(key).replaceAll("^\"|\"$", "");
  }

  private void write(String key, String value) throws IOException  {
      FileOutputStream fileOut = new FileOutputStream(this.file, false);
      this.properties.setProperty(key, value);
      this.properties.store(fileOut, "Delphix Properties");
  }

  public String getEngine() {
    return this.read(engineRef);
  }

  public String getContainerOperation() {
    return this.read(containerOperation);
  }

  public String getBookmarkOperation() {
    return this.read(bookmarkOperation);
  }

  public String getContainerReference() {
    return this.read(containerRef);
  }

  public String getBookmarkReference() {
    return this.read(bookmarkRef);
  }

  public void setEngine(String engine) throws IOException {
    this.write(engineRef, engine);
  }

  public void setContainerOperation(String operation) throws IOException {
    this.write(containerOperation, operation);
  }

  public void setBookmarkOperation(String operation) throws IOException {
    this.write(bookmarkOperation, operation);
  }

  public void setContainerReference(String container) throws IOException {
    this.write(containerRef, container);
  }

  public void setBookmarkReference(String bookmark) throws IOException {
    this.write(bookmarkRef, bookmark);
  }
}
