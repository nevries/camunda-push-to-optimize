package com.camunda.consulting.camunda_bpm_simulator_example;

import com.camunda.consulting.camunda_bpm_simulator_example.mock.PulsarTestConsumer;
import com.camunda.consulting.camunda_bpm_simulator_example.plugin.PushHistoryEventHandler;
import com.camunda.consulting.camunda_bpm_simulator_example.plugin.PushPlugin;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.Variables;
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
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableProcessApplication
@EnableScheduling
@EnableConfigurationProperties
@Slf4j
public class CamundaApplication {

  PushPlugin pushPlugin;
  RuntimeService runtimeService;
  PayloadGenerator payloadGenerator;

  @Autowired
  public CamundaApplication(PushPlugin pushPlugin, RuntimeService runtimeService, PayloadGenerator payloadGenerator) {
    this.pushPlugin = pushPlugin;
    this.runtimeService = runtimeService;
    this.payloadGenerator = payloadGenerator;
  }

  public static void main(String... args) {
    SpringApplication.run(CamundaApplication.class, args);
  }


  @EventListener
  public void onPostDeploy(PostDeployEvent event) {
    pushPlugin.postDeploy(event);
  }

  @Scheduled(fixedRate = 10_000)
  public void outputCounter() {
    System.err.println("Msgs received: " + PulsarTestConsumer.counter);
    System.err.println("Started PIs: " + PushHistoryEventHandler.counterStart);
    System.err.println("Finished PIs: " + PushHistoryEventHandler.counterEnd);
  }

  @PostConstruct
  public void makeSteam() {
    new Thread(
        () -> {
          try {
            Thread.sleep(10_000);
          } catch (InterruptedException e) {
            //
          }
          log.warn("STEAM!!!");
          while (true) {
            runtimeService.startProcessInstanceByKey("bathroomRoutine", Variables.putValue("camundo", payloadGenerator.firstname()));
            try {
              Thread.sleep(10);
            } catch (InterruptedException e) {
              //
            }
          }
        }
    ).start();
  }
}
