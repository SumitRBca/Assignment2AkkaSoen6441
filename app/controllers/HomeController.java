package controllers;

import models.*;

import play.mvc.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;
import java.io.IOException;

import play.api.libs.json.Json;
import play.libs.ws.*;

/**
 * This controller contains an action to handle HTTP requests to the
 * application's home page.
 */
public class HomeController extends Controller implements WSBodyReadables, WSBodyWritables {
    private final WSClient ws;

    @Inject
    public HomeController(WSClient ws) {
        this.ws = ws;
    }

    /**
     * An action that renders an HTML page with a welcome message. The configuration
     * in the <code>routes</code> file means that this method will be called when
     * the application receives a <code>GET</code> request with a path of
     * <code>/</code>.
     */
    public CompletableFuture<Result> index(Http.Request request) {
        var sessionData = request.session().get("searchedTerms");
        System.out.println(sessionData);

        if (!sessionData.isPresent()) {
            return CompletableFuture.supplyAsync(() -> ok(views.html.index.render(new ArrayList<QuerySearchResult>())));
        } else {
            var post = Arrays.stream(sessionData.get().split(","))
            .filter(e -> !e.isEmpty()).parallel().map(CacheManager.GetCache(ws)::GetTrimmedSearchResult).collect(Collectors.toList());

            System.out.println("23");
            System.out.println(post);
            var arrPost = post.toArray(new CompletableFuture[post.size()]);

            System.out.println("33");
            System.out.println(arrPost);

            return CompletableFuture.allOf(arrPost).thenApply(v -> post.stream().map(CompletableFuture::join).collect(Collectors.toList())).thenApply(res -> {
                System.out.println("43");
                System.out.println(res);


                return ok(views.html.index.render(res));
            });
            // return CompletableFuture.allOf(post)
            //     .thenApply((List<QuerySearchResult> a) -> {
            //     return ok(views.html.index.render(a));
            // });
            // return CompletableFuture.allOf(Arrays
            //     .stream(sessionData.get().split(","))
            //     .filter(e -> !e.isEmpty())
            //     .parallel()
            //     .map(CacheManager.GetCache(ws)::GetTrimmedSearchResult)
            //     .collect(Collectors.toList())
            //     .toArray(new CompletableFuture[post.s])
            // ).thenApply((List<QuerySearchResult> a)-> {
            //     return ok(views.html.index.render(a));
            // });
        }
    }

    public Result search(String query, Http.Request request) {
        var currentSession = request.session().get("searchedTerms").orElse("");
        currentSession += "," + query;

        return redirect("/").addingToSession(request, "searchedTerms", currentSession);
    }

    public Result searchUser(String user, Http.Request request) {
        var result = CacheManager.GetCache(ws).GetUserInfo((user));
        return ok(views.html.profile.render(result));
    }

    public Result searchThread(String subreddit, Http.Request request)  {
        var result = CacheManager.GetCache(ws).GetThreadInfo((subreddit));
        return ok(views.html.thread.render(result));
    }

}
