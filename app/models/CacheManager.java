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

  // public QuerySearchResult GetFullSearchResult(String keyTerm){
  //   System.out.println(keyTerm);
  //   if(!results.containsKey(keyTerm)) AddToCache(keyTerm);
  //   return results.get(keyTerm);
  // }

  public CompletableFuture<QuerySearchResult> GetTrimmedSearchResult(String keyTerm){
    if(!results.containsKey(keyTerm)) {
      return AddToCache(keyTerm).thenApply((List<SearchResult> a) -> {
        var returnData = new QuerySearchResult(keyTerm);
        returnData.setKeyTermData(results.get(keyTerm).getAllPosts().stream().limit(10).collect(Collectors.toList()));
        return returnData;
      }).toCompletableFuture();
    }

    return CompletableFuture.supplyAsync(() -> {
      var returnData = new QuerySearchResult(keyTerm);
      returnData.setKeyTermData(results.get(keyTerm).getAllPosts().stream().limit(10).collect(Collectors.toList()));
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

  public QuerySearchResult GetThreadInfo(String key) {
    if (!threadResults.containsKey(key)) {
      AddThreadToCache(key);
    }

    var returnData = new QuerySearchResult(key);
    returnData.setKeyTermData(threadResults.get(key).getAllPosts().stream().limit(10).collect(Collectors.toList()));
    return returnData;
  }

  private void AddThreadToCache(String key) {
    var result = new QuerySearchResult(key);
    result.PopulateThread(helper);
    threadResults.put(key, result);
  }

  public QuerySearchResult GetUserInfo(String key) {
    if (!userResults.containsKey(key)) {
      AddUserInfoToCache(key);
    }

    var returnData = new QuerySearchResult(key);
    returnData.setKeyTermData(userResults.get(key).getAllPosts().stream().limit(10).collect(Collectors.toList()));
    return returnData;
  }

  private void AddUserInfoToCache(String key) {
    var result = new QuerySearchResult(key);
    result.PopulateUser(helper);
    userResults.put(key, result);
  }
}
