/**
 * Copyright (c) 2015 by Delphix. All rights reserved.
 */

package com.delphix.delphix;

/**
 * Thrown when the Delphix Engine returns an error for an API request.
 */
public class DelphixEngineException extends Exception {
    private static final long serialVersionUID = 1L;

    public DelphixEngineException(String message) {
        super(message);
    }
}
