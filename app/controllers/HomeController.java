package controllers;

import models.*;
import actors.*;

import play.mvc.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;
import java.io.IOException;

import com.fasterxml.jackson.databind.*;
import play.api.libs.json.Json;
import play.libs.ws.*;
import play.libs.F;

import play.libs.streams.ActorFlow;
import play.mvc.*;
import akka.actor.*;
import akka.stream.*;
import akka.stream.javadsl.*;


/**
 * This controller contains an action to handle HTTP requests to the
 * application's home page.
 *  @author Saghana Mahesh Sarma
 *  @author Sumit Ramesh Bhiungade
 *  @author Jai Sahni
 */
public class HomeController extends Controller implements WSBodyReadables, WSBodyWritables {
    private final WSClient ws;
    private static String endpoint = "https://api.pushshift.io/reddit/search";

    private final ActorSystem actorSystem;
    private final Materializer materializer;

    @Inject
    public HomeController(WSClient ws, ActorSystem actorSystem, Materializer materializer) {
        this.ws = ws;
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }

    /**
     * An action that renders an HTML page with the index page . The configuration
     * in the <code>routes</code> file means that this method will be called when
     * the application receives a <code>GET</code> request with a path of
     * <code>/</code>.
     *   @author Saghana Mahesh Sarma
     *   @author Sumit Ramesh Bhiungade
     *   @author Jai Sahni
     */
    public CompletableFuture<Result> index(Http.Request request) {
        var sessionData = request.session().get("searchedTerms");

        if (!sessionData.isPresent()) {
            return CompletableFuture.supplyAsync(() -> ok(views.html.index.render(new ArrayList<QuerySearchResult>(), request)));
        } else {
            var post = Arrays
                .stream(sessionData.get().split(","))
                .filter(e -> !e.isEmpty())
                .map(k -> k.toLowerCase().trim())
                .distinct()
                .limit(10)
                .parallel()
                .map(CacheManager.GetCache(ws, endpoint)::GetTrimmedSearchResult)
                .collect(Collectors.toList());

            var arrPost = post.toArray(new CompletableFuture[post.size()]);
            return CompletableFuture
                .allOf(arrPost)
                .thenApply(v -> {
                    // reverse post order to show latest posts first >
                    Collections.reverse(post);
                    return post
                        .stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList());
                })
                .thenApply(res -> ok(views.html.index.render(res, request)));
        }
    }

    /**
     * This controller method will be used to take in a search term, perform a lookup, add that to cache and then redirect to index page.
     * @author Saghana Mahesh Sarma
     * @author Sumit Ramesh Bhiungade
     * @author Jai Sahni
     * @param query The search term for which we need to fetch posts from Reddit
     * @param request {@link play.mvc.Http.Request}
     * @return {@link Result}
     * */

    public Result search(String query, Http.Request request) {
        var currentSession = request.session().get("searchedTerms").orElse("");
        currentSession += "," + query;

        return redirect("/").addingToSession(request, "searchedTerms", currentSession);
    }

    /**
     * This controller method will be used to take in a user id and helps in retrieving owner information
     * and then return a new view
     * @author Saghana Mahesh Sarma
     * @param user The user id to get the details of the author of a reddit
     * @return {@link Result}
     * */

    public CompletableFuture<Result> searchUser(String user, Http.Request request) {
        return CacheManager
            .GetCache(ws, endpoint)
            .GetUserInfo((user))
            .thenApply((result) -> ok(views.html.profile.render(result, request)));
    }

    /**
     * This controller method will be used to take in a subreddit and helps in retrieving reddit posts under that particular subreddit
     * and then return a new view
     * @author Jai Sahni
     * @param subreddit The subreddit for which the reddits need to be fetched.
     * @return {@link Result}
     * */

    public CompletableFuture<Result> searchThread(String subreddit, Http.Request request) {
        return CacheManager
            .GetCache(ws, endpoint)
            .GetThreadInfo((subreddit))
            .thenApply((result) -> ok(views.html.thread.render(result, request)));
    }

    /**
     * This controller method will be used to perform word stats on a given search query .
     * and then return a new view
     * @author Sumit Ramesh Bhiungade
     * @param query The query on which the word stats need to be performed
     * @return {@link Result}
     * */

    public CompletableFuture<Result> searchStats(String query, Http.Request request) {
        return CacheManager
            .GetCache(ws, endpoint)
            .GetTrimmedSearchResult((query))
            .thenApply((result) -> ok(views.html.stats.render(result, request)));
    }

    private CompletionStage<F.Either<Result, Flow<JsonNode, JsonNode, ?>>> createActorFlow(Http.RequestHeader request) {
        return CompletableFuture.completedFuture(
            F.Either.Right(createFlowForActor())
        );
    }

    private Flow<JsonNode, JsonNode, ?> createFlowForActor() {
        return ActorFlow.actorRef(out -> Messenger.props(out), actorSystem, materializer);
    }

    public WebSocket socket() {
        System.out.println("Connected to socket!");
        return WebSocket.Json
            .acceptOrResult(this::createActorFlow);
    }
}
