package testmodels;

import models.*;
import org.junit.*;
import play.mvc.*;
import play.libs.ws.*;

import java.util.List;
import java.io.IOException;
import java.util.HashMap;
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


public class CacheManagerTest {

  private WSClient ws;
  private Server server;

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
    })
    .build());

    ws = play.test.WSTestClient.newClient(server.httpPort());
  }

  @Test
  public void GetTrimmedSearchResult_Test() {
    QuerySearchResult querySearchResult = new QuerySearchResult("test");
    QuerySearchResult searchResult = CacheManager.GetCache(ws, "").GetTrimmedSearchResult("test").join();
    querySearchResult.setKeyTermData(searchResult.getData().stream().limit(10).collect(Collectors.toList()));

    // Check if the data from the api matches the data stored in instance
    Assert.assertEquals(querySearchResult.getData(), searchResult.getData());
    // Check if the response has a max of 10 items
    Assert.assertTrue(querySearchResult.getData().size() <= 10);
  }

  @Test
  public void GetThreadInfo_Test() {
    QuerySearchResult querySearchResult = new QuerySearchResult("test");
    QuerySearchResult searchResult = CacheManager.GetCache(ws, "").GetThreadInfo("test").join();
    querySearchResult.setKeyTermData(searchResult.getData().stream().limit(10).collect(Collectors.toList()));

    // Check if the data from the api matches the data stored in instance
    Assert.assertEquals(querySearchResult.getData(), searchResult.getData());
    // Check if the response has a max of 10 items
    Assert.assertTrue(querySearchResult.getData().size() <= 10);
  }

  @Test
  public void GetUserInfo_Test() {
    QuerySearchResult querySearchResult = new QuerySearchResult("test");
    QuerySearchResult searchResult = CacheManager.GetCache(ws, "").GetUserInfo("test").join();
    querySearchResult.setKeyTermData(searchResult.getData().stream().limit(10).collect(Collectors.toList()));

    // Check if the data from the api matches the data stored in instance
    Assert.assertEquals(querySearchResult.getData(), searchResult.getData());
    // Check if the response has a max of 10 items
    Assert.assertTrue(querySearchResult.getData().size() <= 10);
  }
}
