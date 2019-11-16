package com.nileshgule.quickstart;

import io.reactivex.Flowable;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
/**
 * A bean producing random string every 5 minutes. The string is picked up by the consumer.
 */
@ApplicationScoped
public class PriceGenerator {

    @Outgoing("producer")
    public Flowable<String> produce() {

        return Flowable.interval(100, TimeUnit.MILLISECONDS).map(tick -> RandomStringUtils.randomAlphabetic(64));
        

    }


}