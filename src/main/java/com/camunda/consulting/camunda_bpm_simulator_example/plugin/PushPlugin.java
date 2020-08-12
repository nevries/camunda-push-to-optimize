package com.camunda.consulting.camunda_bpm_simulator_example.plugin;


import com.camunda.consulting.camunda_bpm_simulator_example.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;

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

}
