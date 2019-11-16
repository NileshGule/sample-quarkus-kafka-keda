package com.nileshgule.quickstart;

import io.smallrye.reactive.messaging.annotations.Stream;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/prices")
public class PriceResource {

//    @Inject
//    @Stream("my-data-stream") Publisher<Double> prices;

    @Inject
    PriceGenerator priceGenerator;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

//    @GET
//    @Path("/stream")
//    @Produces(MediaType.SERVER_SENT_EVENTS)
//    public Publisher<Double> stream() {
//        System.out.println("Returning prices from Price Resource");
//        return prices;
//    }

    @POST
    @Path("/generatePrices")
//    public Response generatePrices(@PathParam("countOfMessages") int countOfMessages)
    public Response generatePrices()
    {
        System.out.print("Generating 1000 messages");

        priceGenerator.produce();

        return Response.ok().build();
    }


}