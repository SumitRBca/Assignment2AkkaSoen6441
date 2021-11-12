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

  public CompletionStage<List<SearchResult>> getSubredditPosts(String sr) {
    System.out.println("thread --" + sr);

    WSRequest req = ws.url(endpoint + "/submission");
    req.addQueryParameter("q", "");
    req.addQueryParameter("subreddit", sr);
    req.addQueryParameter("over_18", "false");
    req.addQueryParameter("fields", "title,author,selftext,subreddit,author_fullname,author_is_blocker,author_premium");

    return req.get().thenApply((WSResponse res) -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        List<SearchResult> postList = new ArrayList<SearchResult>();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        postList = Arrays.asList(mapper.readValue(res.asJson().get("data").toString(), SearchResult[].class));
        System.out.println(postList);
        return postList;

      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    });
  }

  public CompletionStage<List<SearchResult>> getUserPosts(String author) {
    WSRequest req = ws.url(endpoint + "/submission");
    req.addQueryParameter("q", "");
    req.addQueryParameter("author", author);
    req.addQueryParameter("over_18", "false");
    req.addQueryParameter("fields", "title,author,selftext,subreddit,author_fullname,author_is_blocker,author_premium");

    return req.get().thenApply((WSResponse res) -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        List<SearchResult> postList = new ArrayList<SearchResult>();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        postList = Arrays.asList(mapper.readValue(res.asJson().get("data").toString(), SearchResult[].class));
        System.out.println(postList);
        return postList;

      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    });
  }

  public CompletionStage<List<SearchResult>> getSearchResult(String query) {
    System.out.println("query --" + query);

    WSRequest req = ws.url(endpoint + "/submission");
    req.addQueryParameter("q", query);
    req.addQueryParameter("over_18", "false");
    req.addQueryParameter("fields", "title,author,selftext,subreddit,author_fullname,author_is_blocker,author_premium");
    // req.addQueryParameter("size", "100");

    return req.get().thenApply((WSResponse res) -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        List<SearchResult> postList = new ArrayList<SearchResult>();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        postList = Arrays.asList(mapper.readValue(res.asJson().get("data").toString(), SearchResult[].class));
        System.out.println(postList);
        return postList;

      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    });
  }
}
