package testmodels;

import models.*;
import org.junit.*;
import play.mvc.*;
import play.libs.ws.*;

import java.util.List;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.math.BigInteger;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Collectors;
import play.libs.ws.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.apache.http.HttpStatus;
import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

import play.libs.Json;
import play.libs.ws.*;
import play.routing.RoutingDsl;
import play.server.Server;
import static play.mvc.Results.*;

import Reddit.RedditHelper;


public class QuerySearchResultTest {

  private WSClient ws;
  private Server server;
  private RedditHelper helper;
  private QuerySearchResult result;

  /**
   * Setup mock server to listen to http requests being made from tests to route '/submission'
   * and instantiate WSClient, QuerySearchResult, RedditHelper Java
   * @author Sumit Ramesh Bhiungade
   * @author Saghana Mahesh Sarma
   * @author Jai Sahni
   * */
  @Before
  public void Setup() {
    server = Server.forRouter((components) ->
      RoutingDsl.fromComponents(components)
      .GET("/submission")
      .routingTo(request -> {
        if (request.queryString().containsKey("subreddit")) {
          return ok().sendResource("subreddit.json");
        }

        if (request.queryString().containsKey("author")) {
          return ok().sendResource("user.json");
        }

        return ok().sendResource("general.json");
      }).build()
    );

    ws = play.test.WSTestClient.newClient(server.httpPort());

    result = new QuerySearchResult("test");
    helper = new RedditHelper(ws, "");
  }

  @Test
  public void setAnalyticsTest() {
    HashMap<String, Integer> map = new HashMap<>();
    map.put("test", 100);
    map.put("new", 10);
    result.setAnalytics(map);

    Assert.assertEquals("test", result.getAnalytics().get(0).getKey());
  }


  @Test
  public void getAnalyticsTest() {
    HashMap<String, Integer> map = new HashMap<>();
    map.put("test", 100);
    result.setAnalytics(map);

    Assert.assertEquals("test", result.getAnalytics().get(0).getKey());
    Assert.assertTrue(100 == result.getAnalytics().get(0).getValue());
  }

  @Test
  public void getAnalyticsDataTest() {
    HashMap<String, Integer> map = new HashMap<>();
    map.put("test", 100);
    result.setAnalytics(map);

    Assert.assertTrue(100 == result.getAnalyticsData().get("test"));
  }

  @Test
  public void getSearchTermTest() {
    Assert.assertEquals("test", result.getSearchTerm());
  }

  @Test
  public void setKeyTermDataTest() {
    List<SearchResult> sample = new ArrayList();
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));
    result.setKeyTermData(sample);
    Assert.assertEquals("test", sample.get(0).title);
  }

  @Test
  public void PopulateDataTest() {
    List<SearchResult> res = result
      .PopulateData(helper)
      .toCompletableFuture()
      .join();

    result.setKeyTermData(res.stream().limit(10).collect(Collectors.toList()));

    Assert.assertEquals(res.get(0).title, result.getData().get(0).title);
    Assert.assertEquals(res.get(0).author, result.getData().get(0).author);
    Assert.assertEquals(res.get(0).subreddit, result.getData().get(0).subreddit);
    Assert.assertEquals(res.get(0).selftext, result.getData().get(0).selftext);
  }

  @Test
  public void getDataTest() {
    List<SearchResult> sample = new ArrayList();
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));
    sample.add(new SearchResult("test", "test", "test", "test", "test", "false", "false"));

    var expectedOp = sample.stream().limit(10).collect(Collectors.toList());

    result.setKeyTermData(expectedOp);

    Assert.assertEquals(expectedOp, result.getData());
    Assert.assertTrue(result.getData().size() <= 10);
  }

}
