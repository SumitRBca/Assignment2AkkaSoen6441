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

  /**
   * Public constructor of the RedditHelper method that initializes the class
   * @author Saghana Mahesh Sarma
   * @param ws - WSClient instance to make http request calls
   * @param endpoint - BaseURL for making http calls with
   */
  public RedditHelper(WSClient ws, String endpoint) {
    this.ws = ws;
    this.endpoint = endpoint;
  }

  /**
   * Get instance of the WSClient with default api configuration and query parameters
   * @author Saghana Mahesh Sarma
   * @return WSClient instance
   */
  private WSRequest getWSInstance() {
    WSRequest req = ws.url(endpoint + "/submission");
    req.addQueryParameter("over_18", "false");
    return req;
  }

  /**
   * Format response received from the http request to playshift api.
   * Convert the response body to JSON and read select properties defined from {@link SearchResult}
   * @author Saghana Mahesh Sarma
   * @return Returns function that takes a WSResponse and returns a List of SearchResults
   */
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

  /**
   * Get subreddit posts from playshift api by passing empty search query and
   * requested subreddit searchterm
   * @author Saghana Mahesh Sarma
   * @param sr Subreddit to filter search results
   * @return Returns CompletionStage of WSClient response from request
   */
  public CompletionStage<List<SearchResult>> getSubredditPosts(String sr) {
    WSRequest req = this.getWSInstance();
    req.addQueryParameter("q", "");
    req.addQueryParameter("subreddit", sr);
    req.addQueryParameter("size", "10");

    return req.get().thenApply(formatResponse());
  }

  /**
   * Get user profile and post information from playshift api by passing empty search query and
   * requested userId
   * @author Saghana Mahesh Sarma
   * @param author User to filter results by
   * @return Returns CompletionStage of WSClient response from request
   */
  public CompletionStage<List<SearchResult>> getUserPosts(String author) {
    WSRequest req = this.getWSInstance();
    req.addQueryParameter("q", "");
    req.addQueryParameter("author", author);
    req.addQueryParameter("size", "10");

    return req.get().thenApply(formatResponse());
  }

  /**
   * Get posts from playshift api by for requested query search term
   * @author Saghana Mahesh Sarma
   * @param query Search query to fetch results based on
   * @return Returns CompletionStage of WSClient response from request
   */
  public CompletionStage<List<SearchResult>> getSearchResult(String query) {
    WSRequest req = this.getWSInstance();
    req.addQueryParameter("q", query);
    req.addQueryParameter("size", "50");

    return req.get().thenApply(formatResponse());
  }
}
