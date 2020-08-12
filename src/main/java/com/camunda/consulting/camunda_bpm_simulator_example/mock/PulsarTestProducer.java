package com.camunda.consulting.camunda_bpm_simulator_example.mock;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("producer")
public class PulsarTestProducer {


  private final Producer<String> producer;

  @Autowired
  public PulsarTestProducer(@Qualifier("pulsarPiRunningProducer") Producer<String> producer) {
    log.info("Create test producer");
    this.producer = producer;
  }

  @Scheduled(fixedDelay = 3_000)
  public void produceStuff() {
    try {
      producer.send("Stuff");
    } catch (PulsarClientException e) {
      throw new RuntimeException(e);
    }
  }
}
