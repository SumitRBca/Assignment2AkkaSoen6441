package models;

// import models.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  public QuerySearchResult(String query) {
    this.searchTerm = query;
    this.posts = new ArrayList<>();
  }

  public String getSearchTerm() {
    return searchTerm;
  }

  public void PopulateData(RedditHelper helper) {
    var response = helper.getSearchResult(this.searchTerm);
    System.out.println(response);
    this.setKeyTermData(response);
    // response.thenApply((List<SearchResult> posts) -> {
    //   this.posts = posts;
    //   return posts;
    // });
  }

  public void PopulateThread(RedditHelper helper) {
    var response = helper.getSubredditPosts(this.searchTerm);
    this.setKeyTermData(response);
  }

  public void PopulateUser(RedditHelper helper) {
    var response = helper.getUserPosts(this.searchTerm);
    this.setKeyTermData(response);
  }

  // private SearchResult mapKeyTermData(SearchResult e){
  //   return new SearchResult(e.getId().getVideoId(),e.getSnippet().getChannelId(),e.getSnippet().getChannelTitle(),e.getSnippet().getTitle(), Utility.GetTimeElapsedTillNow(e.getSnippet().getPublishedAt()));
  // }

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
