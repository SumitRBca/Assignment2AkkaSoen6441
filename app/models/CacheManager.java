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

@Singleton
public class CacheManager {
  private static CacheManager cache = null;
  private static HashMap<String, QuerySearchResult> results;
  private static HashMap<String, QuerySearchResult> userResults;
  private static HashMap<String, QuerySearchResult> threadResults;
  private static RedditHelper helper;

  private CacheManager() {
  }

  public static CacheManager GetCache(WSClient ws){
    if (cache==null) {
      cache = new CacheManager();
      helper = new RedditHelper(ws);
      results = new HashMap<>();
      userResults = new HashMap<>();
      threadResults = new HashMap<>();
    }

    return cache;
  }

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

  private CompletionStage<List<SearchResult>> AddToCache(String keyTerm){
    var result = new QuerySearchResult(keyTerm);
    return result.PopulateData(helper).thenApply((List<SearchResult> a) -> {
      results.put(keyTerm, result);
      return a;
    });
  }

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

  private CompletionStage<List<SearchResult>> AddThreadToCache(String keyTerm){
    var result = new QuerySearchResult(keyTerm);
    return result.PopulateThread(helper).thenApply((List<SearchResult> a) -> {
      threadResults.put(keyTerm, result);
      return a;
    });
  }

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

  private CompletionStage<List<SearchResult>> AddUserInfoToCache(String keyTerm){
    var result = new QuerySearchResult(keyTerm);
    return result.PopulateUser(helper).thenApply((List<SearchResult> a) -> {
      userResults.put(keyTerm, result);
      return a;
    });
  }
}
