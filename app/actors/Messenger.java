package actors;

import models.*;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.Http;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.stream.Materializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import Reddit.RedditHelper;
import play.libs.ws.*;
import play.libs.Json;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;

public class Messenger extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private RedditHelper helper;

    private ActorRef out;

    public Messenger(ActorRef out, WSClient ws, String endpoint) {
        this.out = out;
        this.helper = new RedditHelper(ws, endpoint);
    }

    public static Props props(ActorRef out, WSClient ws, String endpoint) {
        return Props.create(Messenger.class, () -> new Messenger(out, ws, endpoint));
    }

    @Override
    public void preStart() throws Exception {
        log.info(
            "Messenger actor started at {}",
            OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );
    }

    @Override
    public void postStop() throws Exception {
        log.info(
            "Messenger actor stopped at {}",
            OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );
    }

    private void onSendMessage(JsonNode jsonNode) throws JsonProcessingException {
        System.out.println("New");
        System.out.println(jsonNode);

        var type = jsonNode.get("type").asText();
        var query = jsonNode.get("query").asText();

        System.out.println("type: " + type);
        System.out.println("query: " + query);

        final ObjectNode response = Json.newObject();
        ObjectMapper objectMapper = new ObjectMapper();

        response.put("type", type);
        response.put("query", query);

        var value = this.helper
            .getSubredditPosts(query)
            .toCompletableFuture()
            .join();

        response.put("data", objectMapper.writeValueAsString(value));
        out.tell(response, self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
          .match(JsonNode.class, this::onSendMessage)
          .matchAny(o -> log.error("Received unknown message: {}", o.getClass()))
          .build();
    }
}
