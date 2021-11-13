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
  private HashMap<String, Integer> analytics;
  private static String[] IGNORE_WORDS = new String[] { "the", "is", "in", "for", "where", "when", "to", "at" };

  public QuerySearchResult(String query) {
    this.searchTerm = query;
    this.posts = new ArrayList<>();
    this.allPosts = new ArrayList<>();
    this.analytics = new HashMap<String, Integer>();
  }

  public String getSearchTerm() {
    return searchTerm;
  }

  private String sanitizeData(String content) {
    return content
      .trim()
      .toLowerCase()
      .replace(",", "")
      .replace("\"", "")
      .replace("!", "")
      .replace("?", "")
      .replace(":", "")
      .replace(".", "")
      .replace("(", "")
      .replace(")", "")
      .replace("-", "");
  }

  public CompletionStage<List<SearchResult>> PopulateData(RedditHelper helper) {
    var response = helper.getSearchResult(this.searchTerm);
    return response.thenApply((List<SearchResult> posts) -> {
      this.allPosts = posts;

      posts
        .stream()
        .map((d) -> (d.title + " " + d.selftext).split(" "))
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

  public void setAnalytics(HashMap<String, Integer> analytics) {
    this.analytics = analytics;
  }

  public HashMap<String, Integer> getAnalyticsData() {
    return this.analytics;
  }

  public List<Map.Entry<String,Integer>> getAnalytics() {
    return analytics
      .entrySet()
      .stream()
      .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
      .collect(Collectors.toList());
  }

  public void setKeyTermData(List<SearchResult> posts) {
    this.posts = posts;
  }
}
