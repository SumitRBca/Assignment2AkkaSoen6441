package models;

// import models.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import Reddit.RedditHelper;
import play.libs.ws.*;
import java.util.concurrent.CompletionStage;
import com.fasterxml.jackson.databind.*;
import play.libs.Json;

import static java.util.stream.Collectors.*;

public class QuerySearchResult {
  private String searchTerm;
  private List<SearchResult> posts;
  private List<SearchResult> allPosts;
  private HashMap<String, Integer> analytics;
  // private static String[] IGNORE_WORDS = new String[] { "the", "is", "in", "for", "where", "when", "to", "at" };

  public QuerySearchResult(String query) {
    this.searchTerm = query;
    this.posts = new ArrayList<>();
    this.allPosts = new ArrayList<>();
    this.analytics = new HashMap<String, Integer>();
  }

  public String getSearchTerm() {
    return searchTerm;
  }

  public CompletionStage<List<SearchResult>> PopulateData(RedditHelper helper) {
    var response = helper.getSearchResult(this.searchTerm);
    return response.thenApply((List<SearchResult> posts) -> {
      this.allPosts = posts;
      // var x = Arrays.stream(posts).filter(v -> {
      //   return v.title.length > 4 && v.selftext.length > 4 && !Arrays.stream(IGNORE_WORDS).anyMatch("s"::equals);
      // }).map((var filteredPosts) -> {});
      return posts;
    });
  }

  public CompletionStage<List<SearchResult>> PopulateThread(RedditHelper helper) {
    var response = helper.getSubredditPosts(this.searchTerm);
    return response.thenApply((List<SearchResult> posts) -> {
      this.allPosts = posts;
      return posts;
    });
  }

  public CompletionStage<List<SearchResult>> PopulateUser(RedditHelper helper) {
    var response = helper.getUserPosts(this.searchTerm);
    return response.thenApply((List<SearchResult> posts) -> {
      this.allPosts = posts;
      return posts;
    });
  }

  public List<SearchResult> getAllPosts(){
    return this.allPosts;
  }

  public List<SearchResult> getData(){
    return this.posts;
  }

  public void setKeyTermData(List<SearchResult> posts) {
    this.posts = posts;
  }

  // private SearchResult mapKeyTermData(SearchResult e){
  //   return new SearchResult(e.title, e.author, e.body, e.subreddit);
  // }
}
