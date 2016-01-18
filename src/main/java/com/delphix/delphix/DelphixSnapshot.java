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
 * Represents a timeflow in the Delphix Engine
 */
public class DelphixSnapshot {
    private final String name;

    private final String reference;

    private final String containerRef;

    private final String timeflowRef;

    private final String latestChangePoint;

    public DelphixSnapshot(String reference, String name, String containerRef, String timeflowRef,
            String latestChangePoint) {
        this.reference = reference;
        this.name = name;
        this.containerRef = containerRef;
        this.timeflowRef = timeflowRef;
        this.latestChangePoint = latestChangePoint;
    }

    public String getName() {
        return this.name;
    }

    public String getReference() {
        return this.reference;
    }

    public String getContainerRef() {
        return this.containerRef;
    }

    public String getTimeflowRef() {
        return this.timeflowRef;
    }

    public String getLatestChangePoint() {
        return this.latestChangePoint;
    }
}
