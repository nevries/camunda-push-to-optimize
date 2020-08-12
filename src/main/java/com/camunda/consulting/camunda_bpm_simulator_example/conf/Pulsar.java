package com.camunda.consulting.camunda_bpm_simulator_example.conf;

import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pulsar")
public class Pulsar {
  private String url;
  private String topic;

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
  public Producer<String> pulsarProducer(@Autowired PulsarClient pulsarClient) {
    try {
      return pulsarClient.newProducer(Schema.STRING).topic(getTopic()).create();
    } catch (PulsarClientException e) {
      throw new RuntimeException(e);
    }
  }

  @Bean
  Consumer<String> pulsarConsumer(@Autowired PulsarClient pulsarClient) {
    try {
      return pulsarClient.newConsumer(Schema.STRING).topic(getTopic()).subscriptionName("Test").subscribe();
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

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }
}
