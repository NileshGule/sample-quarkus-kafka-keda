package com.nileshgule.quickstart;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * A bean consuming data from the "prices" Kafka topic and applying some
 * conversion. The result is pushed to the "my-data-stream" stream which is an
 * in-memory stream.
 */
@ApplicationScoped
public class PriceConverter {

    private static final double CONVERSION_RATE = 0.88;

    @Incoming("prices")
    @Outgoing("my-data-stream")
    @Broadcast
    public double process(int priceInUsd) throws InterruptedException {
//    public double process(int priceInUsd)  {
        double convertedPrice = priceInUsd * CONVERSION_RATE;
        Thread.sleep(2000);
        System.out.println("converting the price " + priceInUsd + " converted price : " + convertedPrice);
        return priceInUsd * CONVERSION_RATE;
    }

    @Incoming("prices")
//    @Outgoing("my-data-stream")
//    @Broadcast
    public void processPrices(List<Integer> pricesInUsd) {
      pricesInUsd.forEach( price -> {
          try {
              process(price);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      });
    }


}