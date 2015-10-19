/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
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
