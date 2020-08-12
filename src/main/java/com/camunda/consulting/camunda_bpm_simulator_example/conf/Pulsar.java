package com.camunda.consulting.camunda_bpm_simulator_example.conf;

import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pulsar")
public class Pulsar {
  public static String TOPIC_PI_RUNNING = "optimize:process-instance:running";
  public static String TOPIC_PI_COMPLETED = "optimize:process-instance:completed";
  public static String TOPIC_VAR_UPDATE = "optimize:variable-update";
  public static String TOPIC_AI_RUNNING = "optimize:activity-instance:running";
  public static String TOPIC_AI_COMPLETED = "optimize:activity-instance:completed";

  private String url;

  @Bean
  public PulsarClient pulsarClient() {
    try {
      return PulsarClient.builder()
          .serviceUrl(getUrl())
          .build();
    } catch (PulsarClientException e) {
      throw new RuntimeException(e);
    }
  }

  @Bean
  public Producer<String> pulsarPiRunningProducer(@Autowired PulsarClient pulsarClient) {
    try {
      return pulsarClient.newProducer(Schema.STRING).topic(TOPIC_PI_RUNNING).create();
    } catch (PulsarClientException e) {
      throw new RuntimeException(e);
    }
  }

  @Bean
  Consumer<String> pulsarPiRunningConsumer(@Autowired PulsarClient pulsarClient) {
    try {
      return pulsarClient.newConsumer(Schema.STRING)
          .topic(TOPIC_PI_RUNNING, TOPIC_AI_COMPLETED, TOPIC_AI_RUNNING, TOPIC_PI_COMPLETED, TOPIC_VAR_UPDATE)
          .subscriptionName("Test").subscribe();
    } catch (PulsarClientException e) {
      throw new RuntimeException(e);
    }
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}
