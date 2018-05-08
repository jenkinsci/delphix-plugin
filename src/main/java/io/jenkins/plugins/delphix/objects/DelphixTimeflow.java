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

package io.jenkins.plugins.delphix.objects;

/**
 * Represents a group in the Delphix Engine
 */
public class DelphixTimeflow {

    private final String reference;
    private final String name;
    private final String timestamp;
    private final String container;

    public DelphixTimeflow(String reference, String name, String timestamp, String container) {
        this.reference = reference;
        this.name = name;
        this.timestamp = timestamp;
        this.container = container;
    }

    public String getName() {
        return this.name;
    }

    public String getReference() {
        return this.reference;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getContainer() {
        return this.container;
    }
}
