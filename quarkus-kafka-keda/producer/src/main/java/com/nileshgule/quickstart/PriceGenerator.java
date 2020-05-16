package com.nileshgule.quickstart;

import io.reactivex.Flowable;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PriceGenerator {

    @Outgoing("producer")
    public Flowable<String> produce() {

        System.out.println("Sending messages...");

        return Flowable.interval(200, TimeUnit.MILLISECONDS)
                .map(tick -> "sending new message " + RandomStringUtils.randomAlphabetic(64));
    }

}