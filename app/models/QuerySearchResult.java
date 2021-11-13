package models;

// import models.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

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
  // private List<Map.Entry<String,Integer>> analytics;
  private HashMap<String, Integer> analytics;
  private static String[] IGNORE_WORDS = new String[] { "the", "is", "in", "for", "where", "when", "to", "at" };

  public QuerySearchResult(String query) {
    this.searchTerm = query;
    this.posts = new ArrayList<>();
    this.allPosts = new ArrayList<>();
    this.analytics = new HashMap<String, Integer>();
    // this.analytics = new ArrayList<>();
  }

  public String getSearchTerm() {
    return searchTerm;
  }

  private String sanitizeData(String content) {
    return content.replace(",", "").trim();
  }

  public CompletionStage<List<SearchResult>> PopulateData(RedditHelper helper) {
    var response = helper.getSearchResult(this.searchTerm);
    return response.thenApply((List<SearchResult> posts) -> {
      this.allPosts = posts;
      var analytics = new HashMap<String, Integer>();

      posts
        .stream()
        .map((d) -> d.title + " " + d.selftext)
        .map(k -> k.split(" "))
        .flatMap(Arrays::stream)
        .map(this::sanitizeData)
        .filter(key -> key.length() > 4 && Arrays.stream(IGNORE_WORDS).anyMatch(x -> x != key))
        .forEach(key -> {
          var currentCount = analytics.get(key);
          if (analytics.containsKey(key)) {
            analytics.put(key, currentCount + 1);
          } else {
            analytics.put(key, 1);
          }
        });

      System.out.println(analytics);

      // this.analytics = analytics.entrySet().stream().sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue())).collect(Collectors.toList());

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

  // public List<Map.Entry<String,Integer>> getAnalytics() {
  public HashMap<String,Integer> getAnalytics() {
    // var analyticsData = analytics.entrySet().stream().sorted((k1, k2) -> {
    //   return -k1.getValue().compareTo(k2.getValue());
    // }).collect(Collectors.toList());

    System.out.println(analytics);
    // var analyticsData = this.analytics
    //   .entrySet()
    //   .stream()
    //   .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()));
    //   // .collect(Collectors.toList());

    return analytics;
  }

  public void setKeyTermData(List<SearchResult> posts) {
    this.posts = posts;
  }

  // private SearchResult mapKeyTermData(SearchResult e){
  //   return new SearchResult(e.title, e.author, e.body, e.subreddit);
  // }
}
