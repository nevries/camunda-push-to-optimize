package com.camunda.consulting.camunda_bpm_simulator_example.plugin;

import com.camunda.consulting.camunda_bpm_simulator_example.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableUpdate;
import org.camunda.bpm.engine.impl.cmd.optimize.OptimizeRunningHistoricTaskInstanceQueryCmd;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.db.entitymanager.DbEntityManager;
import org.camunda.bpm.engine.impl.history.event.*;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.camunda.bpm.engine.rest.dto.history.HistoricProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.optimize.OptimizeHistoricActivityInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.optimize.OptimizeHistoricVariableUpdateDto;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

@Slf4j
public class PushHistoryEventHandler implements HistoryEventHandler {
  private final PushService pushService;

  public PushHistoryEventHandler(PushService pushService) {
    this.pushService = pushService;
  }

  @Override
  public void handleEvent(HistoryEvent historyEvent) {
    if (historyEvent instanceof HistoricProcessInstanceEventEntity) {
      handle((HistoricProcessInstanceEventEntity) historyEvent);
    } else if (historyEvent instanceof HistoricVariableUpdateEventEntity) {
      // FIXME not working yet
      // handle((HistoricVariableUpdateEventEntity) historyEvent);
    } else if (historyEvent instanceof HistoricActivityInstanceEventEntity) {
      handle((HistoricActivityInstanceEventEntity)historyEvent);
    } else {
      log.warn("Not handled: " + historyEvent.toString());
    }
  }

  private void handle(HistoricActivityInstanceEventEntity historyEvent) {
    OptimizeHistoricActivityInstanceDto optimizeHistoricActivityInstanceDto = OptimizeHistoricActivityInstanceDto.fromHistoricActivityInstance(
        getProxy(historyEvent, HistoricActivityInstance.class));
    if (historyEvent.isEventOfType(HistoryEventTypes.ACTIVITY_INSTANCE_START)) {
      pushService.pushAiRunning(optimizeHistoricActivityInstanceDto);
    } else if (historyEvent.isEventOfType(HistoryEventTypes.ACTIVITY_INSTANCE_END)) {
      pushService.pushAiCompleted(optimizeHistoricActivityInstanceDto);
    }
  }

  private void handle(HistoricVariableUpdateEventEntity historyEvent) {
    OptimizeHistoricVariableUpdateDto.fromHistoricVariableUpdate(getProxy(historyEvent, HistoricVariableUpdate.class));
  }

  private void handle(HistoricProcessInstanceEventEntity processInstanceEventEntity) {
    HistoricProcessInstanceDto historicProcessInstanceDto = HistoricProcessInstanceDto.fromHistoricProcessInstance(
        getProxy(processInstanceEventEntity, HistoricProcessInstance.class));
    if (processInstanceEventEntity.isEventOfType(HistoryEventTypes.PROCESS_INSTANCE_START)) {
      pushService.pushPiRunning(historicProcessInstanceDto);
    } else if (processInstanceEventEntity.isEventOfType(HistoryEventTypes.PROCESS_INSTANCE_END)) {
      pushService.pushPiCompleted(historicProcessInstanceDto);
    }
  }


  @Override
  public void handleEvents(List<HistoryEvent> historyEvents) {
    historyEvents.stream().forEach(this::handleEvent);
  }

  private <T> T getProxy(Object original, Class<T> clazz) {
    return (T)
        Proxy.newProxyInstance(
            PushHistoryEventHandler.class.getClassLoader(),
            new Class[]{clazz},
            new ForwardInvocationHandler(original));
  }

  class ForwardInvocationHandler implements InvocationHandler {

    Object original;

    public ForwardInvocationHandler(Object original) {
      this.original = original;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Method originalMethod = original.getClass().getMethod(method.getName(), method.getParameterTypes());
      return originalMethod.invoke(original, args);
    }
  }
}
