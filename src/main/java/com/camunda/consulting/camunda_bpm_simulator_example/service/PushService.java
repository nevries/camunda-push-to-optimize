package com.camunda.consulting.camunda_bpm_simulator_example.service;

import com.camunda.consulting.camunda_bpm_simulator_example.conf.Serialization;
import com.camunda.consulting.camunda_bpm_simulator_example.conf.Pulsar;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.camunda.bpm.engine.rest.dto.history.HistoricProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricTaskInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.optimize.OptimizeHistoricActivityInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.optimize.OptimizeHistoricIdentityLinkLogDto;
import org.camunda.bpm.engine.rest.dto.history.optimize.OptimizeHistoricVariableUpdateDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDiagramDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Component
public class PushService {
  private PulsarClient client;
  private ObjectMapper objectMapper;

  @Autowired
  public PushService(PulsarClient client, ObjectMapper objectMapper) {
    this.client = client;
    this.objectMapper = objectMapper;
  }

  public void push(ProcessDefinitionDto processDefinitionDto) {
    push(processDefinitionDto, Pulsar.TOPIC_PD);
  }

  public void push(ProcessDefinitionDiagramDto processDefinitionDiagramDto) {
    push(processDefinitionDiagramDto, Pulsar.TOPIC_PD_XML);
  }

  public void push(OptimizeHistoricIdentityLinkLogDto dto) {
    push(dto, Pulsar.TOPIC_ID_LINK_LOG);
  }

  public void pushCompleted(OptimizeHistoricActivityInstanceDto optimizeHistoricActivityInstanceDto) {
    push(optimizeHistoricActivityInstanceDto, Pulsar.TOPIC_AI_COMPLETED);
  }

  public void pushRunning(OptimizeHistoricActivityInstanceDto optimizeHistoricActivityInstanceDto) {
    push(optimizeHistoricActivityInstanceDto, Pulsar.TOPIC_AI_RUNNING);
  }

  public void push(OptimizeHistoricVariableUpdateDto optimizeHistoricVariableUpdateDto) {
    push(optimizeHistoricVariableUpdateDto, Pulsar.TOPIC_VAR_UPDATE);
  }

  public void pushCompleted(HistoricProcessInstanceDto historicProcessInstanceDto) {
    push(historicProcessInstanceDto, Pulsar.TOPIC_PI_COMPLETED);
  }

  public void pushRunning(HistoricProcessInstanceDto historicProcessInstanceDto) {
    push(historicProcessInstanceDto, Pulsar.TOPIC_PI_RUNNING);
  }

  public void pushCompleted(HistoricTaskInstanceDto dto) {
    push(dto, Pulsar.TOPIC_TI_COMPLETED);
  }

  public void pushRunning(HistoricTaskInstanceDto dto) {
    push(dto, Pulsar.TOPIC_TI_RUNNING);
  }

  Map<String,Producer<String>> producerCache = new HashMap<>();

  @PreDestroy
  public void closeProducers() {
    producerCache.values().stream().forEach(producer -> {
      try {
        producer.close();
      } catch (PulsarClientException e) {
        // ignore
      }
    });
  }

  private void push(Object optimizeHistoricVariableUpdateDto, String topic) {
    try {
      Producer<String> producer = producerCache.get(topic);
      if (producer == null) {
        producer = client.newProducer(Schema.STRING).topic(topic).create();
        producerCache.put(topic, producer);
      }
      producer.send(objectMapper.writeValueAsString(optimizeHistoricVariableUpdateDto));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
