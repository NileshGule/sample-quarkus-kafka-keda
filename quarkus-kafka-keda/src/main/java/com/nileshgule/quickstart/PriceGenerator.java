package com.nileshgule.quickstart;

import io.reactivex.Flowable;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * A bean producing random prices every 5 milliseconds. The prices are written
 * to a Kafka topic (prices). The Kafka configuration is specified in the
 * application configuration.
 */
@ApplicationScoped
public class PriceGenerator {

    private Random random = new Random();

    @Outgoing("generated-price1")
    public Flowable<String> generate1() {

        System.out.println("Generating random number");

        List<String> prices = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            prices.add("" + random.nextInt());
        }

        return Flowable
                .fromIterable(prices);
    }


}