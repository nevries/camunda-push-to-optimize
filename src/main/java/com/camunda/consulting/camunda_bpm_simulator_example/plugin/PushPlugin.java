package com.camunda.consulting.camunda_bpm_simulator_example.plugin;


import com.camunda.consulting.camunda_bpm_simulator_example.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDiagramDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PushPlugin implements ProcessEnginePlugin {
  private PushService pushService;

  public PushPlugin(PushService pushService) {
    this.pushService = pushService;
  }


  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    log.info("Registering Push Plugin");
    List<HistoryEventHandler> handlers = processEngineConfiguration.getCustomHistoryEventHandlers();
    if (handlers==null) {
      handlers = new ArrayList<>();
      processEngineConfiguration.setCustomHistoryEventHandlers(handlers);
    }
    handlers.add(new PushHistoryEventHandler(pushService));
  }

  @Override
  public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

  }

  @Override
  public void postProcessEngineBuild(ProcessEngine processEngine) {

  }

  // TODO: currently called via Spring event, needs to be more robust
  // TODO: DMN missing
  public void postDeploy(PostDeployEvent event) {
    List<ProcessDefinition> processDefinitions = event.getProcessEngine().getRepositoryService().createProcessDefinitionQuery().list();
    for (ProcessDefinition processDefinition : processDefinitions) {
      ProcessDefinitionDto processDefinitionDto = ProcessDefinitionDto.fromProcessDefinition(processDefinition);
      pushService.push(processDefinitionDto);
      InputStream processModelIn = event.getProcessEngine().getRepositoryService().getProcessModel(processDefinition.getId());
      byte[] processModel = IoUtil.readInputStream(processModelIn, "processModelBpmn20Xml");
      try {
        ProcessDefinitionDiagramDto processDefinitionDiagramDto = ProcessDefinitionDiagramDto.create(processDefinition.getId(), new String(processModel, "UTF-8"));
        pushService.push(processDefinitionDiagramDto);
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
