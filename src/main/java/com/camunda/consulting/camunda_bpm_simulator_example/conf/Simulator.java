package com.camunda.consulting.camunda_bpm_simulator_example.conf;

import com.camunda.consulting.simulator.PayloadGenerator;
import com.camunda.consulting.simulator.SimulatorPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Simulator {
  /**
   * Makes the PayloadGenerator available in expressions and scripts.
   */
  @Bean
  public PayloadGenerator generator() {
    return new PayloadGenerator();
  }

  @Bean
  public SimulatorPlugin simulatorPlugin() {
    return new SimulatorPlugin();
  }
}
