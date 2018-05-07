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

/**
 * Represents a group in the Delphix Engine
 */
public class SelfServiceRequest {

    private final String type;
    private final Boolean forceOption;
    private final String bookmark;

    public SelfServiceRequest(String type, Boolean forceOption, String bookmark) {
        this.type = type;
        this.forceOption = forceOption;
        this.bookmark = bookmark;
    }

    private String buildRequestBody(String type){
        String build = "";
        switch (type) {
            case "JSDataContainerRefreshParameters":
                build += "\"forceOption\":" + this.forceOption;
                break;
            case "JSDataContainerRestoreParameters":
                build += "\"timelinePointParameters\":{\"type\":\"JSTimelinePointBookmarkInput\",\"bookmark\":\""+ this.bookmark + "\"}";
                build += "\"forceOption\":" + this.forceOption;
                break;
            case "JSDataContainerResetParameters":
                build += "\"forceOption\":" + this.forceOption;
                break;
            case "JSDataContainerLockParameters":
                break;
        }
        return build;
    }

    public String toJson() {
        String build = "{";
        build += "\"type\":\"" +this.type + "\",";
        build += this.buildRequestBody(this.type);
        build += "}";
        return build;
    }
}
