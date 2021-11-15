package controllers;

import org.junit.*;

import models.CacheManager;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

/**
 * This test file contains the test cases for HomeController
 * application's home page.
 */
public class HomeControllerTest extends WithApplication {

    private WSClient ws;
    private Server server;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    /**
     * Setup mock server to listen to http requests being made from tests to route '/submission'
     * and instantiate cache manager singleton
     * @author Sumit Ramesh Bhiungade
     * @author Saghana Mahesh Sarma
     * @author Jai Sahni
     * */
    @Before
    public void Setup(){
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

        CacheManager.GetCache(ws, "");
    }

    /**
     * This test method will be used to test  index.
     * @author Sumit Ramesh Bhiungade
     * @author Saghana Mahesh Sarma
     * @author Jai Sahni
     * */
    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * This test method will be used to test index with sessions.
     * @author Sumit Ramesh Bhiungade
     * @author Saghana Mahesh Sarma
     * @author Jai Sahni
     * */
    @Test
    public void testIndex_WithSession() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        request.session("searchedTerms", "test");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * This test method will be used to test  index with sessions and empty sessions.
     * @author Sumit Ramesh Bhiungade
     * @author Saghana Mahesh Sarma
     * @author Jai Sahni
     * */
    @Test
    public void testIndex_WithSession_WithEmptySession() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        request.session("searchedTerms", "test");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * This test method will be used to test search query for empty data.
     * @author Saghana Mahesh Sarma
     * */
    @Test
    public void withMultipleKeysAndEmptyDataTest() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        request.session("searchedTerms", "test,,db, ");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * This test method will be used to test search query.
     * @author Sumit Ramesh Bhiungade
     * @author Saghana Mahesh Sarma
     * @author Jai Sahni
     * */
    @Test
    public void searchQuery() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/lookup/test");

        Result result = route(app, request);
        assertEquals(HttpStatus.SC_SEE_OTHER, result.status());
    }

    /**
     * This test method will be used to test search threads.
     * @author Jai Sahni
     * */
    @Test
    public void searchThread() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/thread/test");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * This test method will be used to test the user
     * @author Sumit Ramesh Bhiungade
     * */
    @Test
    public void searchUser() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/user/test");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * This test method will be used to test word stats on a given search query .
     * @author Saghana Mahesh Sarma
     * */
    @Test
    public void searchStats() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/stats/test");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

}
