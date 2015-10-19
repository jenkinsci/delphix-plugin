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
 * Represents a container on the Delphix Engine.
 */
public class DelphixContainer {
    public enum ContainerType {
        SOURCE,
        VDB
    }

    /**
     * Address of the Delphix Engine
     */
    private String engineAddress;

    /**
     * Name of the container
     */
    private String name;

    /**
     * Reference for the container
     */
    private String reference;

    private ContainerType type;

    public DelphixContainer(String engineAddress, String name, String reference, ContainerType type) {
        this.engineAddress = engineAddress;
        this.name = name;
        this.reference = reference;
        this.type = type;
    }

    public String getEngineAddress() {
        return engineAddress;
    }

    public String getName() {
        return name;
    }

    public String getReference() {
        return reference;
    }

    public ContainerType getType() {
        return type;
    }
}
