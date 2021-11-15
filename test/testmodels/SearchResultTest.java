package testmodels;

import models.*;
import org.junit.*;
import play.mvc.*;

import java.util.List;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.math.BigInteger;
import java.util.stream.Collectors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import static org.junit.Assert.*;

/**
 * This test method will be used to test the search results.
 * @author Sumit Ramesh Bhiungade
 * */
public class SearchResultTest {

  @Test
  public void searchResultTest() {
    String title = "test";
    String author = "test";
    String subreddit = "test";
    String selftext = "test";
    String author_fullname = "test";
    String author_is_blocked = "false";
    String author_premium = "false";

    var result = new SearchResult(title, author, selftext, subreddit, author_fullname, author_is_blocked, author_premium);

    Assert.assertEquals(title, result.title);
    Assert.assertEquals(author, result.author);
    Assert.assertEquals(selftext, result.selftext);
    Assert.assertEquals(subreddit, result.subreddit);
    Assert.assertEquals(author_fullname, result.author_fullname);
    Assert.assertEquals(author_is_blocked, result.author_is_blocked);
    Assert.assertEquals(author_premium, result.author_premium);
  }
}
