package com.camunda.consulting.camunda_bpm_simulator_example.service;

import com.camunda.consulting.camunda_bpm_simulator_example.conf.ObjectMapperBuilder;
import com.camunda.consulting.camunda_bpm_simulator_example.conf.Pulsar;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.camunda.bpm.engine.rest.dto.history.HistoricProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.optimize.OptimizeHistoricActivityInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.optimize.OptimizeHistoricIdentityLinkLogDto;
import org.camunda.bpm.engine.rest.dto.history.optimize.OptimizeHistoricVariableUpdateDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDiagramDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;
import org.camunda.bpm.engine.rest.util.ProvidersUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Component
public class PushService {
  private PulsarClient client;
  private static final ObjectMapper objectMapper = ObjectMapperBuilder.build();

  @Autowired
  public PushService(PulsarClient client) {
    this.client = client;
  }

  public void pushProcDef(ProcessDefinitionDto processDefinitionDto) {
    push(processDefinitionDto, Pulsar.TOPIC_PD);
  }

  public void pushProcDefXML(ProcessDefinitionDiagramDto processDefinitionDiagramDto) {
    push(processDefinitionDiagramDto, Pulsar.TOPIC_PD_XML);
  }

  public void pushIdLinkLog(OptimizeHistoricIdentityLinkLogDto dto) {
    push(dto, Pulsar.TOPIC_ID_LINK_LOG);
  }

  public void pushAiCompleted(OptimizeHistoricActivityInstanceDto optimizeHistoricActivityInstanceDto) {
    push(optimizeHistoricActivityInstanceDto, Pulsar.TOPIC_AI_COMPLETED);
  }

  public void pushAiRunning(OptimizeHistoricActivityInstanceDto optimizeHistoricActivityInstanceDto) {
    push(optimizeHistoricActivityInstanceDto, Pulsar.TOPIC_AI_RUNNING);
  }

  public void pushVarUpdate(OptimizeHistoricVariableUpdateDto optimizeHistoricVariableUpdateDto) {
    push(optimizeHistoricVariableUpdateDto, Pulsar.TOPIC_VAR_UPDATE);
  }

  public void pushPiCompleted(HistoricProcessInstanceDto historicProcessInstanceDto) {
    push(historicProcessInstanceDto, Pulsar.TOPIC_PI_COMPLETED);
  }

  public void pushPiRunning(HistoricProcessInstanceDto historicProcessInstanceDto) {
    push(historicProcessInstanceDto, Pulsar.TOPIC_PI_RUNNING);
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
