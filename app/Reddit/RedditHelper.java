package Reddit;

import models.*;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import play.mvc.*;
import play.api.libs.json.Json;
import play.libs.ws.*;
import play.api.Application;
import java.util.concurrent.CompletionStage;
import com.fasterxml.jackson.databind.*;
// import play.libs.Json;

public class RedditHelper {
  private final WSClient ws;

  private static String endpoint = "https://api.pushshift.io/reddit/search";

  public RedditHelper(WSClient ws) {
    this.ws = ws;
  }

  public List<SearchResult> getSubredditPosts(String sr) {
    try {
      WSRequest req = ws.url(endpoint + "/submission");
      req.addQueryParameter("q", "");
      req.addQueryParameter("subreddit", sr);
      req.addQueryParameter("over_18", "false");

      WSResponse res = req.get().toCompletableFuture().get();

      ObjectMapper mapper = new ObjectMapper();
      List<SearchResult> postList = new ArrayList<SearchResult>();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      postList = Arrays.asList(mapper.readValue(res.asJson().get("data").toString(), SearchResult[].class));
      // System.out.println(postList);
      return postList;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public List<SearchResult> getUserPosts(String author) {
    try {
      WSRequest req = ws.url(endpoint + "/submission");
      req.addQueryParameter("q", "");
      req.addQueryParameter("author", author);
      req.addQueryParameter("over_18", "false");

      WSResponse res = req.get().toCompletableFuture().get();

      ObjectMapper mapper = new ObjectMapper();
      List<SearchResult> postList = new ArrayList<SearchResult>();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      postList = Arrays.asList(mapper.readValue(res.asJson().get("data").toString(), SearchResult[].class));
      // System.out.println(postList);
      return postList;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

  public List<SearchResult> getSearchResult(String query) {
    try {
      WSRequest req = ws.url(endpoint + "/submission");
      req.addQueryParameter("q", query);
      req.addQueryParameter("over_18", "false");
      WSResponse res = req.get().toCompletableFuture().get();

      ObjectMapper mapper = new ObjectMapper();
      List<SearchResult> postList = new ArrayList<SearchResult>();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      postList = Arrays.asList(mapper.readValue(res.asJson().get("data").toString(), SearchResult[].class));
      // System.out.println(postList);
      return postList;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
