package com.github.mwarc.realtimeauctions;

import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.platform.Verticle;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class AuctionService extends Verticle {

    @Override
    public void start() {
        HttpServer httpServer = createHttpServer();
        handleRequests(httpServer);
        createSockJsServer(httpServer);
        runHttpServer(httpServer);
    }

    private HttpServer createHttpServer() {
        return vertx.createHttpServer();
    }

    private void handleRequests(HttpServer httpServer) {
        RouteMatcher httpRouteMatcher = new RouteMatcher()
                .get("/", request -> request.response().sendFile("index.html"))
                .get(".*\\.(js)$", request -> request.response().sendFile("js/" + new File(request.path())))
                .get("/auctions/:id", request -> {
                    ConcurrentMap<String, String> auctionSharedData = vertx.sharedData().getMap(request.params().get("id"));

                    if (auctionSharedData.isEmpty()) {
                        request.response()
                            .putHeader("content-type", "application/json")
                            .setStatusCode(404)
                            .end();
                    } else {
                        request.response()
                            .putHeader("content-type", "application/json")
                            .setStatusCode(200)
                            .end(AuctionConverter.toJson(auctionSharedData));
                    }
                })
                .patch("/auctions/:id", request -> {
                    request.bodyHandler(body -> {
                        String auctionId = request.params().get("id");
                        Map<String, String> auctionRequestBody = AuctionConverter.toMap(body);
                        ConcurrentMap<String, String> auctionSharedData = vertx.sharedData().getMap(auctionId);

                        if (AuctionValidator.isBidPossible(auctionSharedData, auctionRequestBody)) {
                            auctionSharedData.put("id", auctionId);
                            auctionSharedData.put("price", auctionRequestBody.get("price"));
                            vertx.eventBus().publish("auction." + auctionId, AuctionConverter.toJson(auctionRequestBody));

                            request.response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(200)
                                .end();
                        } else {
                            request.response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(400)
                                .end();
                        }
                    });
                });
        httpServer.requestHandler(httpRouteMatcher);
    }

    private void createSockJsServer(HttpServer httpServer) {
        SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);
        sockJSServer.setHook(new EventBusHook());

        JsonArray inboundPermitted = new JsonArray();
        inboundPermitted.add(new JsonObject());
        JsonArray outboundPermitted = new JsonArray();
        outboundPermitted.add(new JsonObject());

        sockJSServer.bridge(
            new JsonObject().putString("prefix", "/ws"),
            inboundPermitted,
            outboundPermitted
        );
    }

    private void runHttpServer(HttpServer httpServer) {
        httpServer.listen(8080);
    }
}
