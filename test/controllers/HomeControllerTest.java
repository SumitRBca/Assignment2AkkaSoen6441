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

public class HomeControllerTest extends WithApplication {

    private WSClient ws;
    private Server server;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Before
    public void Setup(){
        server = Server.forRouter((components) ->
            RoutingDsl.fromComponents(components)
            .GET("/submission")
            .routingTo(request -> {
                // System.out.println(request.queryString());
                if (request.queryString().containsKey("subreddit")) {
                    System.out.println("thread --" + request.queryString().get("subreddit"));
                    return ok().sendResource("subreddit.json");
                }

                if (request.queryString().containsKey("author")) {
                    System.out.println("author --" + request.queryString().get("author"));
                    return ok().sendResource("user.json");
                }

                System.out.println("general --\n");
                return ok().sendResource("general.json");
            })
            .build());
        ws = play.test.WSTestClient.newClient(server.httpPort());

        CacheManager.GetCache(ws, "");
    }

    // @After
    // public void tearDown() throws IOException {
    //     try {
    //         ws.close();
    //     } catch(Exception err) {
    //         err.printStackTrace();
    //     }finally {
    //         server.stop();
    //     }
    // }

    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testIndex_WithSession() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        request.session("searchedTerms", "test");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testIndex_WithSession_WithEmptySession() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        request.session("searchedTerms", "test");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void searchQuery() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/lookup/test");

        Result result = route(app, request);
        assertEquals(HttpStatus.SC_SEE_OTHER, result.status());
    }

    @Test
    public void searchThread() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/thread/test");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }


    @Test
    public void searchUser() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/user/test");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void searchStats() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/stats/test");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

}
