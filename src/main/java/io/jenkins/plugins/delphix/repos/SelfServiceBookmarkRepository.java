/**
 * Copyright (c) 2018 by Delphix. All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jenkins.plugins.delphix.repos;

import com.fasterxml.jackson.databind.JsonNode;
import io.jenkins.plugins.delphix.DelphixEngine;
import io.jenkins.plugins.delphix.DelphixEngineException;
import io.jenkins.plugins.delphix.objects.SelfServiceBookmark;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.LinkedHashMap;

/* Used for interacting with Self Service Bookmarks */
public class SelfServiceBookmarkRepository {

  private static final String PATH_ROOT = "/resources/json/delphix/jetstream/bookmark";
  private DelphixEngine delphixEngine;

  public SelfServiceBookmarkRepository(DelphixEngine engine) {
    delphixEngine = engine;
  }

  /**
   * List Bookmarks in the Delphix Engine.
   *
   * @return LinkedHashMap
   * @throws ClientProtocolException [description]
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public LinkedHashMap<String, SelfServiceBookmark> listBookmarks()
      throws IOException, DelphixEngineException {
    LinkedHashMap<String, SelfServiceBookmark> bookmarks =
        new LinkedHashMap<String, SelfServiceBookmark>();
    JsonNode bookmarksJson = this.list();

    for (int i = 0; i < bookmarksJson.size(); i++) {
      JsonNode bookmarkJson = bookmarksJson.get(i);
      SelfServiceBookmark bookmark = SelfServiceBookmark.fromJson(bookmarkJson);
      bookmarks.put(bookmark.getReference(), bookmark);
    }

    return bookmarks;
  }

  /**
   * List Bookmarks.
   *
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode list() throws IOException, DelphixEngineException {
    JsonNode result = delphixEngine.engineGet(PATH_ROOT).get("result");
    return result;
  }

  /**
   * Delete a Self Service Bookmark by Reference.
   *
   * @param bookmarkRef String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode delete(String bookmarkRef) throws IOException, DelphixEngineException {
    JsonNode result = delphixEngine.enginePost(String.format(PATH_ROOT + "/%s/delete", bookmarkRef), "{}");
    return result;
  }

  /**
   * Create a new Self Service Bookmark.
   *
   * @param name String
   * @param branch String
   * @param sourceDataLayout String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode create(String name, String branch, String sourceDataLayout)
      throws IOException, DelphixEngineException {
    String request =
        "{"
            + "\"type\": \"JSBookmarkCreateParameters\","
            + "\"bookmark\": { \"type\": \"JSBookmark\","
            + "\"name\": \""
            + name
            + "\","
            + "\"branch\": \""
            + branch
            + "\""
            + "},\"timelinePointParameters\": {\"type\": \"JSTimelinePointLatestTimeInput\","
            + "\"sourceDataLayout\": \""
            + sourceDataLayout
            + "\""
            + "}}";
    JsonNode result = delphixEngine.enginePost(PATH_ROOT, request);
    return result;
  }

  /**
   * Share a Self Service Bookmark.
   *
   * @param bookmarkRef String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode share(String bookmarkRef) throws IOException, DelphixEngineException {
    JsonNode result = delphixEngine.enginePost(String.format(PATH_ROOT + "/%s/share", bookmarkRef), "{}");
    return result;
  }

  /**
   * Unshare a Self Service Bookmark.
   *
   * @param bookmarkRef String
   * @return JsonNode
   * @throws IOException [description]
   * @throws DelphixEngineException [description]
   */
  public JsonNode unshare(String bookmarkRef) throws IOException, DelphixEngineException {
    JsonNode result = delphixEngine.enginePost(String.format(PATH_ROOT + "/%s/unshare", bookmarkRef), "{}");
    return result;
  }
}
