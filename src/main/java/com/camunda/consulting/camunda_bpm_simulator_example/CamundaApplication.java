package com.camunda.consulting.camunda_bpm_simulator_example;

import com.camunda.consulting.camunda_bpm_simulator_example.plugin.PushPlugin;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.camunda.consulting.simulator.PayloadGenerator;
import com.camunda.consulting.simulator.SimulationExecutor;
import com.camunda.consulting.simulator.SimulatorPlugin;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableProcessApplication
@EnableScheduling
@EnableConfigurationProperties
public class CamundaApplication {

  PushPlugin pushPlugin;

  @Autowired
  public CamundaApplication(PushPlugin pushPlugin) {
    this.pushPlugin = pushPlugin;
  }

  public static void main(String... args) {
    SpringApplication.run(CamundaApplication.class, args);
  }

  @EventListener
  public void onPostDeploy(PostDeployEvent event) {
    pushPlugin.postDeploy(event);
  }

}
