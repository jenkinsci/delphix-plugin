/**
 * Copyright (c) 2015, 2018 by Delphix. All rights reserved.
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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Handles the loading of localization messages.
 */
public final class Messages {

    /*
     * IDs for messages that can be localized
     */
    private static final String MESSAGE_BUNDLE = "messages";
    public static final String CANCEL_JOB_FAIL = "cancel.job.fail";
    public static final String NO_ENGINES = "no.engines";
    public static final String INVALID_ENGINE_CONTAINER = "invalid.engine.container";
    public static final String INVALID_ENGINE_ENVIRONMENT = "invalid.engine.environment";
    public static final String UNABLE_TO_LOGIN = "unable.to.login";
    public static final String UNABLE_TO_CONNECT = "unable.to.connect";
    public static final String CANCELED_JOB = "canceled.job";
    public static final String TEST_LOGIN_SUCCESS = "test.login.success";
    public static final String TEST_LOGIN_FAILURE = "test.login.failure";
    public static final String TEST_LOGIN_CONNECT = "test.login.connect";
    public static final String REFRESH_OPERATION = "refresh.operation";
    public static final String ROLLBACK_OPERATION = "rollback.operation";
    public static final String PROVISION_OPERATION = "provision.operation";
    public static final String ENVIRONMENT_CREATE_OPERATION = "environment.create.operation";
    public static final String SYNC_OPERATION = "sync.operation";
    public static final String ENVIRONMENT_REFRESH_OPERATION = "environment.refresh.operation";
    public static final String PLUGIN_NAME = "plugin.name";
    public static final String RETRY = "retry";
    public static final String DELETE_CONTAINER_OPERATION = "delete.container.operation";
    public static final String CONTAINER_BUILDER_SUMMARY = "container.builder.summary";
    public static final String CONTAINER_NOT_PRESENT = "container.not.present";
    public static final String ENVIRONMENT_DELETE_OPERATION = "environment.delete.operation";
    public static final String UPDATE_HOOKS = "update.hooks";
    public static final String UPDATE_HOOKS_SKIP = "update.hooks.skip";
    public static final String UPDATE_HOOKS_ORACLE_SKIP = "update.hooks.oracle.skip";
    public static final String SELFSERVICE_OPERATION = "selfservice.operation";

    private static ResourceBundle messages = ResourceBundle.getBundle(MESSAGE_BUNDLE, Locale.getDefault());

    /**
     * Get message that does not have any parameters
     *
     * @param  message Sting
     * @return         String
     */
    public static String getMessage(String message) {
        return messages.getString(message);
    }

    /**
     * Get message with parameters
     *
     * @param  message String
     * @param  params  String[]
     * @return         String
     */
    public static String getMessage(String message, String[] params) {
        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(Locale.getDefault());
        formatter.applyPattern(messages.getString(message));
        return formatter.format(params);
    }
}
