package com.camunda.consulting.camunda_bpm_simulator_example.conf;

import com.camunda.consulting.camunda_bpm_simulator_example.plugin.PushPlugin;
import com.camunda.consulting.camunda_bpm_simulator_example.service.PushService;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Plugin {

  @Bean
  public PushPlugin pushPlugin(@Autowired PushService pushService) {
    return new PushPlugin(pushService);
  }
}
