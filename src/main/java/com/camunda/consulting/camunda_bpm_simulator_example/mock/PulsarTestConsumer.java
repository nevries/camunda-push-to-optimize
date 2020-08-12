package com.camunda.consulting.camunda_bpm_simulator_example.mock;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("consumer")
public class PulsarTestConsumer {
  @Autowired
  public PulsarTestConsumer(@Qualifier("pulsarPiRunningConsumer") Consumer<String> consumer) {
    log.info("Create test consumer");
    new Thread(() -> {
      while (true) {
        Message message = null;
        try {
          message = consumer.receive();
        } catch (PulsarClientException e) {
          throw new RuntimeException(e);
        }

        log.info("Got: " + new String(message.getData()));
      }
    }).start();
  }

}
