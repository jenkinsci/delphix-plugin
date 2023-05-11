package io.jenkins.plugins.util;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.delphix.dct.models.ProvisionVDBBySnapshotParameters;
import com.delphix.dct.models.ProvisionVDBFromBookmarkParameters;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

public class ValidationUtil {
    private TypeAdapter<JsonObject> strictGsonObjectAdapter;
    private JsonObject result;
    private HashSet<String> snapshotFieldSet;
    private HashSet<String> bookmarkFieldSet;

    public ValidationUtil() {
        strictGsonObjectAdapter = new Gson().getAdapter(JsonObject.class);
        result = null;
        snapshotFieldSet = new HashSet<String>();
        bookmarkFieldSet = new HashSet<String>();
    }

    public void validateJsonFormat(String json) {
        try {
            JsonReader reader = new JsonReader(new StringReader(json));
            result = strictGsonObjectAdapter.read(reader);
        } catch (IOException e) {
            throw new JsonSyntaxException(e);
        }
    }

    public String validateJsonWithSnapshotProvisionParameters() {
        if (result != null && result.keySet().size() > 0) {
            Field[] provisionSnapshotFields = ProvisionVDBBySnapshotParameters.class.getDeclaredFields();
            for (Field x : provisionSnapshotFields) {
                SerializedName sName = x.getAnnotation(SerializedName.class);
                if (sName != null) {
                    snapshotFieldSet.add(sName.value());
                }
            }
            for (String jsonKey : result.keySet()) {
                if (!snapshotFieldSet.contains(jsonKey)) {
                    return jsonKey;
                }
            }
            return null;
        }
        return null;
    }

    public String validateJsonWithBookmarkProvisionParameters() {
        if (result != null && result.keySet().size() > 0) {
            Field[] provisionBookmarkFields = ProvisionVDBFromBookmarkParameters.class.getDeclaredFields();
            for (Field x : provisionBookmarkFields) {
                SerializedName sName = x.getAnnotation(SerializedName.class);
                if (sName != null) {
                    bookmarkFieldSet.add(sName.value());
                }
            }
            for (String jsonKey : result.keySet()) {
                if (!bookmarkFieldSet.contains(jsonKey)) {
                    return jsonKey;
                }
            }
            return null;
        }
        return null;
    }

}
