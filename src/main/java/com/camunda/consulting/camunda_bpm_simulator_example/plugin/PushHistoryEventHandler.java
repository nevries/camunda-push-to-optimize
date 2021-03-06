package com.camunda.consulting.camunda_bpm_simulator_example.plugin;

import com.camunda.consulting.camunda_bpm_simulator_example.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricIdentityLinkLog;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.impl.history.event.*;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricDetailVariableInstanceUpdateEntity;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.camunda.bpm.engine.rest.dto.history.HistoricIdentityLinkLogDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricTaskInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.optimize.OptimizeHistoricActivityInstanceDto;
import org.camunda.bpm.engine.rest.dto.history.optimize.OptimizeHistoricIdentityLinkLogDto;
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
      handle((HistoricVariableUpdateEventEntity) historyEvent);
    } else if (historyEvent instanceof HistoricActivityInstanceEventEntity) {
      handle((HistoricActivityInstanceEventEntity) historyEvent);
    } else if (historyEvent instanceof HistoricTaskInstanceEventEntity) {
      handle((HistoricTaskInstanceEventEntity) historyEvent);
    } else if (historyEvent instanceof HistoricIdentityLinkLogEventEntity) {
      handle((HistoricIdentityLinkLogEventEntity) historyEvent);
    } else {
      log.debug("Not handled: " + historyEvent.toString());
    }
  }

  private void handle(HistoricTaskInstanceEventEntity historyEvent) {
    HistoricTaskInstanceDto historicTaskInstanceDto = HistoricTaskInstanceDto.fromHistoricTaskInstance(getProxy(historyEvent, HistoricTaskInstance.class));
    if (historyEvent.isEventOfType(HistoryEventTypes.TASK_INSTANCE_COMPLETE)) {
      pushService.pushCompleted(historicTaskInstanceDto);
    } else if (historyEvent.isEventOfType(HistoryEventTypes.TASK_INSTANCE_CREATE)) {
      pushService.pushRunning(historicTaskInstanceDto);
    }
  }

  private void handle(HistoricIdentityLinkLogEventEntity historyEvent) {
    OptimizeHistoricIdentityLinkLogDto dto = new OptimizeHistoricIdentityLinkLogDto();
    HistoricIdentityLinkLogDto.fromHistoricIdentityLink(dto, getProxy(historyEvent, HistoricIdentityLinkLog.class));
    dto.setProcessInstanceId(historyEvent.getProcessInstanceId());

    pushService.push(dto);
  }

  private void handle(HistoricActivityInstanceEventEntity historyEvent) {
    OptimizeHistoricActivityInstanceDto optimizeHistoricActivityInstanceDto = OptimizeHistoricActivityInstanceDto.fromHistoricActivityInstance(
        getProxy(historyEvent, HistoricActivityInstance.class));
    if (historyEvent.isEventOfType(HistoryEventTypes.ACTIVITY_INSTANCE_START)) {
      pushService.pushRunning(optimizeHistoricActivityInstanceDto);
    } else if (historyEvent.isEventOfType(HistoryEventTypes.ACTIVITY_INSTANCE_END)) {
      pushService.pushCompleted(optimizeHistoricActivityInstanceDto);
    }
  }

  private void handle(HistoricVariableUpdateEventEntity event) {
    if (event.getByteValue() != null) {
      // TODO: currently we abandon complex data types
      return;
    }

    HistoricDetailVariableInstanceUpdateEntity historicDetailVariableInstanceUpdateEntity = new HistoricDetailVariableInstanceUpdateEntity();

    historicDetailVariableInstanceUpdateEntity.setByteArrayId(null);
    historicDetailVariableInstanceUpdateEntity.setByteArrayValue(null);
    historicDetailVariableInstanceUpdateEntity.setSerializerName(null);
    historicDetailVariableInstanceUpdateEntity.setByteValue(null);

    historicDetailVariableInstanceUpdateEntity.setActivityInstanceId(event.getActivityInstanceId());
    historicDetailVariableInstanceUpdateEntity.setCaseDefinitionId(event.getCaseDefinitionId());
    historicDetailVariableInstanceUpdateEntity.setCaseDefinitionKey(event.getCaseDefinitionKey());
    historicDetailVariableInstanceUpdateEntity.setCaseDefinitionName(event.getCaseDefinitionName());
    historicDetailVariableInstanceUpdateEntity.setCaseExecutionId(event.getCaseExecutionId());
    historicDetailVariableInstanceUpdateEntity.setCaseInstanceId(event.getCaseInstanceId());
    historicDetailVariableInstanceUpdateEntity.setDoubleValue(event.getDoubleValue());
    historicDetailVariableInstanceUpdateEntity.setEventType(event.getEventType());
    historicDetailVariableInstanceUpdateEntity.setExecutionId(event.getExecutionId());
    historicDetailVariableInstanceUpdateEntity.setId(event.getId());
    historicDetailVariableInstanceUpdateEntity.setInitial(event.isInitial());
    historicDetailVariableInstanceUpdateEntity.setLongValue(event.getLongValue());
    historicDetailVariableInstanceUpdateEntity.setProcessDefinitionId(event.getProcessDefinitionId());
    historicDetailVariableInstanceUpdateEntity.setProcessDefinitionKey(event.getProcessDefinitionKey());
    historicDetailVariableInstanceUpdateEntity.setProcessDefinitionName(event.getProcessDefinitionName());
    historicDetailVariableInstanceUpdateEntity.setProcessDefinitionVersion(event.getProcessDefinitionVersion());
    historicDetailVariableInstanceUpdateEntity.setProcessInstanceId(event.getProcessInstanceId());
    historicDetailVariableInstanceUpdateEntity.setRemovalTime(event.getRemovalTime());
    historicDetailVariableInstanceUpdateEntity.setRevision(event.getRevision());
    historicDetailVariableInstanceUpdateEntity.setRootProcessInstanceId(event.getRootProcessInstanceId());
    historicDetailVariableInstanceUpdateEntity.setScopeActivityInstanceId(event.getScopeActivityInstanceId());
    historicDetailVariableInstanceUpdateEntity.setSequenceCounter(event.getSequenceCounter());
    historicDetailVariableInstanceUpdateEntity.setTaskId(event.getTaskId());
    historicDetailVariableInstanceUpdateEntity.setTenantId(event.getTenantId());
    historicDetailVariableInstanceUpdateEntity.setTextValue(event.getTextValue());
    historicDetailVariableInstanceUpdateEntity.setTextValue2(event.getTextValue2());
    historicDetailVariableInstanceUpdateEntity.setTimestamp(event.getTimestamp());
    historicDetailVariableInstanceUpdateEntity.setUserOperationId(event.getUserOperationId());
    historicDetailVariableInstanceUpdateEntity.setVariableInstanceId(event.getVariableInstanceId());
    historicDetailVariableInstanceUpdateEntity.setVariableName(event.getVariableName());

    OptimizeHistoricVariableUpdateDto optimizeHistoricVariableUpdateDto = OptimizeHistoricVariableUpdateDto.fromHistoricVariableUpdate(historicDetailVariableInstanceUpdateEntity);

    pushService.push(optimizeHistoricVariableUpdateDto);
  }

  // TODO: dirty counter
  public static long counterStart = 0;
  public static long counterEnd = 0;

  private void handle(HistoricProcessInstanceEventEntity processInstanceEventEntity) {
    processInstanceEventEntity.setProcessDefinitionVersion(1); // TODO: think about how to properly receive the version
    HistoricProcessInstanceDto historicProcessInstanceDto = HistoricProcessInstanceDto.fromHistoricProcessInstance(
        getProxy(processInstanceEventEntity, HistoricProcessInstance.class));
    if (processInstanceEventEntity.isEventOfType(HistoryEventTypes.PROCESS_INSTANCE_START)) {
      pushService.pushRunning(historicProcessInstanceDto);
      counterStart++;
    } else if (processInstanceEventEntity.isEventOfType(HistoryEventTypes.PROCESS_INSTANCE_END)) {
      pushService.pushCompleted(historicProcessInstanceDto);
      counterEnd++;
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
