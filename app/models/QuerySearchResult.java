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

/**
 * This class will store all the data regarding all the Reddit Posts pertaining to a particular search term.
 * @author Saghana Mahesh Sarma
 * @author Sumit Ramesh Bhiungade
 * @author Jai Sahni
 * */
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

  /**
   * This is the getter method to get the search term associated with the search results
   * @author Saghana Mahesh Sarma
   * @author Sumit Ramesh Bhiungade
   * @author Jai Sahni
   * @return The search term associated with this search result
   * */

  public String getSearchTerm() {
    return searchTerm;
  }

  /**
   * This is the method to clean the content of unrequired symbols
   * @author Saghana Mahesh Sarma
   * @author Sumit Ramesh Bhiungade
   * @author Jai Sahni
   * @return The filtered content
   * */
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

  /**
   * This method is used to fetch the required data associated with a search term from Reddit, map it to the model class {@link SearchResult},
   * <p>
   * This method also used to perform the word stats on the query .
   * @author Saghana Mahesh Sarma
   * @author Sumit Ramesh Bhiungade
   * @author Jai Sahni
   * @param helper - This is the corresponding implementation you want to use to fetch data
   * @return The Reddit posts under a given search term
   * */
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

  /**
   * This method used to fetch the reddit posts under a particular subreddits
   * @author Jai Sahni
   * @param helper - This is the corresponding implementation you want to use to fetch comments
   * @return The subreddit posts
   */
  public CompletionStage<List<SearchResult>> PopulateThread(RedditHelper helper) {
    var response = helper.getSubredditPosts(this.searchTerm);
    return response.thenApply((List<SearchResult> posts) -> {
      this.allPosts = posts;
      return posts;
    });
  }
  /**
   * This method used to fetch the user info of the author of a reddit post
   * @author Saghana Mahesh Sarma
   * @param helper - This is the corresponding implementation you want to use to fetch comments
   * @return The information of the author
   */
  public CompletionStage<List<SearchResult>> PopulateUser(RedditHelper helper) {
    var response = helper.getUserPosts(this.searchTerm);
    return response.thenApply((List<SearchResult> posts) -> {
      this.allPosts = posts;
      return posts;
    });
  }

  /**
   * This method is a getter method that returns all reddit posts
   * @author Saghana Mahesh Sarma
   * @return all reddit posts
   */
  public List<SearchResult> getAllPosts(){
    return this.allPosts;
  }

  /**
   * This is the getter method to return all the posts along with their data under a given search term
   * @author Saghana Mahesh Sarma
   * @return A list of all Reddit data({@link SearchResult})
   * */
  public List<SearchResult> getData(){
    return this.posts;
  }

  /**
   * This is a setter method that stores analytics data in the field instance
   * @author Saghana Mahesh Sarma
   * @param analytics HashMap of words and their frequency
   */
  public void setAnalytics(HashMap<String, Integer> analytics) {
    this.analytics = analytics;
  }

  /**
   * This is a getter method that returns the latest analytics HashMap
   * @author Saghana Mahesh Sarma
   * @return analytics HashMap of words and their frequency
   */
  public HashMap<String, Integer> getAnalyticsData() {
    return this.analytics;
  }

  /**
   * Getter method to get the formatted analytics data from the instance as a list
   * @author Saghana Mahesh Sarma
   * @return List of words and their frequencies
   */
  public List<Map.Entry<String,Integer>> getAnalytics() {
    return analytics
      .entrySet()
      .stream()
      .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
      .collect(Collectors.toList());
  }

  /**
   * This is setter method that can be used to set the reddit data for given search term
   * @author Saghana Mahesh Sarma
   * @param posts - List of reddit data({@link SearchResult}) for given search term
   * */
  public void setKeyTermData(List<SearchResult> posts) {
    this.posts = posts;
  }
}
