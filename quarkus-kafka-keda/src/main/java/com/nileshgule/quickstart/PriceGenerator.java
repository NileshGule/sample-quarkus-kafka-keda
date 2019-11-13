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

//    @Outgoing("generated-price")
//    public Flowable<Integer> generate() {
//
//        System.out.println("Generating random number");
//
//        return Flowable.interval(5, TimeUnit.SECONDS).map(tick -> random.nextInt(100));
//
//    }

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

//    @Outgoing("processed-b")
//    public PublisherBuilder<Integer> filter(int countOfMessages) {
////        return input.filter(item -> item.length() > 4);
//
//        System.out.println("Generating random number");
//
//        List<Integer> prices = new ArrayList<>();
//
//        for (int i = 0; i < countOfMessages; i++) {
//            prices.add(random.nextInt());
//        }
//
//    }


}