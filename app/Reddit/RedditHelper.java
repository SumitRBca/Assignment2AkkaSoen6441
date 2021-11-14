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
import java.util.function.Function;

import com.fasterxml.jackson.databind.*;

public class RedditHelper {
  private final WSClient ws;
  private final String endpoint;

  public RedditHelper(WSClient ws, String endpoint) {
    this.ws = ws;
    this.endpoint = endpoint;
  }

  private WSRequest getWSInstance() {
    WSRequest req = ws.url(endpoint + "/submission");
    req.addQueryParameter("over_18", "false");
    return req;
  }

  private Function<WSResponse, List<SearchResult>> formatResponse() throws NullPointerException {
    return (WSResponse res) -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        List<SearchResult> postList = new ArrayList<SearchResult>();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        postList = Arrays.asList(
          mapper.readValue(
            res.asJson().get("data").toString(),
            SearchResult[].class
          )
        );
        return postList;
      } catch (Exception e) {
        return null;
      }
    };
  }

  public CompletionStage<List<SearchResult>> getSubredditPosts(String sr) {
    WSRequest req = this.getWSInstance();
    req.addQueryParameter("q", "");
    req.addQueryParameter("subreddit", sr);
    req.addQueryParameter("size", "10");

    return req.get().thenApply(formatResponse());
  }

  public CompletionStage<List<SearchResult>> getUserPosts(String author) {
    WSRequest req = this.getWSInstance();
    req.addQueryParameter("q", "");
    req.addQueryParameter("author", author);
    req.addQueryParameter("size", "10");

    return req.get().thenApply(formatResponse());
  }

  public CompletionStage<List<SearchResult>> getSearchResult(String query) {
    WSRequest req = this.getWSInstance();
    req.addQueryParameter("q", query);
    req.addQueryParameter("size", "50");

    return req.get().thenApply(formatResponse());
  }
}
