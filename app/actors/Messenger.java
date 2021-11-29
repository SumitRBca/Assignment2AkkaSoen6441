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
import akka.http.javadsl.model.HttpMessage;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.Materializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;

public class Messenger extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private ActorRef out;

    public Messenger(ActorRef out) {
        this.out = out;
    }

    public static Props props(ActorRef out) {
        return Props.create(Messenger.class, () -> new Messenger(out));
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

    private void onSendMessage(JsonNode jsonNode) {
        System.out.println("New");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
          .match(JsonNode.class, this::onSendMessage)
          .matchAny(o -> log.error("Received unknown message: {}", o.getClass()))
          .build();
    }
}
