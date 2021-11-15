package Reddit;

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

import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;

import play.libs.ws.*;
import play.routing.RoutingDsl;
import play.server.Server;
import static play.mvc.Results.*;

/**
 * This test class is used for testing the routes and sub-reddits posts.
 * @author Saghana Mahesh Sarma
 * */
public class RedditHelperTest {
  private WSClient ws;
  private Server server;
  private RedditHelper redditHelper;

  /**
   * Setup mock server to listen to http requests being made from tests
   * to route '/submission' and instantiate mock WSClient
   * @author Sumit Ramesh Bhiungade
   * @author Saghana Mahesh Sarma
   * @author Jai Sahni
   * */
  @Before
  public void Setup() {
    server = Server.forRouter((components) ->
      RoutingDsl.fromComponents(components)
      .GET("/submission")
      .routingTo(request -> ok().sendResource("reddit.json"))
      .build()
    );

    ws = play.test.WSTestClient.newClient(server.httpPort());

    this.redditHelper = new RedditHelper(ws, "");
  }

  /**
   * This handleErrorTest method is used for testing the results for an invalid URL
   * @author Saghana Mahesh Sarma
   */
  @Test
  public void handleErrorTest() {
    List<SearchResult> result = new RedditHelper(ws, "https://www.github.com")
      .getSubredditPosts("test")
      .toCompletableFuture()
      .join();

    Assert.assertEquals(null, result);
  }

  /**
   * This method is used for testing the subreddit posts with each of its following contents
   * @author Saghana Mahesh Sarma
   */
  @Test
  public void getSubredditPostsTest() {
    List<SearchResult> result = redditHelper
      .getSubredditPosts("test")
      .toCompletableFuture()
      .join();

    Assert.assertEquals("test", result.get(0).title);
    Assert.assertEquals("test", result.get(0).author);
    Assert.assertEquals("test", result.get(0).subreddit);
    Assert.assertEquals("test", result.get(0).selftext);
    Assert.assertEquals("test", result.get(0).author_fullname);
    Assert.assertEquals("test", result.get(0).author_is_blocked);
    Assert.assertEquals("test", result.get(0).author_premium);
  }

  /**
   * This method is used for testing the user information from user profile
   * @author Saghana Mahesh Sarma
   */
  @Test
  public void getUserPostsTest() {
    List<SearchResult> result = redditHelper
      .getUserPosts("test")
      .toCompletableFuture()
      .join();

    Assert.assertEquals("test", result.get(0).title);
    Assert.assertEquals("test", result.get(0).author);
    Assert.assertEquals("test", result.get(0).subreddit);
    Assert.assertEquals("test", result.get(0).selftext);
    Assert.assertEquals("test", result.get(0).author_fullname);
    Assert.assertEquals("test", result.get(0).author_is_blocked);
    Assert.assertEquals("test", result.get(0).author_premium);
  }

  /**
   * This method is used for testing the submission results
   * @author Saghana Mahesh Sarma
   */
  @Test
  public void getSearchResultTest() {
    List<SearchResult> result = redditHelper
      .getSearchResult("test")
      .toCompletableFuture()
      .join();

    Assert.assertEquals("test", result.get(0).title);
    Assert.assertEquals("test", result.get(0).author);
    Assert.assertEquals("test", result.get(0).subreddit);
    Assert.assertEquals("test", result.get(0).selftext);
    Assert.assertEquals("test", result.get(0).author_fullname);
    Assert.assertEquals("test", result.get(0).author_is_blocked);
    Assert.assertEquals("test", result.get(0).author_premium);
  }

}
