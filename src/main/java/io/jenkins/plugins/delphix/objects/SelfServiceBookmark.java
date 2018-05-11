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

package io.jenkins.plugins.delphix.objects;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a bookmark in the Delphix Engine
 */
public class SelfServiceBookmark {

    private final String type;
    private final String reference;
    private final String namespace;
    private final String name;
    private final String branch;
    private final String timestamp;
    private final String description;
    private final Boolean shared;
    private final String container;
    private final String template;
    private final String containerName;
    private final String templateName;
    private final Boolean usable;
    private final Number checkoutCount;
    private final String bookmarkType;
    private final Boolean expiration;
    private final String creationTime;

    public SelfServiceBookmark (
        String type,
        String reference,
        String namespace,
        String name,
        String branch,
        String timestamp,
        String description,
        Boolean shared,
        String container,
        String template,
        String containerName,
        String templateName,
        Boolean usable,
        Number checkoutCount,
        String bookmarkType,
        Boolean expiration,
        String creationTime
    ) {
        this.type = type;
        this.reference = reference;
        this.namespace = namespace;
        this.name = name;
        this.branch = branch;
        this.timestamp = timestamp;
        this.description = description;
        this.shared = shared;
        this.container = container;
        this.template = template;
        this.containerName = containerName;
        this.templateName = templateName;
        this.usable = usable;
        this.checkoutCount = checkoutCount;
        this.bookmarkType = bookmarkType;
        this.expiration = expiration;
        this.creationTime = creationTime;
    }

    public String getType(){
        return this.type;
    }

    public String getReference(){
        return this.reference;
    }

    public String getNamespace(){
        return this.namespace;
    }

    public String getName(){
        return this.name;
    }

    public String getBranch(){
        return this.branch;
    }

    public String getTimestamp(){
        return this.timestamp;
    }

    public String getDescription(){
        return this.description;
    }

    public Boolean getShared(){
        return this.shared;
    }

    public String getContainer(){
        return this.container;
    }

    public String getTemplate(){
        return this.template;
    }

    public String getContainerName(){
        return this.containerName;
    }

    public String getTemplateName(){
        return this.templateName;
    }

    public Boolean getUsable(){
        return this.usable;
    }

    public Number getCheckoutCount(){
        return this.checkoutCount;
    }

    public String getBookmarkType(){
        return this.bookmarkType;
    }

    public Boolean getExpiration(){
        return this.expiration;
    }

    public String getCreationTime(){
        return this.creationTime;
    }

    public static SelfServiceBookmark fromJson(JsonNode json){
        SelfServiceBookmark bookmark = new SelfServiceBookmark(
            json.get("type").asText(),
            json.get("reference").asText(),
            json.get("namespace").asText(),
            json.get("name").asText(),
            json.get("branch").asText(),
            json.get("timestamp").asText(),
            json.get("description").asText(),
            json.get("shared").asBoolean(),
            json.get("container").asText(),
            json.get("template").asText(),
            json.get("containerName").asText(),
            json.get("templateName").asText(),
            json.get("usable").asBoolean(),
            json.get("checkoutCount").asInt(),
            json.get("bookmarkType").asText(),
            json.get("expiration").asBoolean(),
            json.get("creationTime").asText()
        );
        return bookmark;
    }
}
