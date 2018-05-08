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
 * Represents a group in the Delphix Engine
 */
public class User {

    private final String type;
    private final String reference;
    private final String namespace;
    private final String name;
    private final String userType;
    private final String emailAddress;
    private final Boolean enabled;
    private final String firstName;
    private final String lastName;
    private final String passwordUpdateRequest;
    private final Boolean isDefault;
    private final String mobilePhoneNumber;
    private final String workPhoneNumber;
    private final String homePhoneNumber;
    private final String authenticationType;
    private final String principal;
    private final String credential;
    private final String publicKey;
    private final Number sessionTimeout;
    private final String locale;

    public User(
        String type,
        String reference,
        String namespace,
        String name,
        String userType,
        String emailAddress,
        Boolean enabled,
        String firstName,
        String lastName,
        String passwordUpdateRequest,
        Boolean isDefault,
        String mobilePhoneNumber,
        String workPhoneNumber,
        String homePhoneNumber,
        String authenticationType,
        String principal,
        String credential,
        String publicKey,
        Number sessionTimeout,
        String locale
    ) {
        this.type = type;
        this.reference = reference;
        this.namespace = namespace;
        this.name = name;
        this.userType = userType;
        this.emailAddress = emailAddress;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordUpdateRequest = passwordUpdateRequest;
        this.isDefault = isDefault;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.workPhoneNumber = workPhoneNumber;
        this.homePhoneNumber = homePhoneNumber;
        this.authenticationType = authenticationType;
        this.principal = principal;
        this.credential = credential;
        this.publicKey = publicKey;
        this.sessionTimeout = sessionTimeout;
        this.locale = locale;
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

    public String getUserType(){
        return this.userType;
    }

    public String getEmailAddress(){
        return this.emailAddress;
    }

    public Boolean getEnabled(){
        return this.enabled;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getPasswordUpdateRequest(){
        return this.passwordUpdateRequest;
    }

    public Boolean getIsDefault(){
        return this.isDefault;
    }

    public String getMobilePhoneNumber(){
        return this.mobilePhoneNumber;
    }

    public String getWorkPhoneNumber(){
        return this.workPhoneNumber;
    }

    public String getHomePhoneNumber(){
        return this.homePhoneNumber;
    }

    public String getAuthenticationType(){
        return this.authenticationType;
    }

    public String getPrincipal(){
        return this.principal;
    }

    public String getCredential(){
        return this.credential;
    }

    public String getPublicKey(){
        return this.publicKey;
    }

    public Number getSessionTimeout(){
        return this.sessionTimeout;
    }

    public String getLocale(){
        return this.locale;
    }

    public static User fromJson(JsonNode json){
        User user = new User(
            json.get("type").asText(),
            json.get("reference").asText(),
            json.get("namespace").asText(),
            json.get("name").asText(),
            json.get("userType").asText(),
            json.get("emailAddress").asText(),
            json.get("enabled").asBoolean(),
            json.get("firstName").asText(),
            json.get("lastName").asText(),
            json.get("passwordUpdateRequest").asText(),
            json.get("isDefault").asBoolean(),
            json.get("mobilePhoneNumber").asText(),
            json.get("workPhoneNumber").asText(),
            json.get("homePhoneNumber").asText(),
            json.get("authenticationType").asText(),
            json.get("principal").asText(),
            json.get("credential").asText(),
            json.get("publicKey").asText(),
            json.get("sessionTimeout").asInt(),
            json.get("locale").asText()
        );
        return user;
    }
}
