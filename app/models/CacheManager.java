package models;

import models.*;
import play.mvc.*;
import play.libs.ws.*;

import java.util.List;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Collectors;
import play.libs.ws.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import Reddit.RedditHelper;


/**
 *  This class is a Singleton class which handles caching of already searched terms
 * @author Saghana Mahesh Sarma
 * @author Sumit Ramesh Bhiungade
 * @author Jai Sahni
 */

@Singleton
public class CacheManager {
  private static CacheManager cache = null;
  private static HashMap<String, QuerySearchResult> results;
  private static HashMap<String, QuerySearchResult> userResults;
  private static HashMap<String, QuerySearchResult> threadResults;
  private static RedditHelper helper;

  /**
   * CacheManager private default Constructor. This initializes the CacheManager object only once
   * @author Saghana Mahesh Sarma
   * @author Sumit Ramesh Bhiungade
   * @author Jai Sahni
   */

  private CacheManager() {
  }

  /**
   * This will be used to get the cache object
   * @author Saghana Mahesh Sarma
   * @author Sumit Ramesh Bhiungade
   * @author Jai Sahni
   * @return The singleton cache object {@link CacheManager}
   * */
  public static CacheManager GetCache(WSClient ws, String endpoint){
    if (cache==null) {
      cache = new CacheManager();
      helper = new RedditHelper(ws, endpoint);
      results = new HashMap<>();
      userResults = new HashMap<>();
      threadResults = new HashMap<>();
    }

    return cache;
  }
  /**
   * This method will return the Trimmed result set for given search term i.e only top 10 results which need to be shown to user. This will first fetch and store the data if it is not already present
   * @author Saghana Mahesh Sarma
   * @author Sumit Ramesh Bhiungade
   * @author Jai Sahni
   * @param keyTerm - The search term for which you want to fetch the result
   * @return The result of type {@link QuerySearchResult} which will contain only 10 results for that search term
   * */
  public CompletableFuture<QuerySearchResult> GetTrimmedSearchResult(String keyTerm){
    if(!results.containsKey(keyTerm)) {
      return AddToCache(keyTerm).thenApply((List<SearchResult> a) -> {
        var returnData = new QuerySearchResult(keyTerm);
        returnData.setKeyTermData(results.get(keyTerm).getAllPosts().stream().limit(10).collect(Collectors.toList()));
        returnData.setAnalytics(results.get(keyTerm).getAnalyticsData());
        return returnData;
      }).toCompletableFuture();
    }

    return CompletableFuture.supplyAsync(() -> {
      var returnData = new QuerySearchResult(keyTerm);
      returnData.setKeyTermData(results.get(keyTerm).getAllPosts().stream().limit(10).collect(Collectors.toList()));
      returnData.setAnalytics(results.get(keyTerm).getAnalyticsData());
      return returnData;
    });
  }

  /**
   * This method will add search results for a new keyterm to the cache. This will call the PopulateData method of {@link QuerySearchResult}
   * @author Saghana Mahesh Sarma
   * @author Sumit Ramesh Bhiungade
   * @author Jai Sahni
   * @param keyTerm - This is the search term for which we need to add the corresponding result to cache
   * @return {@link SearchResult}
   * */
  private CompletionStage<List<SearchResult>> AddToCache(String keyTerm){
    var result = new QuerySearchResult(keyTerm);
    return result.PopulateData(helper).thenApply((List<SearchResult> a) -> {
      results.put(keyTerm, result);
      return a;
    });
  }

  /**
   * This method checks if the subreddit is present in the Hash Data structure already else calls the below method -
   * AddThreadToCache to retrieve the information and returns the result
   * @author Jai Sahni
   * @param keyTerm - This is the search term for which we check if it already exists in the HashMap
   * @return {@link QuerySearchResult} object for corresponding subreddit
   *  @return {@link QuerySearchResult}
   * */
  public CompletableFuture<QuerySearchResult> GetThreadInfo(String keyTerm){
    if(!threadResults.containsKey(keyTerm)) {
      return AddThreadToCache(keyTerm).thenApply((List<SearchResult> a) -> {
        var returnData = new QuerySearchResult(keyTerm);
        returnData.setKeyTermData(threadResults.get(keyTerm).getAllPosts().stream().limit(10).collect(Collectors.toList()));
        return returnData;
      }).toCompletableFuture();
    }

    return CompletableFuture.supplyAsync(() -> {
      var returnData = new QuerySearchResult(keyTerm);
      returnData.setKeyTermData(threadResults.get(keyTerm).getAllPosts().stream().limit(10).collect(Collectors.toList()));
      return returnData;
    });
  }

  /**
   * This method will add subreddit information for a subreddit to the cache.
   * @author Jai Sahni
   * @param keyTerm - This is the search term for which we add the corresponding result to cache
   * @return {@link SearchResult}
   * */
  private CompletionStage<List<SearchResult>> AddThreadToCache(String keyTerm){
    var result = new QuerySearchResult(keyTerm);
    return result.PopulateThread(helper).thenApply((List<SearchResult> a) -> {
      threadResults.put(keyTerm, result);
      return a;
    });
  }

  /**
   * This method checks if the user Id is present in the Hash Data structure already else calls the below method -
   * AddOwnerInfoToCache to retrieve the information and returns the result
   * @author Saghana Mahesh Sarma
   * @param keyTerm - This is the search term for which we check if it already exists in the HashMap
   * @return {@link QuerySearchResult} object for corresponding user
   * */
  public CompletableFuture<QuerySearchResult> GetUserInfo(String keyTerm){
    if(!userResults.containsKey(keyTerm)) {
      return AddUserInfoToCache(keyTerm).thenApply((List<SearchResult> a) -> {
        var returnData = new QuerySearchResult(keyTerm);
        returnData.setKeyTermData(userResults.get(keyTerm).getAllPosts().stream().limit(10).collect(Collectors.toList()));
        return returnData;
      }).toCompletableFuture();
    }

    return CompletableFuture.supplyAsync(() -> {
      var returnData = new QuerySearchResult(keyTerm);
      returnData.setKeyTermData(userResults.get(keyTerm).getAllPosts().stream().limit(10).collect(Collectors.toList()));
      return returnData;
    });
  }

  /**
   * This method will add user information for a user id to the cache.
   * @author Saghana Mahesh Sarma
   * @param keyTerm - This is the search term for which we add the corresponding result to cache
   * */
  private CompletionStage<List<SearchResult>> AddUserInfoToCache(String keyTerm){
    var result = new QuerySearchResult(keyTerm);
    return result.PopulateUser(helper).thenApply((List<SearchResult> a) -> {
      userResults.put(keyTerm, result);
      return a;
    });
  }
}
