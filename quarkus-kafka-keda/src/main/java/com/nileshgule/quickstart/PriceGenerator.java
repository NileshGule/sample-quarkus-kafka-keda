package com.nileshgule.quickstart;

import io.reactivex.Flowable;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * A bean producing random prices every 5 milliseconds. The prices are written to a
 * Kafka topic (prices). The Kafka configuration is specified in the application
 * configuration.
 */
@ApplicationScoped
public class PriceGenerator {

    private Random random = new Random();

    @Outgoing("generated-price")
    public Flowable<Integer> generate() {

        System.out.println("Generating random number");

            return Flowable.interval(5, TimeUnit.MILLISECONDS)
                .map(tick -> random.nextInt(100));

    }

}